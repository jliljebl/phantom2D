package animator.phantom.gui.modals.render;

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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import animator.phantom.controller.MenuActions;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.RenderModeController;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.PHButtonFactory;
import animator.phantom.gui.PHProgressBar;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MCheckBox;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MFileSelect;
import animator.phantom.gui.modals.MTextField;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;

//--- GUI panel for render info setting and render progress display.
public class RenderWindowPanel extends JPanel implements ActionListener
{
	private static final int TOP_GAP = 5;
	private static final int SIDE_GAP = 10;
	private static final int BUTTONS_GAP = 20;
	private static final int SET_BUTTONS_GAP = 10;
	private static final int BOTTOM_GAP = 20;
	private static final int INFO_PROG_GAP = 6;
	private static final int PROG_BUTTONS_GAP = 6;
	private static final int BORDER_GAP = 10;
	private static final int RANGE_GAP = 40;
	private static final int RANGE_PAD = 350;
	private static final int WIDTH = 600;
	private static final int SETTINGS_ROW_GAP = 8;
	private static final int PROG_HEIGHT = 15;

	private JButton setFrameSettins;
	private JButton setRenderSettins;
	public JButton render;//--- public: state set from WriteRenderThread
	public JButton stop;//--- public: state set from WriteRenderThread
	public JButton exit;//--- public: state set from WriteRenderThread

	private JLabel lastTime = new JLabel("");
	private JLabel rendering = new JLabel("Not rendering");
	private JLabel elapsed = new JLabel("");
	private JLabel proj = new JLabel();

	private JTextField from = new JTextField();
	private JTextField to = new JTextField();

	public PHProgressBar pbar = new PHProgressBar();

	private int framesCount;
	private int currentFrame;

	private RenderWindow window;

