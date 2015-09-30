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

import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.flow.FlowBox;
import animator.phantom.gui.flow.FlowConnectionArrow;
import animator.phantom.renderer.RenderNode;

//--- Undo/redo action for multi arrow add.
public class MultiArrowAddUndoEdit extends PhantomUndoableEdit
{
	private Vector<FlowConnectionArrow> addArrows;

	public MultiArrowAddUndoEdit(Vector<FlowConnectionArrow> addArrows )
	{
		super();
		this.addArrows = new Vector<FlowConnectionArrow>( addArrows );
	}

	public void undo()
	{
		for( FlowConnectionArrow arrow : addArrows )
		{
			FlowBox startB = arrow.getSourceBox();
			FlowBox endB = arrow.getTargetBox();

			RenderNode source = startB.getRenderNode();
			RenderNode target = endB.getRenderNode();

			int sourceCPIndex = arrow.getSourceCP().getIndex();
			int targetCPIndex = arrow.getTargetCP().getIndex();

			FlowController.disconnectNodes( source, target, sourceCPIndex, targetCPIndex );

			arrow.getSourceCP().setArrow( null );
			arrow.getTargetCP().setArrow( null );

			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( arrow, arrow.getArea() );
			GUIComponents.renderFlowPanel.arrows.remove( arrow );
			GUIComponents.renderFlowPanel.selectedArrows.remove( arrow );
			GUIComponents.renderFlowPanel.movingGraphics.remove( arrow );
		}

		GUIComponents.renderFlowPanel.repaint();
	}

	public void redo()
	{
		for( FlowConnectionArrow arrow : addArrows )
		{
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

}//end class