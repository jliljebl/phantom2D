package animator.phantom.gui;

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
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.Border;

import animator.phantom.blender.Blender;
import animator.phantom.controller.RenderModeController;
import animator.phantom.gui.modals.MCheckBox;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MFileSelect;
import animator.phantom.gui.modals.MTextField;

public class RenderAreaPanel extends JPanel implements ActionListener
{
	public MCheckBox globalMB;
	public MComboBox passes;
	public MTextField angle;
	public MComboBox quality;
	public MComboBox size;
	public MComboBox outputFileType ;
	public MFileSelect tfs;
	public MTextField framename;
	public MComboBox pad;
	public MCheckBox overWrite;
	//public MTextField cacheShare;
	//public MTextField viewshare;

	private static final int BORDER_GAP = 5;

	private JButton render;
	
	public RenderAreaPanel()
	{
		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));

		add( Box.createRigidArea(new Dimension( 0, 6 ) ) );
		add( initPanel() );
		Border b1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border b2 = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 0, BORDER_GAP + 2, BORDER_GAP + 2, BORDER_GAP), b1 );
		
		setBorder( b2 );

		validate();
		repaint();
	}

	private JPanel initPanel()
	{
		File targetFolder = RenderModeController.getWriteFolder();
		tfs = new MFileSelect( "Folder", "Select folder for frames", 25, targetFolder, null );
		tfs.setType( JFileChooser.DIRECTORIES_ONLY );

		overWrite = new MCheckBox( "Overwrite", true );
		
		String[] fileTypes = new String[ 2 ];
		fileTypes[ 0 ] = "png";
		fileTypes[ 1 ] = "jpg";

		outputFileType = new MComboBox( "File type", fileTypes );

		String fname = RenderModeController.getFrameName();
		if( fname == null ) fname = "frame";
		framename = new MTextField( "Frame name", fname );
		framename.setTextFieldSize( 135 );
		String[] padOtps = { "3 digits","4 digits","5 digits", "No padding" };
		pad = new MComboBox( "Zero padding", padOtps );

 		String[] qualityOpts = { "normal","draft" };
		quality = new MComboBox( "Quality", qualityOpts );
		int currentQuality = RenderModeController.getWriteQuality();
		quality.setSelectedIndex( currentQuality );
		String[] sizeOtps = { "full","half","quarter" };
		size = new MComboBox( "Size", sizeOtps );

		globalMB = new MCheckBox( "Global motion blur", false );
		passes = new MComboBox( "Passes", Blender.selectablePassesOpts );
		
		int selIndex = 0;
		int origVal = Blender.getPasses();
		for( int i = 0; i < Blender.selectablePasses.length; i++ )
			if( Blender.selectablePasses[ i ] == origVal ) selIndex = i;
		passes.setSelectedIndex( selIndex );
		angle = new MTextField( "Shutter angle", new Integer( Blender.getShutterAngle() ) );
		angle.setTextFieldSize( 50 );

		render = new JButton( GUIResources.getIcon( GUIResources.renderLaunch ));
		GUIResources.prepareMediumMediumButton( render, this, "Render" );

		JPanel buttonRow = new JPanel();
		buttonRow.setLayout( new BoxLayout( buttonRow, BoxLayout.X_AXIS) );
		buttonRow.add( Box.createHorizontalGlue() );
		buttonRow.add( render );
		buttonRow.add( Box.createRigidArea( new Dimension( 6, 4 ) ) );
		
		JPanel p = new JPanel();
		p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS) );
		addEditorPad( p );
		p.add( tfs );
		addEditorPad( p );
		p.add( overWrite );
		addAreaPad( p );
		p.add( framename );
		addEditorPad( p );
		p.add( outputFileType );
		addEditorPad( p );
		p.add( pad );
		addAreaPad( p );
		p.add( quality );
		addEditorPad( p );
		p.add( size );
		addAreaPad( p );
		p.add( globalMB );
		addEditorPad( p );
		p.add( passes );
		addEditorPad( p );
		p.add( angle );
		p.add( Box.createVerticalGlue() );
		p.add( buttonRow );
		p.add( Box.createRigidArea( new Dimension( 6, 6 ) ) );
		return p;
	}

	private void addEditorPad( JPanel p)
	{
		p.add( Box.createRigidArea( new Dimension( 0, 4 ) ) );
	}
	private void addAreaPad( JPanel p )
	{
		p.add( Box.createRigidArea( new Dimension( 0, 25 ) ) );
	}

	public void actionPerformed( ActionEvent e )
	{
	
	}
}//end class 