	public RenderWindowPanel( RenderWindow window )
	{
		this.window = window;

		render = PHButtonFactory.getButton( "Render" );//--- public: state set from WriteRenderThread
		stop = PHButtonFactory.getButton( "Stop" );//--- public: state set from WriteRenderThread
		exit = PHButtonFactory.getButton( "Exit" );//--- public: state set from WriteRenderThread

		setFrameSettins =  PHButtonFactory.getButton( "Set" );
		setRenderSettins = PHButtonFactory.getButton( "Set" );

		proj.setText( "Project: " + ProjectController.getName() );

		int fps =  ProjectController.getFramesPerSecond();
		from.setText(  TimeLineDisplayPanel.parseTimeCodeString( RenderModeController.writeRangeStart, 6, fps ));
		to.setText(  TimeLineDisplayPanel.parseTimeCodeString( RenderModeController.writeRangeEnd, 6, fps ));

		stop.setEnabled( false );

		lastTime.setHorizontalAlignment( SwingConstants.LEFT );
		rendering.setHorizontalAlignment( SwingConstants.LEFT );
		elapsed.setHorizontalAlignment( SwingConstants.LEFT );

		setFrameSettins.setFont( GUIResources.BASIC_FONT_12 );
		setRenderSettins.setFont( GUIResources.BASIC_FONT_12 );
		render.setFont( GUIResources.BASIC_FONT_12 );
		stop.setFont( GUIResources.BASIC_FONT_12 );
		exit.setFont( GUIResources.BASIC_FONT_12 );
		lastTime.setFont( GUIResources.BASIC_FONT_12 );
		rendering.setFont( GUIResources.BASIC_FONT_12 );
		elapsed.setFont( GUIResources.BASIC_FONT_12 );
		proj.setFont( GUIResources.BASIC_FONT_12 );

		render.addActionListener( this );
		stop.addActionListener( this );
		exit.addActionListener( this );
		setFrameSettins.addActionListener( this );
		setRenderSettins.addActionListener( this );

		//--- Save settings

		File targetFolder = RenderModeController.getWriteFolder();
		MFileSelect tfs = new MFileSelect( "Render Output Folder", "Select folder for frames", 25, targetFolder, null );
		tfs.setType( JFileChooser.DIRECTORIES_ONLY );
		
		String fname = RenderModeController.getFrameName();
		if( fname == null ) fname = "frame";
		MTextField framename = new MTextField( "Frame name", 75, fname );
		
		String[] padOtps = { "3 digits","4 digits","5 digits", "no padding" };
		MComboBox pad = new MComboBox( "Zero padding", 75, padOtps );
		
		MCheckBox overWrite = new MCheckBox( "Overwrite without warning", true );


		//--- Set render values.
		/*
		//RenderModeController.setOutFrameType( (String) outputFileType.getValue() );
		RenderModeController.setFrameName( framename.getStringValue() );
		RenderModeController.setWriteFolder( tfs.getSelectedFile() );
		int zpad = pad.getSelectedIndex();
		if( zpad == 3 ) zpad = -1;
		else zpad = zpad + 3;
		RenderModeController.setZeroPadding( zpad );
		*/


		JPanel setpanel = new JPanel();
		setpanel.setLayout( new BoxLayout( setpanel, BoxLayout.Y_AXIS) );
		setpanel.add( Box.createRigidArea( new Dimension(0,10) ) );
		setpanel.add( tfs );
		setpanel.add( Box.createRigidArea( new Dimension(0,SETTINGS_ROW_GAP) ) );
		setpanel.add( framename );
		setpanel.add( Box.createRigidArea( new Dimension(0,SETTINGS_ROW_GAP) ) );
		setpanel.add( pad );
		setpanel.add( Box.createRigidArea( new Dimension(0,SETTINGS_ROW_GAP) ) );
		setpanel.add( overWrite );

		Border b1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border b2 = BorderFactory.createTitledBorder( b1, "Settings");
		Border b3 = BorderFactory.createCompoundBorder( b2, BorderFactory.createEmptyBorder( 0, BORDER_GAP, BORDER_GAP, BORDER_GAP) );	
		setpanel.setBorder( b3 );

		JPanel top = new JPanel();
		top.setLayout( new BoxLayout( top, BoxLayout.X_AXIS) );
		top.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		top.add( setpanel );
		top.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		JPanel rangePanel = new JPanel();
		rangePanel.setLayout( new BoxLayout( rangePanel, BoxLayout.X_AXIS) );
		JLabel rlfrom = new JLabel( "From:" );
		rlfrom.setFont( GUIResources.BASIC_FONT_12 );
		rangePanel.add( rlfrom );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( from );
		rangePanel.add( Box.createRigidArea( new Dimension(RANGE_GAP,0)));
		JLabel rlto = new JLabel( "To:" );
		rlto.setFont( GUIResources.BASIC_FONT_12 );
		rangePanel.add( rlto );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( to );
		rangePanel.add( Box.createRigidArea( new Dimension(RANGE_PAD,0)));
		rangePanel.add( Box.createHorizontalGlue() );
		Border m1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border m2 = BorderFactory.createTitledBorder( m1, "Range");
		Border m3 = BorderFactory.createCompoundBorder( m2, BorderFactory.createEmptyBorder( 0, BORDER_GAP, BORDER_GAP, BORDER_GAP) );
		rangePanel.setBorder( m3 );

		JPanel middle = new JPanel();
		middle.setLayout( new BoxLayout( middle, BoxLayout.X_AXIS) );
		middle.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		middle.add( rangePanel );
		middle.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		JPanel renderedP = new JPanel();
		JPanel lastTimeP = new JPanel();
		JPanel elapsedTimeP = new JPanel();
		renderedP.setLayout( new BoxLayout( renderedP, BoxLayout.X_AXIS) );
		renderedP.add( rendering );
		renderedP.add( Box.createHorizontalGlue() );
		lastTimeP.setLayout( new BoxLayout( lastTimeP, BoxLayout.X_AXIS) );
		lastTimeP.add( lastTime );
		lastTimeP.add( Box.createHorizontalGlue() );
		elapsedTimeP.setLayout( new BoxLayout( elapsedTimeP, BoxLayout.X_AXIS) );
		elapsedTimeP.add( elapsed );
		elapsedTimeP.add( Box.createHorizontalGlue() );

		JPanel gridP = new JPanel();
		gridP.setLayout( new GridLayout( 1, 3 ));
		gridP.add( renderedP );
		gridP.add( lastTimeP );
		gridP.add( elapsedTimeP );

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout( new BoxLayout( infoPanel, BoxLayout.X_AXIS) );
		infoPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		infoPanel.add( gridP );
		infoPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		JPanel progPanel = new JPanel();
		progPanel.setLayout( new BoxLayout( progPanel, BoxLayout.X_AXIS) );
		progPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		progPanel.add( pbar );
		progPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		pbar.setPreferredSize( new Dimension( WIDTH, PROG_HEIGHT ));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout( new BoxLayout( buttonsPanel, BoxLayout.X_AXIS) );
		buttonsPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		buttonsPanel.add( proj );
		buttonsPanel.add( Box.createHorizontalGlue() );
		buttonsPanel.add( stop );
		buttonsPanel.add( Box.createRigidArea( new Dimension(BUTTONS_GAP,0) ) );
		buttonsPanel.add( render );
		buttonsPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		buttonsPanel.add( exit );
		buttonsPanel.add( Box.createRigidArea( new Dimension(BUTTONS_GAP,0) ) );

		JPanel rendPanel = new JPanel();
		rendPanel.setLayout( new BoxLayout( rendPanel, BoxLayout.Y_AXIS) );
		rendPanel.add( infoPanel );
		rendPanel.add( Box.createRigidArea( new Dimension( 0, INFO_PROG_GAP )));
		rendPanel.add( progPanel );
		rendPanel.add( Box.createRigidArea( new Dimension( 0, PROG_BUTTONS_GAP )));
		rendPanel.add( buttonsPanel );
		Border b4 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border b5 = BorderFactory.createTitledBorder( b4, "Render");
		Border b6 = BorderFactory.createCompoundBorder( b5, BorderFactory.createEmptyBorder( 0, BORDER_GAP, BORDER_GAP, BORDER_GAP) );	
		rendPanel.setBorder( b6 );

		JPanel bottom = new JPanel();
		bottom.setLayout( new BoxLayout( bottom, BoxLayout.X_AXIS) );
		bottom.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		bottom.add( rendPanel );
		bottom.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( Box.createRigidArea( new Dimension( 0,TOP_GAP )));
		add( top );
		add( middle );
		add( bottom );
		add( Box.createRigidArea( new Dimension( 0,BOTTOM_GAP )));
	}

