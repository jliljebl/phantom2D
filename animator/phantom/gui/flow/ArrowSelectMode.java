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

//import osj.jliljeblad.animator.gui.EditorOperations;
import java.awt.event.MouseEvent;

public class ArrowSelectMode extends EditMode
{

	public ArrowSelectMode( FlowEditPanel editPanel, LookUpGrid lookUpGrid )
	{
		this.editPanel = editPanel;
	}

	//--- This is called after mouse press when arrow select begins if:
	//--- - parentBox != null
	//--- - startCp != null
	//--- - startCP.getArrow() != null 
	public void mousePressed( 	MouseEvent e,
					FlowBox selectedBox,
					FlowBoxConnectionPoint startCP  )
	{

			//--- Get selectedArrow from control point.
			FlowConnectionArrow selectedArrow = startCP.getArrow();
			//Flip selection state	
			selectedArrow.setConnectionPointsSelected( startCP.isActive()==false );
			//---Redraw source box and set to be updated.
			selectedArrow.getSourceCP().redrawIntoParentBox();

			//---Redraw targetbox box and set to be updated.
			selectedArrow.getTargetCP().redrawIntoParentBox();

			//--- Add arrow to selected arrows vector.
			editPanel.selectedArrows.addElement( selectedArrow );


	}

	//--- Called when edit in this mode is ongoing and mouse is dragged.
	public void mouseDragged( MouseEvent e )
	{
		//--- noop
	}

	//--- Called when edit in this mode is ongoing and mouse is button is released.
	public void mouseReleased( MouseEvent e )
	{
		//--- noop
	}

}//end class