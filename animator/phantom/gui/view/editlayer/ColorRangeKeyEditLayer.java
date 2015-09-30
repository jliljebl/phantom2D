package animator.phantom.gui.view.editlayer;

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

import animator.phantom.gui.view.ColorPickListener;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.renderer.ImageOperation;

public class ColorRangeKeyEditLayer extends ColorPickEditLayer
{
	public ColorRangeKeyEditLayer( 	ImageOperation iop,
					ColorPickListener listener )
	{
		super( iop, listener );
	}

	//--- Create a vector of hardcoded indexes of buttons and ask to display them, see ViewControlButtons
	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> indxs = new Vector<Integer>();
		indxs.add( new Integer( ViewControlButtons.PICK_COLOR_B ));
		indxs.add( new Integer( ViewControlButtons.PICK_FG_COLOR_B ));
		indxs.add( new Integer( ViewControlButtons.PICK_BG_COLOR_B ));
		buttons.setModeButtons( indxs );
	}

}//end class