	public void renderStart( int framesCount )
	{
		this.framesCount = framesCount;
		this.currentFrame = 1;
		displayRenderingOf();

		repaint();
	}

	public void nextFrame( int totalMillis, int lastMillis )
	{
		currentFrame++;
		displayRenderingOf();
		lastTime.setText( "Frame time:" + createTimeString( lastMillis, true ) );
		elapsed.setText( "Elapsed time:" + createTimeString( totalMillis, false ) );
		pbar.advanceOne();

		repaint();
	}

	private void displayRenderingOf()
	{
		rendering.setText( "Rendering " + Integer.toString( currentFrame ) + " of " + Integer.toString( framesCount ) );
	}

	/*
	public void clearLastElaps()
	{
		lastTime.setText( " " );
		elapsed.setText( " " );
	}

	public void displayMovieFileSize( int size )
	{
		rendering.setText( "Encoding clip " +  Integer.toString( size ) + " B");
	}
	*/
	public void renderingDone()
	{
		rendering.setText( "Rendering done");
		render.setEnabled( true );
		stop.setEnabled( false );
		exit.setEnabled( true );
	}
	
	public static String createTimeString( int millis, boolean fractions )
	{
		int seconds = millis / 1000;
		int minutes = seconds / 60;
		int hours = minutes / 60;

		seconds = seconds - (minutes * 60);
		minutes = minutes - (hours * 60);
	
		//--- Create time code String
		StringBuilder sb = new StringBuilder();
		if( hours > 0 )
		{
			sb.append( hours );
			sb.append( "h " );
		}

		if( minutes > 0 )
		{
			sb.append( minutes );
			sb.append( "m " );
		}

		sb.append( seconds );
		if( !fractions )
		{
			sb.append( "s" );
			return sb.toString();
		}

		sb.append( "." );
		sb.append( millis % 1000 );
		sb.append( "s" );
		return sb.toString();
	}

	public int parseTCString( String tc )
	{
		String numberString = getNumberString( tc );
		System.out.println(numberString);
		Vector<String> tokens = getTimeTokens( numberString );
		
		int frames = 0;
		try
		{
			int c = 0;
			for( int i = tokens.size() -1; i >= 0; i-- )
			{
				int val = Integer.parseInt( tokens.elementAt( i ) );
				if( c == 0 )
				{
					if (val >= ProjectController.getFramesPerSecond())
						return -1;
					frames += val;//f
				}
				if( c == 1 ) 
				{
					if( val >= 60 )
						return -1;
					frames += ProjectController.getFramesPerSecond() * val;//s
				}
				if( c == 2 )
				{
					if( val >= 60 )
						return -1;
					frames += ProjectController.getFramesPerSecond() * val * 60;//m
				}
				if( c == 3 ) frames += ProjectController.getFramesPerSecond() * val * 60 * 60;//h
				c++;
			}
		}
		catch( Exception e )
		{
			return -1;
		}
		
		return frames;
	}

