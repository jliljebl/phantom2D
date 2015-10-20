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

import animator.phantom.controller.UpdateController;

public class BoxSelectionMode extends EditMode
{
	//--- Selected boxes.
	private Vector<FlowBox> selectedBoxes;
	//--- Selection box that is dragged to select flow boxes.
	private FlowSelectionBox selectionBox;

	public BoxSelectionMode( FlowEditPanel editPanel )
	{
		this.editPanel = editPanel;
		selectedBoxes = editPanel.selectedBoxes;
		selectionBox = new FlowSelectionBox();
	}

	public void mousePressed( 	MouseEvent e,
					FlowBox selectedBox,
					FlowBoxConnectionPoint cp  )
	{
		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();

		//--- Set selection box coordinates.
		selectionBox.setStartPos( mouseX, mouseY );
		selectionBox.setEndPos( mouseX, mouseY );

		//--- Set selection box as moving graphic.
		editPanel.movingGraphics.add( selectionBox );

	}

	public void mouseDragged(MouseEvent e)
	{
		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();

		//--- Set selection box end coordinates.
		selectionBox.setEndPos( mouseX, mouseY );
	}
	
	public void mouseReleased(MouseEvent e)
	{
		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		//--- Set selection box end pos.
		selectionBox.setEndPos( mouseX, mouseY );

		//--- Add selected boxes into selectedBoxes, set selected and draw into BG
		Vector<FlowBox> lastSelected = getBoxesInRect( selectionBox.getArea() );
		
		for( FlowBox b : lastSelected )
		{
			b.setSelected( b.isSelected() == false );

			//--- Switch selection states and add or remove box from selected Vector.
			//--- This is done so that area drag with ctrl can be used to 
			//--- deselect some selected boxes.
			if( b.isSelected() && !selectedBoxes.contains( b ) )
				selectedBoxes.addElement( b );

			if( !b.isSelected() && selectedBoxes.contains( b ) )
				selectedBoxes.removeElement( b );
		}
	
		editPanel.setMovingStopped();
	}

	//--- Returns all boxes that have area intersecting with area
	private Vector<FlowBox> getBoxesInRect( Rectangle rect )
	{
		Vector<FlowBox> selected = new Vector<FlowBox>();
		for( FlowBox box : editPanel.boxes )
		{
			if( box.rectIntersectsBox( rect ) ) selected.add( box );
		}
		return selected;
	}

}//end class