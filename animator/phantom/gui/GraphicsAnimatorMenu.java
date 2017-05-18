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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import animator.phantom.controller.EditorPersistance;
import animator.phantom.controller.GraphicsAnimatorController;
import animator.phantom.controller.MenuActions;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.RenderModeController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.project.MovieFormat;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

public class GraphicsAnimatorMenu extends JMenuBar implements ActionListener, MenuBarCallbackInterface
{
	//--- File
	JMenuItem openProject;
	JMenuItem openRecent;
	JMenuItem saveProject;
	JMenuItem saveProjectAs;
	JMenuItem closeProject;
	JMenuItem quit;

	//--- Edit
	JMenuItem undo;
	JMenuItem redo;

	//--- Project Menu
	JMenuItem addImage;
	JMenuItem projectSettings;

	//--- Render
	JMenuItem previewCurrent;
	JMenuItem previewFromCurrent;
	JMenuItem previewFromStart;
	JMenuItem renderMovie;

	//--- Help
	JMenuItem keyboardShortcuts;
	JMenuItem about;

	//----
	private static int SHORT_CUT_PLACE = 25;

	//---
	Vector<JMenuItem> newProjectItems = new Vector<JMenuItem>();

	public GraphicsAnimatorMenu()
	{
		super();

		setFont( GUIResources.BASIC_FONT_12 );

		//---------------------------------- File menu
		JMenu fileMenu = new JMenu("File");

		JMenu newSelect = new JMenu( "New" );
		JMenuItem customItem = new JMenuItem( "custom" );
		customItem.addActionListener(this);
		newSelect.add( customItem );
		newProjectItems.add( customItem );
		for( int i = 0; i < MovieFormat.formats.size(); i++ )
		{

			JMenuItem addItem = new JMenuItem( MovieFormat.formats.elementAt( i ).getName() );
			addItem.addActionListener(this);
			newSelect.add( addItem );
			newProjectItems.add( addItem );
		}
		fileMenu.add( newSelect );

		openProject = new JMenuItem("Open...");
		openProject.addActionListener(this);
		fileMenu.add(openProject);

		openRecent = new JMenu( "Open Recent" );
		updateRecentMenu();
		fileMenu.add( openRecent );

		fileMenu.addSeparator();

		saveProject = new JMenuItem( "Save" );
		saveProject.setAccelerator( KeyStroke.getKeyStroke( "ctrl pressed S"  ) );
		saveProject.addActionListener(this);
		fileMenu.add( saveProject );

		saveProjectAs = new JMenuItem("Save As...");
		saveProjectAs.addActionListener(this);
		fileMenu.add( saveProjectAs );

		fileMenu.addSeparator();

		closeProject = new JMenuItem( "Close" );
		closeProject.addActionListener(this);
		fileMenu.add( closeProject );

		quit = new JMenuItem( "Quit" );
		quit.addActionListener(this);
		quit.setAccelerator( KeyStroke.getKeyStroke( "ctrl pressed Q"  ) );
		fileMenu.add( quit );

		//------------------------------------ Edit menu
		JMenu editMenu = new JMenu("Edit");

		undo = new JMenuItem("Undo");
		undo.addActionListener(this);
		undo.setAccelerator( KeyStroke.getKeyStroke( "ctrl pressed Z"  ) );
		editMenu.add( undo );

		redo = new JMenuItem("Redo");
		redo.addActionListener(this);
		redo.setAccelerator( KeyStroke.getKeyStroke( "ctrl shift pressed Z"  ) );
		editMenu.add( redo );

		//--------------------------- Media menu
		JMenu projectMenu = new JMenu("Project");
		projectSettings = new JMenuItem("Project Properties...");
		projectSettings.addActionListener(this);
		projectMenu.add( projectSettings);

		addImage  = new JMenuItem("Add Images...");
		addImage.addActionListener(this);
		projectMenu.add( addImage );

		//--- ------------------------------- Render menu
		JMenu renderMenu = new JMenu("Render");

		previewFromCurrent = new JMenuItem("Preview From Current" );
		previewFromCurrent.addActionListener(this);
		previewFromCurrent.setAccelerator( KeyStroke.getKeyStroke( "F9"  ) );
		renderMenu.add( previewFromCurrent );

		previewFromStart = new JMenuItem("Preview From Start" );
		previewFromStart.addActionListener(this);
		previewFromStart.setAccelerator( KeyStroke.getKeyStroke( "F10"  ) );
		renderMenu.add( previewFromStart );

		previewCurrent = new JMenuItem("Preview Current Frame" );
		previewCurrent.addActionListener(this);
		previewCurrent.setAccelerator( KeyStroke.getKeyStroke( "F11"  ) );
		renderMenu.add( previewCurrent );

		renderMenu.addSeparator();

		renderMovie = new JMenuItem("Render...");
		renderMovie.addActionListener(this);
		renderMovie.setAccelerator( KeyStroke.getKeyStroke( "F12"  ) );
		renderMenu.add( renderMovie );

		//----------------------------------- Help menu
		JMenu helpMenu = new JMenu("Help");

		keyboardShortcuts = new JMenuItem("Keyboard Shortcuts");
		keyboardShortcuts.addActionListener(this);
		helpMenu.add( keyboardShortcuts );

		helpMenu.addSeparator();

		about = new JMenuItem("About...");
		about.addActionListener(this);
		helpMenu.add( about );

		add( fileMenu );
		add( editMenu );
		add( projectMenu );
		add( renderMenu );
		add( helpMenu );
	}