	//private String getCommaSeparated

	private String getNumberString( String tc )
	{
		StringTokenizer st = new StringTokenizer( tc, ":" );
		String numberString = new String();

		while( st.hasMoreTokens() )
		{
			String token = st.nextToken();
			System.out.println(token);
			numberString = numberString + token;
		}

		int missingZeros = 8 - numberString.length();
		String addZeros = new String();
		for( int i = 0; i <  missingZeros; i++ )
			addZeros = addZeros + "0";

		return addZeros + numberString;
	}

	//--- assumes 8 char long String
	private Vector<String> getTimeTokens( String numberString )
	{
		Vector<String> tokens = new Vector<String>();
		tokens.add( numberString.substring(0, 2) );
		tokens.add( numberString.substring(2, 4) );
		tokens.add( numberString.substring(4, 6) );
		tokens.add( numberString.substring(6, 8) );
		return tokens;
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == render )
		{
			/*
			if( CodecController.encProg == null &&
			    CodecController.movieOn )
			{
				UserActions.displayNoEncoderInfo();
			}

			if( RenderModeController.getTargetFolder() == null )
			{
				String[] options = { "Ok" };
				String[] bLines = { "No folder selected for frames" };
				String[] tLines = { "Select folder for frames:", "Select Frames Output Settins -> Folder for frames" };
				DialogUtils.showTwoTextStyleDialog( JOptionPane.WARNING_MESSAGE, "Render info", options, bLines, tLines, window );
				return;
			}

			int start = parseTCString( from.getText() );
			int end = parseTCString( to.getText() );
			if( start < 0 || end < 0 )
			{
				String[] text = { "There was error parsing timecode values in FROM or TO input boxes. Use format MM:SS:FF" };
				DialogUtils.showTwoStyleInfoForParent( "Render Info", text, window, DialogUtils.WARNING_MESSAGE );
				return;
			}

			if( start >= end )
			{
				String[] text = { "FROM timecode value has to be earlier then TO timecode value." };
				DialogUtils.showTwoStyleInfoForParent( "Render Info", text, window, DialogUtils.WARNING_MESSAGE );
				return;
			}

			if( end >  ProjectController.getLength() )
			{
				String[] text = { "TO timecode value is later then timeline's last frame." };
				DialogUtils.showTwoStyleInfoForParent( "Render Info", text, window, DialogUtils.WARNING_MESSAGE );
				return;
			}
			if( CodecController.movieOn && (CodecController.movieFileName == null || CodecController.movieFileName.length() == 0 ))
			{
				String[] text = { "Movie file name entry is empty. Give proper name for movie." };
				DialogUtils.showTwoStyleInfoForParent( "Render Info", text, window, DialogUtils.WARNING_MESSAGE );
				return;
			}
			*/
			
			int start = parseTCString( from.getText() );
			int end = parseTCString( to.getText() );
			
			int fps =  ProjectController.getFramesPerSecond();
			from.setText(  TimeLineDisplayPanel.parseTimeCodeString( start, 6, fps ));
			to.setText(  TimeLineDisplayPanel.parseTimeCodeString( end, 6, fps ));

			RenderModeController.setWriteRange( start, end );
			RenderModeController.startWriteRender();
			render.setEnabled( false );
			stop.setEnabled( true );
			exit.setEnabled( false );
		}

		if( e.getSource() == setFrameSettins )
		{
			DialogUtils.setDialogParent( window );
			MenuActions.setFrameOutputSettings();
			DialogUtils.setDialogParent( null );
			repaint();
		}

		if( e.getSource() == setRenderSettins )
		{
			DialogUtils.setDialogParent( window );
			MenuActions.setRenderSettings();
			DialogUtils.setDialogParent( null );
			repaint();
		}

		if( e.getSource() == stop )
		{
			RenderModeController.stopWriteRender();
			render.setEnabled( true );
			stop.setEnabled( false );
			exit.setEnabled( true );
		}

		if( e.getSource() == exit )
		{
			RenderModeController.disposeRenderWindow();
		}
	}

}//end class
