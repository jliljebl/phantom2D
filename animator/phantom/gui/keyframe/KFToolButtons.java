package animator.phantom.gui.keyframe;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import animator.phantom.controller.EditorsController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.gui.modals.MTextField;
import animator.phantom.renderer.param.AnimationKeyFrame;

public class KFToolButtons extends JPanel implements ActionListener
{
	private final String[] interpolations = { "linear","bezier" };

	private JButton zoomIn = new JButton( GUIResources.getIcon(  GUIResources.scaleZoomIn ) );
	private JButton zoomOut = new JButton( GUIResources.getIcon(  GUIResources.scaleZoomOut ) );
	private JButton addKF = new JButton( GUIResources.getIcon(  GUIResources.addKF ) );
	private JButton deleteKF = new JButton( GUIResources.getIcon(  GUIResources.deleteKF ) );
	private ImageIcon propsEnabled = GUIResources.getIcon( GUIResources.keyframeProperties );
	private ImageIcon propsDisabled =  GUIResources.getIcon( GUIResources.keyframePropertiesDisabled ); 
	private JButton kfProperties = new JButton( propsDisabled );

	public KFToolButtons()
	{
		GUIResources.prepareMediumButton( zoomIn, this, "Zoom in" );
		GUIResources.prepareMediumButton( zoomOut, this, "Zoom out" );
		GUIResources.prepareMediumButton( addKF, this, "Add keyframe" );
		GUIResources.prepareMediumButton( deleteKF, this, "Delete keyframe" );
		GUIResources.prepareMediumButton( kfProperties, this, "Selected keyframe properties" );

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.X_AXIS));
		p.add( addKF );
		p.add( deleteKF );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		p.add( zoomIn );
		p.add( zoomOut );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		p.add( kfProperties );

		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( p );
		add( Box.createVerticalGlue() );

		setKeyFrame( null );
	}

	public void setKeyFrame( AnimationKeyFrame kf )
	{
		if( kf == null )
			kfProperties.setIcon( propsDisabled );
		else
			kfProperties.setIcon( propsEnabled );
	}

	public void actionPerformed( ActionEvent e )
	{
		requestFocusInWindow();
	
		if( e.getSource() == zoomIn )
			EditorsController.zoomInKeyFrameEditor();
			
		if( e.getSource() == zoomOut )
			EditorsController.zoomOutKeyFrameEditor();
			
		if( e.getSource() == deleteKF )
			EditorsController.deleteKeyFrame();

		if( e.getSource() == addKF )
			EditorsController.addKeyFrame();

		if( e.getSource() == kfProperties )
		{
			if( kfProperties.getIcon() == propsDisabled ) return;//icon used as flag for button being enabled
		
			AnimationKeyFrame kf = EditorsController.getCurrentKeyFrame();
		
			// Build dialog
			final MComboBox leadingInterp = new MComboBox( "Interpolation", interpolations );
			final MTextField leadingTens = new MTextField( "Tension", new Float(0.3f ));
			leadingTens.setTextFieldSize( 50 );
			
			leadingInterp.setSelectedIndex( kf.getLeadingInterpolation() - 1 );// -1 to make values correspond with selection indexes
			leadingTens.setValue( kf.getLeadingTension() );
			
			MInputArea lArea = new MInputArea( "Leading" );
			lArea.add( leadingInterp );
			lArea.add( leadingTens );
			
			final MComboBox trailingInterp = new MComboBox( "Interpolation", interpolations );
			final MTextField trailingTens = new MTextField( "Tension", new Float( 0.3f ));
			trailingTens.setTextFieldSize( 50 );
			
			trailingInterp.setSelectedIndex( kf.getTrailingInterpolation() - 1 );// -1 to make values correspond with selection indexes
			trailingTens.setValue( kf.getTrailingTension() );
			
			MInputArea tArea = new MInputArea( "Trailing" );
			tArea.add( trailingInterp );
			tArea.add( trailingTens );
			
			final MInputPanel pPanel = new MInputPanel( "Keyframe Properties" );
			pPanel.add( lArea );
			pPanel.add( tArea );
			
			// Display
			int retVal = DialogUtils.showMultiInput( pPanel, 400, 250 );//blocks
			if( retVal != DialogUtils.OK_OPTION ) return;
		
			// Set values and repaint
			kf.setLeadingInterpolation( leadingInterp.getSelectedIndex() + 1 );
			kf.setTrailingInterpolation( trailingInterp.getSelectedIndex() + 1 );
			kf.setLeadingTension( leadingTens.getFloatValue() );
			kf.setTrailingTension( trailingTens.getFloatValue() );
			EditorsController.updateKFForValueChange();
		}
	}

}//end class