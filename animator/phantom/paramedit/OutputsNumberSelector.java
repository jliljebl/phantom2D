package animator.phantom.paramedit;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import animator.phantom.renderer.ImageOperation;

/**
* A GUI component for selecting number of outputs a node has. This is added automatically to all plugins' edit panels.
*/
public class OutputsNumberSelector extends ParamComboBox implements ActionListener
{
	private ImageOperation iop;
	private static final String[] outputsNumber = { "1","2","3","4" };

	/**
	* Constructor with <code>ImageOperation</code>. For plugins this ImageOperation is created when 
	* <code>initPlugin( int type )</code> is called.
	* @param iop The <code>ImageOperation</code> that is wrapped in the <code>RenderNode</code> that has
	* its outputs number changed,
	*/
	public OutputsNumberSelector( ImageOperation iop )
	{
		this.iop = iop;
		initComponent( "Number of outputs", outputsNumber, this );
	}
	/**
	* Called when outputs number selection is changed.
	*/
	public void actionPerformed(ActionEvent e)
	{
		EditorInterface.outputsNumberChanged( iop,  getComboBox().getSelectedIndex() + 1 );
	}

}//end class
