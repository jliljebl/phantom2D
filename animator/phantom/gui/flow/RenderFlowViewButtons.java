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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import animator.phantom.controller.FlowController;
import animator.phantom.gui.AnimatorFrame;
import animator.phantom.gui.GUIResources;

public class RenderFlowViewButtons extends JPanel implements ActionListener
{
	private JButton delete = new JButton( GUIResources.getIcon(GUIResources.deleteBoxes) );
	private JButton lineUpBoxes = new JButton( GUIResources.getIcon( GUIResources.lineUpBoxes) );
	private JButton connectBoxes = new JButton( GUIResources.getIcon(GUIResources.connectBoxes) );
	private JButton disConnectBoxes = new JButton( GUIResources.getIcon(GUIResources.disConnectBoxes) );
	private JToggleButton showGrid = new JToggleButton( GUIResources.getIcon( GUIResources.showGrid ) );
	
	public JLabel infoText = new JLabel();

	public RenderFlowViewButtons( AnimatorFrame frame )
	{
		GUIResources.prepareMediumButton( delete, this, "Delete IOPs from flow" );
		GUIResources.prepareMediumButton( lineUpBoxes, this, "Line up selected boxes  F5" );
		GUIResources.prepareMediumButton( connectBoxes, this, "Connect selected boxes  F6" );
		GUIResources.prepareMediumButton( disConnectBoxes, this, "Disconnect selected boxes  F7" );
		GUIResources.prepareMediumButton( showGrid, this, "Show Grid" );

		infoText.setFont( GUIResources.BASIC_FONT_10 );
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.X_AXIS));
		p.add( delete );
		p.add( lineUpBoxes );
		p.add( connectBoxes );
		p.add( disConnectBoxes );
		p.add( Box.createRigidArea( new Dimension(12, 10)));
		p.add( infoText );
		
		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( p );
		add( Box.createVerticalGlue() );
	}

	public void setInfoText( String text )
	{
		infoText.setText( text );
		repaint();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == delete )
			FlowController.deleteSelected();
		if( e.getSource() == lineUpBoxes )
			FlowController.arrangeBoxRow();
		if( e.getSource() == connectBoxes )
			FlowController.connectSelected();
		if( e.getSource() == disConnectBoxes )
			FlowController.disConnectSelected();
	}
	
}//end class