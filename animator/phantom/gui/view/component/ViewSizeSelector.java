package animator.phantom.gui.view.component;

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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.MovieRenderer;
import animator.phantom.gui.GUIResources;

public class ViewSizeSelector extends JPanel implements ActionListener
{
	private JComboBox<String> comboBox;
	private String[] options = {"200%","175%","150%","125%","100%","75%","50%","25%"};
	
	public ViewSizeSelector()
	{
		comboBox = new JComboBox<String>( options );
		comboBox.setSelectedIndex( 2 );
		comboBox.addActionListener( this );
		comboBox.setFont( GUIResources.TOP_LEVEL_COMBO_FONT );
		comboBox.setPreferredSize( new Dimension( 68, 24 ));
		

		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( comboBox );
		add( Box.createVerticalGlue() );
	}

	public void zoomIn()
	{
		int selected = comboBox.getSelectedIndex();
		selected -= 1;
		if( selected < 0 ) selected = 0;
		comboBox.setSelectedIndex( selected );
	}

	public void zoomOut()
	{
		int selected = comboBox.getSelectedIndex();
		selected += 1;
		if( selected > 7 ) selected = 7;
		comboBox.setSelectedIndex( selected );
	}

 	public void actionPerformed( ActionEvent e )
	{
		EditorsController.setViewSize( getMovieRendererSize( comboBox.getSelectedIndex() ) );
	}

 	private int getMovieRendererSize( int selectedIndex )
 	{
		int size = 0;
		if( selectedIndex == 0 ) size = MovieRenderer.DOUBLE_SIZE;
		if( selectedIndex == 1 ) size = MovieRenderer.ONE_THREE_QUARTER_SIZE;
		if( selectedIndex == 2 ) size = MovieRenderer.ONE_HALF_SIZE;
		if( selectedIndex== 3 ) size = MovieRenderer.ONE_QUARTER_SIZE;
		if( selectedIndex == 4 ) size = MovieRenderer.FULL_SIZE;
		if( selectedIndex == 5 ) size = MovieRenderer.THREE_QUARTER_SIZE;
		if( selectedIndex == 6 ) size = MovieRenderer.HALF_SIZE;
		if( selectedIndex == 7 ) size = MovieRenderer.QUARTER_SIZE;

		return size;
 	}

}//end class