	//--- Adds key short cut to give place in string
	private static String getItemString( String item, String shortCut, int correction )
	{
		int middleStringLength = SHORT_CUT_PLACE - item.length() + correction;
		StringBuffer rBuf = new StringBuffer(item);
		for( int i = 0; i < middleStringLength; i++ ) rBuf.append(" ");
		rBuf.append( shortCut );
		return rBuf.toString();
	}


	public void updateRecentMenu()
	{
		openRecent.removeAll();
		Vector<File> recents = EditorPersistance.getRecentProjects();
		if( recents.size() == 0 )
		{
			RecentMenuItem none = new RecentMenuItem("none", null);
			none.setEnabled( false );
			openRecent.add( none );
		}
		else
		{
			for( File f : recents )
			{
				RecentMenuItem addr = new RecentMenuItem( f.getName(), f );
				addr.addActionListener(this);
				openRecent.add( addr );
			}
		}
	}

	public void updateAppMediaMenu()
	{

	}

	//--------------------------------------------- MENU ACTIONS
	public void actionPerformed(ActionEvent e)
	{
		//-------------------------------------------- File menu
		for( int i = 0; i < newProjectItems.size(); i++ )
		{
			if( e.getSource() == newProjectItems.elementAt( i ) )
			{
				MenuActions.newProject( i );
				return;
			}
		}
		if( e.getSource() == openProject ) MenuActions.openProject();
		if( e.getSource() instanceof RecentMenuItem )
		{
			RecentMenuItem r = ( RecentMenuItem )e.getSource();
			if( r.getFile() == null ) return;
			MenuActions.openProjectFile( r.getFile() );
			return;
		}
		if( e.getSource() == saveProject ) MenuActions.saveProject();
		if( e.getSource() == saveProjectAs ) MenuActions.saveProjectAs();
		if( e.getSource() == closeProject ) MenuActions.close();
		if( e.getSource() == quit ) MenuActions.quit();

		//--------------------------------------------- Edit menu
		if( e.getSource() == undo ) MenuActions.undo();
		if( e.getSource() == redo ) MenuActions.redo();

		//--------------------------------------------- Project menu
		if( e.getSource() == projectSettings ) MenuActions.setProjectProperties();
		if( e.getSource() == addImage )
		{
			GraphicsAnimatorController.setAnimatedGraphic();
		}

		//-------------------------------------------------- Render menu.
		if( e.getSource() == previewCurrent ) PreviewController.renderAndDisplayCurrent();
		if( e.getSource() == previewFromStart ) PreviewController.renderAndPlayRange( 0, ProjectController.getLength() - 1 );
		if( e.getSource() == previewFromCurrent ) PreviewController.renderAndPlayRange( TimeLineController.getCurrentFrame(), ProjectController.getLength() - 1 );
		if( e.getSource() == renderMovie ) RenderModeController.writeMovie();

		//----------------------------------------------------- Help menu
		if( e.getSource() == keyboardShortcuts  ) MenuActions.displayKeyboardShortcuts();
		if( e.getSource() == about ) MenuActions.displayAbout();
	}

}//end class
