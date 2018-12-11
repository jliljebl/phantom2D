package animator.phantom.controller;

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
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MActionListener;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.gui.modals.MTextField;
import animator.phantom.gui.modals.MTextInfo;
import animator.phantom.project.Bin;
import animator.phantom.project.MovieFormat;
import animator.phantom.project.Project;
import animator.phantom.project.ProjectNamedFlow;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.RenderFlow;

//--- Project logic and state + access for other logic and GUI.
public class ProjectController
{
	//--- MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA
	//--- Project currently being edited.
	private static Project project = null;
	//--- Flag to turn motion blur off globally, transient, affects preview render only.
	private static boolean motionBlur = true;
	//--- Clears all class data members.
	private static String loadPath = null;
	
	public static void reset()
	{
		project = null;
		motionBlur = true;
		loadPath = null;
	}
	//--- sets project to be edited
	public static void setProject( Project newProject )
	{
		//--- Set project.
		project = newProject;
	}
	public static void setLoadPath( String path )
	{
		loadPath = path;
	}
	public static String getLoadPath()
	{
		return loadPath;
	}
	
	//--- returns current project
	public static Project getProject(){ return AppData.getProject(); }
	//--- Get and set for global motion blur
	public static boolean getMotionBlur(){ return motionBlur; }
	public static void setMotionBlur( boolean val ){ motionBlur = val; }


	//--- PROJECT DATA INTERFACE, PROJECT DATA INTERFACE, PROJECT DATA INTERFACE
	//public static String getName(){ return project.getName(); }
	public static void changeName( String newName )
	{
		AppData.getProject().setName( newName );
		GUIComponents.animatorFrame.setTitle( AppData.getProject().getName() + " - Phantom2D" );
	}
	public static int getFramesPerSecond(){ return AppData.getProject().getFramesPerSecond(); }
	public static void setFramesPerSecond( int fps ){ project.setFramesPerSecond( fps );  }
	public static int getCurrentLength(){ return AppData.getProject().getCurrentComposition().getLength(); }
	public static void setLength( int length ){ project.setLength( length ); }
	public static Dimension getCurrentScreenSize(){ return AppData.getProject().getCurrentScreenDimensions(); }
	public static void setScreenSize( Dimension size ){ project.setScreenDimensions( size );  }
	public static BufferedImage getScreenSample()
	{
		return new BufferedImage( 	project.getCurrentScreenDimensions().width,
									project.getCurrentScreenDimensions().height,
									BufferedImage.TYPE_INT_ARGB );
	}

	//--- RENDER FLOW, RENDER FLOW, RENDER FLOW, RENDER FLOW, RENDER FLOW, RENDER FLOW
	public static RenderFlow getFlow(){ return AppData.getCurrentFlow(); }


	//--- BIN MANAGEMENT, BIN MANAGEMENT, BIN MANAGEMENT, BIN MANAGEMENT, BIN MANAGEMENT
	//--- Adds FileSource to bin.

	//--- Adds multiple FileSources to bin.
	public static void addFileSourceVectorToBin( Vector<FileSource> vec, Bin bin )
	{
		bin.addFileSourceVector( vec );
	}

	public static void deleteFileSourceVectorFromBin( Vector<FileSource> vec, Bin bin )
	{
		bin.removeFileSourceVector( vec );
	}
	public static void deleteFileSourceVector( Vector<FileSource> vec )
	{
		project.deleteFileSourcesFromProject( vec );
	}
	//--- Adds new bin to project.
	public static void addBin( Bin bin ){ project.addBin( bin ); }
	//--- Returns Vector of all bins in project.
	public static Vector<Bin> getBins(){ return project.getBins(); }
	//--- Deletes bin from project.
	public static void deleteBin( Bin bin ){ project.deleteBin( bin ); }
	//--- 
	public static void displayFileSourceInfo( FileSource fs )
	{
		/*
		if( fs != null )
		{
			String info = 	// width x height
				fs.getName() + " " 
				+ ( new Integer( fs.getImageWidth() )).toString() 
				+ " x " + ( new Integer( fs.getImageHeight() )).toString();
			GUIComponents.projectPanel.setInfoLabelText( info );
			GUIComponents.projectPanel.setThumbIcon( fs );
		}
		else GUIComponents.projectPanel.setInfoLabelText( "" );
		*/
	}


	//--- FILE SOURCES, FILE SOURCES, FILE SOURCES, FILE SOURCES, FILE SOURCES
	//--- Adds file sources to project
	public static void addFileSourcesToProject( Vector<FileSource> addFileSources )
	{
		project.addFileSourcesToProject( addFileSources );
	}
	public static void deleteFileSourcesFromProject( Vector<FileSource> deleteFileSources )
	{
		project.deleteFileSourcesFromProject( deleteFileSources );
	}
	//--- Returns all file sources.
	public static Vector<FileSource> getFileSources(){ return project.getFileSources(); }

