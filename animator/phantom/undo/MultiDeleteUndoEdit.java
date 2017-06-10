package animator.phantom.undo;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.flow.FlowBox;
import animator.phantom.gui.flow.FlowBoxConnectionPoint;
import animator.phantom.gui.flow.FlowConnectionArrow;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

//--- Undo/redo action for multi object delete.
public class MultiDeleteUndoEdit extends PhantomUndoableEdit
{
	private Vector<RenderNode> removeNodes;
	private Vector<FlowBox> selectedBoxes;
	private Vector<FlowConnectionArrow> deleteArrows;

	public MultiDeleteUndoEdit(   	Vector<RenderNode> removeNodes,
					Vector<FlowBox> selectedBoxes,
					Vector<FlowConnectionArrow> deleteArrows )//all fooking delete arrows, selected and connected
	{
		super();
		this.removeNodes = removeNodes;
		this.selectedBoxes = selectedBoxes;
		this.deleteArrows = deleteArrows;
	}

	public void undo()
	{
		for( int i = 0; i < removeNodes.size(); i++ )
		{
			RenderNode node = removeNodes.elementAt( i );
			FlowBox box = selectedBoxes.elementAt( i );
			
			GUIComponents.renderFlowPanel.boxes.add( box );
			GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( box );
			ProjectController.getFlow().addNode( node );
			ImageOperation iop = node.getImageOperation();
			EditorsController.addLayerForIop( iop );
			PhantomUndoManager.newIOPCreated( iop );

			if( i ==  removeNodes.size() -1 ) UpdateController.editTargetIOPChanged( iop );
			if( i ==  removeNodes.size() -1 ) EditorsController.initKeyFrameEditor( iop );
		}

		for( int i = 0; i < deleteArrows.size(); i++ )
		{
			FlowConnectionArrow arrow = deleteArrows.elementAt( i );
			FlowBox startB = arrow.getSourceBox();
			FlowBox endB = arrow.getTargetBox();

			RenderNode source = startB.getRenderNode();
			RenderNode target = endB.getRenderNode();
	
			int sourceCPIndex = arrow.getSourceCP().getIndex();
			int targetCPIndex = arrow.getTargetCP().getIndex();
	
			FlowController.connectNodes( source, target, sourceCPIndex, targetCPIndex );
	
			arrow.getSourceCP().setArrow( arrow );
			arrow.getSourceCP().redrawIntoParentBox();
	
			arrow.getTargetCP().setArrow( arrow );
			arrow.getTargetCP().redrawIntoParentBox();
	
			GUIComponents.renderFlowPanel.arrows.add( arrow );
			GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( arrow );
		}

		GUIComponents.renderFlowPanel.repaint();
	}

	public void redo()
	{
		//--- If currently edited iop is among deleted, clear display.
		ImageOperation current = ParamEditController.getParamEditIOP();
		if( current != null ) ParamEditController.clearEditframe();

		//TimeLineController.removeClipsCorrespondingtoNodes( removeNodes );
		EditorsController.removeLayers( removeNodes );
		EditorsController.clearKFEditIfNecessery( removeNodes );

		//--- Remove boxes from panel.
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			FlowBox b = selectedBoxes.elementAt( i );
			GUIComponents.renderFlowPanel.boxes.remove( b );
			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( b, b.getArea() );
		}
		
		//--- Disconnect nodes that had removed arrows connected to them.
		for( int i = 0; i < deleteArrows.size(); i++ )
		{
			FlowConnectionArrow fa = deleteArrows.elementAt( i );
			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( fa ,fa.getArea() );

			FlowBox sourceBox = fa.getSourceBox();
			FlowBox targetBox = fa.getTargetBox();
			
			sourceBox.removeArrow( fa );
			targetBox.removeArrow( fa );

			RenderNode target = targetBox.getRenderNode();
			RenderNode source = sourceBox.getRenderNode();
			
			FlowBoxConnectionPoint sourceCP = fa.getSourceCP();
			FlowBoxConnectionPoint targetCP = fa.getTargetCP();

 			sourceCP.setActive( false );
			targetCP.setActive( false );

			sourceBox.redrawConnectionPoints();
			targetBox.redrawConnectionPoints();

			int sourceCIndex = sourceCP.getIndex();
			int targetCIndex = targetCP.getIndex();

			FlowController.disconnectNodes( source, target, sourceCIndex, targetCIndex);
		}
	
		//---- Delete the nodes from renderflow
		FlowController.deleteRenderNodes( removeNodes );
			
		GUIComponents.renderFlowPanel.arrows.removeAll( deleteArrows );
		GUIComponents.renderFlowPanel.selectedBoxes.removeAllElements();
		GUIComponents.renderFlowPanel.selectedArrows.removeAllElements();

		GUIComponents.renderFlowPanel.repaint();
	}

}//end class