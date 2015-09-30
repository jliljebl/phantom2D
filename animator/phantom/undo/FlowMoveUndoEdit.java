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

import java.awt.Rectangle;
import java.util.Vector;

import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.flow.FlowBox;
import animator.phantom.gui.flow.FlowConnectionArrow;

//--- Undo/redo action for moving objects in flow.
public class FlowMoveUndoEdit extends PhantomUndoableEdit
{
	private Vector<FlowBox> selectedBoxes;
	private Vector<FlowBox> startPlaceBoxes;
	private Vector<FlowBox> endPlaceBoxes;
	private Vector<FlowConnectionArrow> arrowsConnectedToSelected;
	private Vector<Rectangle> startAreasForArrows;
	private Vector<Rectangle> endAreasForArrows;
	
	public FlowMoveUndoEdit( Vector<FlowBox> selectedBoxes, 
					Vector<FlowBox> startPlaceBoxes, 
					Vector<FlowConnectionArrow> arrowsConnectedToSelected,
					Vector <Rectangle> startAreasForArrows )
	{
		super();
		this.selectedBoxes = new Vector<FlowBox>( selectedBoxes );
		this.startPlaceBoxes = new Vector<FlowBox>(  startPlaceBoxes );
		this.arrowsConnectedToSelected = new Vector<FlowConnectionArrow>(  arrowsConnectedToSelected );
		this.startAreasForArrows = new Vector<Rectangle>( startAreasForArrows );

		endPlaceBoxes = new Vector<FlowBox>();
		for( FlowBox b : selectedBoxes )
			endPlaceBoxes.addElement( new FlowBox( b.getX(), b.getY() ) );

		endAreasForArrows = new Vector<Rectangle>();
		for( FlowConnectionArrow arrow : arrowsConnectedToSelected )
			endAreasForArrows.add( arrow.getArea() );
	}

	public void undo()
	{
		//--- Move boxes to new place
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			FlowBox b = selectedBoxes.elementAt( i );
			FlowBox startPlace = startPlaceBoxes.elementAt( i );
			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( b, b.getArea() );
			b.setPlace( startPlace.getX(), startPlace.getY() );
			GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( b );
			GUIComponents.renderFlowPanel.movingGraphics.remove( b );
		}
	
		//--- Move arrows to new place.
		for( int i = 0; i < endAreasForArrows.size(); i++ )
		{
			Rectangle area = endAreasForArrows.elementAt( i );
			FlowConnectionArrow arrow = arrowsConnectedToSelected.elementAt( i );
			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( arrow, area );
			arrow.updatePosition();
			GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( arrow );
			GUIComponents.renderFlowPanel.movingGraphics.remove( arrow );
		}

		GUIComponents.renderFlowPanel.repaint();
	}

	public void redo()
	{
		//--- Move boxes to new place
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			FlowBox b = selectedBoxes.elementAt( i );
			FlowBox endPlace = endPlaceBoxes.elementAt( i );
			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( b, b.getArea() );
			b.setPlace( endPlace.getX(), endPlace.getY() );
			GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( b );
			GUIComponents.renderFlowPanel.movingGraphics.remove( b );
		}
	
		//--- Move arrows to new place.
		for( int i = 0; i < startAreasForArrows.size(); i++ )
		{
			Rectangle area = startAreasForArrows.elementAt( i );
			FlowConnectionArrow arrow = arrowsConnectedToSelected.elementAt( i );
			GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( arrow, area );
			arrow.updatePosition();
			GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( arrow );
		}

		GUIComponents.renderFlowPanel.repaint();
	}

}//end class