	public static void updateProjectInfo()
	{
		//--- Display project info
		String info = AppData.getProject().getCurrentComposition().getName() + "  -  " + Integer.toString(project.getCurrentScreenDimensions().width)
				+ " x " + Integer.toString(AppData.getProject().getCurrentScreenDimensions().height) + ",  "
				+ Integer.toString(AppData.getProject().getFramesPerSecond()) + " fps,  "
				+ Integer.toString(AppData.getProject().getLength()) + " frames";
		GUIComponents.projectInfoLabel.setText( info );
	}

	
	//--- Display properties panel and changes.
	public static void setProjectProperties()
	{
		String[] options = new String[ MovieFormat.formats.size() ];
		for( int i = 0; i < MovieFormat.formats.size(); i++ )
		{
			options[ i ] = MovieFormat.formats.elementAt( i ).getName();
		}

		final MComboBox formats = new MComboBox( "Format", options );
		formats.setBuffering( 0, 20 );
		final MTextField width = new MTextField( "Screen width", new Integer( ProjectController.getCurrentScreenSize().width ) );
		final MTextField height = new MTextField( "Screen height", new Integer( ProjectController.getCurrentScreenSize().height) );
		final MTextField fps = new MTextField( "Frames per second", new Integer( ProjectController.getFramesPerSecond() ) );
		MTextField length  = new MTextField( "Length in frames", new Integer( ProjectController.getCurrentLength() ));

		MInputArea mArea = new MInputArea( "Format" );
		mArea.add( formats );
		mArea.add( width );
		mArea.add( height );
		mArea.add( fps );

		MInputArea mArea2 = new MInputArea( "Length" );
		mArea2.add( length );

		final MInputPanel pPanel = new MInputPanel( "Project Properties" );
		pPanel.add( mArea );
		pPanel.add( mArea2 );

		formats.addActionListener
		(
			new MActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String formatName = (String) formats.getValue();
					int index = -1;
					for( int i = 0; i < MovieFormat.formats.size(); i++ )
					{
						 if( MovieFormat.formats.elementAt( i ).getName().equals( formatName ) )
							index = i;
					}

					if( index == -1 )
						return;

					MovieFormat format = MovieFormat.formats.elementAt( index );
					width.textField.setText( ( new Integer( format.getScreenSize().width ) ).toString() );
					height.textField.setText( ( new Integer( format.getScreenSize().height ) ).toString() );
					fps.textField.setText( ( new Float( format.getFPS() ) ).toString() );

					pPanel.repaint();

				}
			}
		);

		int retVal = DialogUtils.showMultiInput( pPanel, 480, 300 );

		//--- After user inter action.
		if( retVal != DialogUtils.OK_OPTION ) return;
		boolean reloadNeeded = false;

		int editWidth = width.getIntValue();
		int editHeight = height.getIntValue();
		int editFps = (int) fps.getFloatValue();
		int editLength = length.getIntValue();
		if(	getCurrentScreenSize().width != editWidth ||
			getCurrentScreenSize().height != editHeight ||
			getFramesPerSecond() != editFps ||
 			getCurrentLength() != editLength )
		{
			reloadNeeded = true;
		}

		if( reloadNeeded )
		{

			setScreenSize( new Dimension( editWidth, editHeight ) );
			setFramesPerSecond( editFps );
 			setLength( editLength );
 			
			//--- Open old project with updated settings.
			LayerCompositorApplication.getApplication().openProject( ProjectController.getProject() );

			TimeLineController.loadClips();
			TimeLineController.initClipEditorGUI();
		}
	}
	
	public static void newComposition()
	{
		String[] options = new String[ MovieFormat.formats.size() ];
		for( int i = 0; i < MovieFormat.formats.size(); i++ )
		{
			options[ i ] = MovieFormat.formats.elementAt( i ).getName();
		}

		final MTextInfo fps = new MTextInfo( "Frames per second", new Integer( ProjectController.getFramesPerSecond() ) );
		MInputArea projInfoArea = new MInputArea( "Project Properties" );
		projInfoArea.add( fps );
		
		final MTextField name = new MTextField( "Name", "Comp 2");
		final MComboBox formats = new MComboBox( "Format", options );
		formats.setBuffering( 0, 20 );
		final MTextField width = new MTextField( "Screen width", new Integer( ProjectController.getCurrentScreenSize().width ) );
		final MTextField height = new MTextField( "Screen height", new Integer( ProjectController.getCurrentScreenSize().height) );
		MTextField length  = new MTextField( "Length in frames", new Integer( AppData.getProject().getDefaultLength() ));
		MInputArea mArea = new MInputArea( "New Composition Properties" );
		mArea.add( name );
		mArea.add( formats );
		mArea.add( width );
		mArea.add( height );
		mArea.add( length );

		final MInputPanel pPanel = new MInputPanel( "Create New Composition" );
		pPanel.add( projInfoArea );
		pPanel.add( mArea );

		formats.addActionListener
		(
			new MActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String formatName = (String) formats.getValue();
					int index = -1;
					for( int i = 0; i < MovieFormat.formats.size(); i++ )
					{
						 if( MovieFormat.formats.elementAt( i ).getName().equals( formatName ) )
							index = i;
					}

					if( index == -1 )
						return;

					MovieFormat format = MovieFormat.formats.elementAt( index );
					width.textField.setText( ( new Integer( format.getScreenSize().width ) ).toString() );
					height.textField.setText( ( new Integer( format.getScreenSize().height ) ).toString() );

					pPanel.repaint();

				}
			}
		);

		int retVal = DialogUtils.showMultiInput( pPanel, 480, 350 );
		if( retVal != DialogUtils.OK_OPTION ) return;

		int editWidth = width.getIntValue();
		int editHeight = height.getIntValue();
		int editLength = length.getIntValue();
		String compName = name.getStringValue();
		
		ProjectNamedFlow newComp  = AppData.getProject().addNewComposition( compName, editLength, new Dimension( editWidth, editHeight ) );
		openComposition( newComp.getID() );
	}
	
	public static void openComposition( int compositionID )
	{
		AppData.getProject().setCurrentComposition( compositionID );
		
		LayerCompositorApplication.getApplication().openProject( AppData.getProject() );
	
		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();
	}
	
}//end class