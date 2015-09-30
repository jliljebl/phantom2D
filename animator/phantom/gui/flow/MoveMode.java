package animator.phantom.gui.flow;

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
import java.awt.event.MouseEvent;
import java.util.Vector;

import animator.phantom.controller.GUIComponents;
import animator.phantom.undo.FlowMoveUndoEdit;
import animator.phantom.undo.PhantomUndoManager;

//--- Handles moving stuff in FlowEditPanel
public class MoveMode extends EditMode
{
	private FlowEditPanel editPanel;
	//--- Structure used to speed up graphics access.
	private LookUpGrid lookUpGrid;
	
	//--- Get mouse coordinates
	private int moveStartX;
	private int moveStartY;

	//--- Currently selectedBoxes.
	private Vector<FlowBox> selectedBoxes;
	//--- Boxes used to calculate positions while moving, mouse delta gives movement,
	//--- startPlaceBoxes.elementAt( i ) gives original place.
	private Vector<FlowBox> startPlaceBoxes;
	//--- Arrows that are coonected to selected boxes.
	private Vector<FlowConnectionArrow> arrowsConnectedToSelected;
	//--- Areas of arrows before they are moved. Used to clear them from grid.
	private Vector <Rectangle> startAreasForArrows;

	//--- Constructor is called after mouse press and edit begins.
	public MoveMode( FlowEditPanel editPanel, LookUpGrid lookUpGrid )
	{
		this.editPanel = editPanel;
		this.lookUpGrid = lookUpGrid;
	
		selectedBoxes = editPanel.selectedBoxes;

		startPlaceBoxes = new Vector<FlowBox>();
		arrowsConnectedToSelected = new Vector<FlowConnectionArrow>();
		startAreasForArrows = new Vector <Rectangle>();
	}


	//-------------------------------------------------- EDIT EVENTS
	public void mousePressed( 	MouseEvent e,
					FlowBox selectedBox,
					FlowBoxConnectionPoint cp  )
	{
		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();

		//--- Remenber move start point
		moveStartX = mouseX;
		moveStartY = mouseY;

		//--- Set boxes in moving state and create start place dummy box Vector
		for( FlowBox b : selectedBoxes )
		{
			//--- Add boxes moving objects.
			editPanel.movingGraphics.add( b );
			b.setIsMoving( true );
			//--- Remember start place and area.
			startPlaceBoxes.addElement( new FlowBox( b.getX(), b.getY() ) );
		}

		//--- Arrows that are connected to selected need to move( 1 end ) also.
		collectArrowsConnectedToSelected();
		//--- Add arrows moving objects.
		editPanel.movingGraphics.addAll( arrowsConnectedToSelected );
		//--- Get areas of arrows before they are moveds.
		//--- Set arrows in moving statw
		for( FlowConnectionArrow arrow : arrowsConnectedToSelected )
		{
			startAreasForArrows.add( arrow.getArea() );
			arrow.setIsMoving( true );
		}
	}

	public void mouseDragged( MouseEvent e )
	{
		//--- Calulate move.
		int deltaX = e.getX() - moveStartX;
		int deltaY = e.getY() -  moveStartY;

		//--- Move boxes
		FlowBox b = null;
		FlowBox startBox = null;
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			b = selectedBoxes.elementAt( i );
			startBox =  startPlaceBoxes.elementAt( i );
			b.setPlace( deltaX  + startBox.getX(), deltaY + startBox.getY() );
		}
		//--- Move arrows
		for( FlowConnectionArrow moveArrow : arrowsConnectedToSelected )
			moveArrow.updatePosition();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		//--- Move boxes to new place
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			FlowBox b = selectedBoxes.elementAt( i );
			forceLegalPlace( b );
			FlowBox startPlace = startPlaceBoxes.elementAt( i );
			b.setIsMoving( false );
			lookUpGrid.removeFlowGraphicFromGridInArea( b, startPlace.getArea() );
			lookUpGrid.addFlowGraphicToGrid( b );
		}

		//--- Move arrows because box places might have been changed by legalization
		for( FlowConnectionArrow moveArrow : arrowsConnectedToSelected )
			moveArrow.updatePosition();

		//--- Move arrows to new place.
		for( int i = 0; i < startAreasForArrows.size(); i++ )
		{
			Rectangle area = startAreasForArrows.elementAt( i );
			FlowConnectionArrow arrow = arrowsConnectedToSelected.elementAt( i );
			lookUpGrid.removeFlowGraphicFromGridInArea( arrow, area );
			lookUpGrid.addFlowGraphicToGrid( arrow );
			arrow.setIsMoving( false );
		}

		FlowMoveUndoEdit undoEdit = new FlowMoveUndoEdit( selectedBoxes, startPlaceBoxes,
							arrowsConnectedToSelected, startAreasForArrows );
		PhantomUndoManager.addUndoEdit( undoEdit );

		editPanel.setMovingStopped();
	}


	//-------------------------------------------------- PRIVATE METHODS
	//--- Fills global vector with all arrows that are connected to selected boxes.
	private void collectArrowsConnectedToSelected()
	{	
		for( FlowBox b : selectedBoxes )
		{
			Vector<FlowConnectionArrow> arrows = b.getAllArrows();

			for( FlowConnectionArrow arrow : arrows )
				if( !arrowsConnectedToSelected.contains( arrow ) )
					arrowsConnectedToSelected.add( arrow );

		}
	}

	private static void forceLegalPlace( FlowBox b )
	{
		int x = b.getX();
		int y = b.getY();
		int width = GUIComponents.renderFlowPanel.getWidth();
		int height = GUIComponents.renderFlowPanel.getHeight();

		if( x < 0 )
			x = 0;
		if( y < 0 )
			y = 0;
		if( x > (width - FlowBox.width ) )
			x = width - FlowBox.width;
		if( y > (height - FlowBox.height ) )
			y = height - FlowBox.height;

		b.setPlace( x, y ); 
	}
	
}//end class