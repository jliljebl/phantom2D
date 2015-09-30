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

import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.flow.FlowBox;
import animator.phantom.gui.flow.FlowConnectionArrow;
import animator.phantom.renderer.RenderNode;

//--- Undo/redo action for arrow add.
public class ArrowAddUndoEdit extends PhantomUndoableEdit
{
	private FlowBox startB;
	private FlowBox endB;
	private FlowConnectionArrow drawArrow;
	
	public ArrowAddUndoEdit( FlowConnectionArrow drawArrow )
	{
		super();
		this.drawArrow = drawArrow;
		this.startB = drawArrow.getSourceBox();
		this.endB = drawArrow.getTargetBox();
	}

	public void undo()
	{
		FlowBox startB = drawArrow.getSourceBox();
		FlowBox endB = drawArrow.getTargetBox();

		RenderNode source = startB.getRenderNode();
		RenderNode target = endB.getRenderNode();

		int sourceCPIndex = drawArrow.getSourceCP().getIndex();
		int targetCPIndex = drawArrow.getTargetCP().getIndex();

		FlowController.disconnectNodes( source, target, sourceCPIndex, targetCPIndex );

		drawArrow.getSourceCP().setArrow( null );
		drawArrow.getTargetCP().setArrow( null );

		GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( drawArrow, drawArrow.getArea() );
		GUIComponents.renderFlowPanel.arrows.remove( drawArrow );
		GUIComponents.renderFlowPanel.selectedArrows.remove( drawArrow );
		GUIComponents.renderFlowPanel.movingGraphics.remove( drawArrow );

		GUIComponents.renderFlowPanel.repaint();
	}

	public void redo()
	{
		RenderNode source = startB.getRenderNode();
		RenderNode target = endB.getRenderNode();

		int sourceCPIndex = drawArrow.getSourceCP().getIndex();
		int targetCPIndex = drawArrow.getTargetCP().getIndex();

		FlowController.connectNodes( source, target, sourceCPIndex, targetCPIndex );

		drawArrow.getSourceCP().setArrow( drawArrow );
		drawArrow.getSourceCP().redrawIntoParentBox();

		drawArrow.getTargetCP().setArrow( drawArrow );
		drawArrow.getTargetCP().redrawIntoParentBox();

		GUIComponents.renderFlowPanel.arrows.add( drawArrow );
		GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( drawArrow );

		GUIComponents.renderFlowPanel.repaint();
	}

}//end class