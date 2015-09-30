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

import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.FloatParam;

/**
* A GUI editor component for setting value of a <code>FloatParam</code> using text input.
* Value is updated after user inputs value into text area and presses return.
*/
public class FloatNumberEditor extends JPanel implements PropertyChangeListener, UndoListener
{
	//--- AnimatedVlue edited with this component
	private FloatParam editValue;
	//--- GUI component used to edit value.
	private JFormattedTextField numberField;
	//--- Flag to stop infinite loops
	private boolean UNDO_CHANGE_CAUSED_VALUE_CHANGE = false;

	/**
	* Constructor with parameter to be edited and label text.
	* @param text Displayed name for editor and parameter.
	* @param editValue <code>FloatParam</code> that is edited with this editor.
	*/
	public FloatNumberEditor( String text, FloatParam editValue )
	{
		this( text, editValue, 7 );
	}
	/**
	* Constructor with parameter to be edited, label text and width.
	* @param text Displayed name for editor and parameter.
	* @param editValue <code>IntegerParam</code> that is edited with this editor.
	* @param columns Width of edit field in columns. 
	*/
	public FloatNumberEditor( String text, FloatParam editValue, int columns )
	{
		this.editValue = editValue;
		editValue.setParamName( text );

		JLabel textLabel  = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );

		numberField = new JFormattedTextField( NumberFormat.getNumberInstance() );
		numberField.setColumns( columns );
		numberField.setValue( editValue.get() );
		numberField.addPropertyChangeListener( "value", this );
		numberField.setMargin( new Insets(0, 2, 0, 0));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createHorizontalGlue() );

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( numberField );
		rightPanel.add( Box.createHorizontalGlue() );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );
 		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
	}
	/**
	* Called after undo has been done to update display.
	*/
	public void undoDone()
	{
		UNDO_CHANGE_CAUSED_VALUE_CHANGE = true;
		numberField.setValue( editValue.get() );
	}
	/**
	* Called after user edit action.
	*/
	public void propertyChange(PropertyChangeEvent e) 
	{
		if( UNDO_CHANGE_CAUSED_VALUE_CHANGE )
		{
			UNDO_CHANGE_CAUSED_VALUE_CHANGE = false;
			return;
		}

		float newValue = ( (Number) numberField.getValue()).floatValue();
		editValue.set( newValue );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		editValue.registerUndo();
	}

}//end class
