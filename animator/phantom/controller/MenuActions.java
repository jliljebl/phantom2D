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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import animator.phantom.blender.Blender;
import animator.phantom.gui.AnimatorFrameLayout;
import animator.phantom.gui.ContentPaneLayout;
import animator.phantom.gui.FileLoadWindow;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.GUIUtils;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MActionListener;
import animator.phantom.gui.modals.MButton;
import animator.phantom.gui.modals.MCheckBox;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MFileSelect;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.gui.modals.MSlider;
import animator.phantom.gui.modals.MTextField;
import animator.phantom.gui.modals.MTextInfo;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.gui.view.editlayer.MergeEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.project.MovieFormat;
import animator.phantom.project.Project;
import animator.phantom.renderer.FileSequenceSource;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.imagemerge.BasicTwoMergeIOP;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.renderer.param.Param;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.xml.ImageOperationXML;
import animator.phantom.xml.PhantomXML;

//--- Code for actions asked by the user with menu selections.
public class MenuActions
{
	private static FileLoadWindow loadWindow;

	//------------------------------------------------------------ file
	public static void newProject( int selectionIndex )
	{
		//--- preset formats
		if( selectionIndex > 0 )
		{
			MovieFormat format = MovieFormat.formats.elementAt( selectionIndex - 1 );
			Project project = new Project( "untitled.phr", format );
			Application.getApplication().openProject( project );
		}
		//--- custom == default + dialog
		else
		{
			MovieFormat format = MovieFormat.formats.elementAt( 0 );// 0 == default
			Project project = new Project( "untitled.phr", format );
			Application.getApplication().openProject( project );
			setProjectProperties();
		}
	}
	//---
	public static void openProject()
	{
		String acceptedExtensions[] = { Project.PROJECT_FILE_EXTENSION, "xml"};
		File loadFile = GUIUtils.selectFilteredFile( 
				GUIComponents.getAnimatorFrame(),
				acceptedExtensions,
				"Open project");
		if( loadFile == null ) return;
		
		openProjectFile( loadFile );
	}
	public static void openProjectFile( File f )
	{
		final File loadFile = f;
		EditorPersistance.addRecent( loadFile );
		EditorPersistance.write();
		final Document doc = PhantomXML.loadXMLDoc( loadFile.getAbsolutePath() );

		new Thread()
		{
			public void run()
			{
				loadWindow = new FileLoadWindow( GUIComponents.animatorFrame, loadFile.getName() );
				Project project = PhantomXML.loadProject( doc );
				project.setSaveFile( loadFile );
				Application.getApplication().openProject( project );
				loadWindow.setVisible( false );
				loadWindow = null;
			}

		}.start();
	}
	//--- 
	public static void saveProject()
	{
		File saveFile = ProjectController.getProject().getSaveFile();
		//--- If we have save file save and out.
		if( saveFile != null )
		{
			Document doc = PhantomXML.buildXMLDoc( ProjectController.getProject() );
			PhantomXML.writeXMLFile( doc, saveFile.getAbsolutePath()  );
			//--- Update recent files selection in Open Recent
			EditorPersistance.addRecent( saveFile );
			EditorPersistance.write();
		}
		else 
			saveProjectAs();
	}
	//--- Saves project with user specified name and sets it as projects name
	public static void saveProjectAs()
	{
		String[] filters = { Project.PROJECT_FILE_EXTENSION };
		File saveFile = GUIUtils.selectSaveFile( 
					GUIComponents.getAnimatorFrame(),
					filters,
					"Save As",
					ProjectController.getProject().getSaveFile() );

		if( saveFile == null ) return;

		String extension = AppUtils.getExtension( saveFile );
		if( extension == null ) saveFile = new File( saveFile.getAbsolutePath() 
			+ "." + Project.PROJECT_FILE_EXTENSION );
		else if( !extension.equals(  Project.PROJECT_FILE_EXTENSION  ) )
		{
			//--- some info window here?
			System.out.println("WRONG EXTENSION");
			return;
		}
		ProjectController.changeName( saveFile.getName() );
		Document doc = PhantomXML.buildXMLDoc( ProjectController.getProject() );
		PhantomXML.writeXMLFile( doc, saveFile.getAbsolutePath() );
		ProjectController.getProject().setSaveFile( saveFile );
		EditorPersistance.addRecent( saveFile );
		EditorPersistance.write();
		GUIComponents.animatorMenu.updateRecentMenu();
	}
	
	//--- Closes current project if confirmed.
	public static void close()
	{
		String[] opts = { "Cancel", "Close project" };
		String[] bLines = { "Are you sure you want to close current project." };
		String[] tLines = { "All unsaved progress will be lost." };
		int answer 
			= DialogUtils.showTwoTextStyleDialog( 	
								JOptionPane.WARNING_MESSAGE, 
								"Closing project", 
								opts, 
								bLines, 
								tLines, 
								GUIComponents.getAnimatorFrame() );

		if( answer != 1 ) return;
		Application.getApplication().openDefaultProject();
	}

	//--- Quits program if confirmed.
	public static void quit()
	{
		//--- Confirm
		String[] opts = {  "Quit and Discard", "Cancel",  "Save", };
		String[] bLines = { "Are you sure you want to quit?" };
		String[] tLines = { "All unsaved changes for " + ProjectController.getName() + " will be lost." };
		int answer 
			= DialogUtils.showTwoTextStyleDialog( 	
								JOptionPane.WARNING_MESSAGE, 
								"Quit Phantom2D", 
								opts, 
								bLines, 
								tLines, 
								GUIComponents.getAnimatorFrame() );

		//--- Do actual quitting
		if( answer == 2 )//--- save and quit
		{
			saveProject();
			File saveFile = ProjectController.getProject().getSaveFile();
			if( saveFile == null ) return;//didn't save ??????????
			System.exit(0);
		}
		else if( answer == 1 ) //--- cancel
		{
			return;
		}
		else if( answer == 0 )//--- quit, no save
		{
			System.exit(0);
		}
		else return;
	}
	//------------------------------------------------------ edit
	public static void undo()
	{
		PhantomUndoManager.doUndo();
		UpdateController.valueChangeUpdate();
		ParamEditController.undoUpdate();
		//--- Clear all selections, selction is transient and not a (data) edit action
		FlowController.clearSelection();
		//--- kf diamonds update
		ImageOperation iop = PhantomUndoManager.getLastActionIOP();
		if( iop == null )
			return;
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}

	public static void redo()
	{
		PhantomUndoManager.doRedo();
		UpdateController.valueChangeUpdate();
		ParamEditController.undoUpdate();
		//--- Clear all selections, selction is transient and not a (data) edit action
		FlowController.clearSelection();
		//--- kf diamonds update
		ImageOperation iop = PhantomUndoManager.getLastActionIOP();
		if( iop == null )
			return;
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
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
		final MTextField width = new MTextField( "Screen width", new Integer( ProjectController.getScreenSize().width ) );
		final MTextField height = new MTextField( "Screen height", new Integer( ProjectController.getScreenSize().height) );
		final MTextField fps = new MTextField( "Frames per second", new Integer( ProjectController.getFramesPerSecond() ) );
		MTextField length  = new MTextField( "Length in frames", new Integer( ProjectController.getLength() ));

		MInputArea mArea = new MInputArea( "Format" );
		mArea.add( formats );
		mArea.add( width );
		mArea.add( height );
		mArea.add( fps );
		
		MInputArea mArea2 = new MInputArea( "Length" );
		mArea2.add( length );

		final MInputPanel pPanel = new MInputPanel( "Composition Properties" );
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

		int retVal = DialogUtils.showMultiInput( pPanel, 400, 300 );

		//--- After user inter action.
		if( retVal != DialogUtils.OK_OPTION ) return;
		boolean reloadNeeded = false;

		int editWidth = width.getIntValue();
		int editHeight = height.getIntValue();
		int editFps = (int) fps.getFloatValue();
		int editLength = length.getIntValue();
		if(	ProjectController.getScreenSize().width != editWidth || 
			ProjectController.getScreenSize().height != editHeight ||
			ProjectController.getFramesPerSecond() != editFps ||
 			ProjectController.getLength() != editLength )
		{
			reloadNeeded = true;
		}
		
		if( reloadNeeded )
		{
			Vector<ImageOperation> oldClips = new Vector<ImageOperation>( TimeLineController.getClips() );

			ProjectController.setScreenSize( new Dimension( editWidth, editHeight ) );
			ProjectController.setFramesPerSecond( editFps );
 			ProjectController.setLength( editLength );
			//--- Open old project with updated settings.
			Application.getApplication().openProject( ProjectController.getProject() );

			TimeLineController.addClips( oldClips );
			TimeLineController.initClipEditorGUI();
			FlowController.clearSelection();
		}
		
		GUIComponents.renderFlowButtons.repaint();
		
	}

	public static void showProjectInfo()
	{
		DialogUtils.showProjectInfoDialog( ProjectController.getProject() );
	}

	public static void setPluginSettings()
	{
		String ppath =  EditorPersistance.getStringPref( EditorPersistance.PLUGIN_DIR );
		File pluginDir = null;
		if( ppath.length() != 0 )
			pluginDir = new File( ppath );

		MFileSelect tfs = new MFileSelect( "Plugin Folder", "Select Plugin Folder", 25, pluginDir, null );
		tfs.setType( JFileChooser.DIRECTORIES_ONLY );

		MInputArea area = new MInputArea( "Path" );
		area.add( tfs );

		MActionListener listener = 	new MActionListener() 
						{
							public void actionPerformed(ActionEvent e) 
							{
								displayPlugins();
							}
						};

		MButton button = new MButton( "View plugins", listener );
		button.setText( (new Integer( PluginController.getLoadedPluginsSize())).toString() + " plugin(s) loaded." );

		MInputArea area2 = new MInputArea( "Info" );
		area2.add( button );
 
		final MInputPanel iPanel = new MInputPanel( "Plugin Settins" );
		iPanel.add( area );
		iPanel.add( area2 );

		//--- Display dialog.
		int retVal = DialogUtils.showMultiInput( iPanel );

		//--- After user inter action.
		if( retVal != DialogUtils.OK_OPTION ) return;

		File newFolder = tfs.getSelectedFile();
		if( newFolder != null )
		{
			EditorPersistance.setPref( EditorPersistance.PLUGIN_DIR, newFolder.getAbsolutePath() );
			EditorPersistance.write();
		}

		if( !newFolder.getAbsolutePath().equals( ppath ) )
		{
			String[] tLines = { "Plugins in selected folder will be loaded when application is restarted." };
			DialogUtils.showTwoStyleInfo( "Plugin folder info", tLines, DialogUtils.WARNING_MESSAGE );
		}
	}

	private static void displayPlugins()
	{
		JPanel pane = new JPanel();
		pane.setLayout( new BoxLayout( pane, BoxLayout.X_AXIS ));

		JPanel plugins = new JPanel();
		plugins.setLayout( new BoxLayout( plugins, BoxLayout.Y_AXIS ));
		JPanel group = new JPanel();
		group.setLayout( new BoxLayout( group, BoxLayout.Y_AXIS ));
		JPanel version = new JPanel();
		version.setLayout( new BoxLayout( version, BoxLayout.Y_AXIS ));
		JPanel api_version = new JPanel();
		api_version.setLayout( new BoxLayout( api_version, BoxLayout.Y_AXIS ));
		JPanel author = new JPanel();
		author.setLayout( new BoxLayout( author, BoxLayout.Y_AXIS ));

		pane.add( plugins );
		pane.add( group );
		pane.add( version );
		pane.add( api_version );
		pane.add( author );

		plugins.add( getCell( "plugin", true ) );
		group.add( getCell( "group", true ) );
		version.add( getCell( "version", true ) );
		api_version.add( getCell( "API version", true ) );
		author.add( getCell( "author", true ) );

		Hashtable<PluginInfo, PhantomPlugin> pluginTable = PluginController.getPluginsTable();

		for (Enumeration<PluginInfo> e = pluginTable.keys(); e.hasMoreElements();)
		{
			PluginInfo pinfo = e.nextElement();
			PhantomPlugin plugin = pluginTable.get( pinfo );

			plugins.add( getCell( plugin.getName() ) );
			group.add(  getCell( pinfo.group ) );
 			version.add(  getCell( pinfo.version ) );
			api_version.add(  getCell( (new Integer(pinfo.apiVersion)).toString() ) );
			author.add(  getCell(pinfo.author) );
		}

		int pad = 0;
		if(  pluginTable.size() == 0 )
		{
			pane = new JPanel();
			pane.add( new JLabel( "No plugins loaded" ) );
			pad = 30;
		}

		DialogUtils.showPanelOKDialog( pane, "Loaded plugins", 500, pluginTable.size() * 30 + pad );
	}

	private static JPanel getCell( String text )
	{
		return getCell( text, false );
	}
	private static JPanel getCell( String text, boolean bold )
	{

		JPanel cell = new JPanel();
		cell.setLayout( new BoxLayout( cell, BoxLayout.X_AXIS ));
		JLabel label = new JLabel( text );
		if( !bold )
			label.setFont( GUIResources.BASIC_FONT_12 );
		cell.add( label );
		cell.add(  Box.createHorizontalGlue() );
		cell.add(  Box.createRigidArea( new Dimension( 5, 0 )) );
		return cell;
	}

	public static void setEditorPreferences()
	{
		int mh = EditorPersistance.getIntPref( EditorPersistance.LAYOUT_MID );
		int fw = EditorPersistance.getIntPref( EditorPersistance.FLOW_WIDTH );
		int fh = EditorPersistance.getIntPref( EditorPersistance.FLOW_HEIGHT );

		MTextField midHeight = new MTextField( "Layout middle", 75, new Integer( mh ));
		MInputArea winArea = new MInputArea( "Windows" );
		winArea.add( midHeight );

		MTextField flowWidth = new MTextField( "Flow edit area width", 75, new Integer( fw ));
		MTextField flowHeight = new MTextField( "Flow edit area height", 75, new Integer( fh ));
		MInputArea flowArea = new MInputArea( "Flow editor" );
		flowArea.add( flowWidth );
		flowArea.add( flowHeight );

		final MInputPanel windowsPanel = new MInputPanel( "Editor Preferences" );
		windowsPanel.add( winArea );
		windowsPanel.add( flowArea );

		int retVal = DialogUtils.showMultiInput( windowsPanel );
		if( retVal != DialogUtils.OK_OPTION ) return;

		EditorPersistance.setPref( EditorPersistance.LAYOUT_MID, midHeight.getIntValue()  );
		EditorPersistance.setPref( EditorPersistance.FLOW_WIDTH, flowWidth.getIntValue() );
		EditorPersistance.setPref( EditorPersistance.FLOW_HEIGHT,flowHeight.getIntValue()  );
		EditorPersistance.write();

		String[] tLines = { "Save project and restart application to apply changes." };
		DialogUtils.showTwoStyleInfo( "Changed editor prefences saved", tLines, DialogUtils.WARNING_MESSAGE );
	}

	public static void displayKeyboardShortcuts()
	{
		//--- Create dialog panel
		final MInputPanel editPanel = new MInputPanel( "Keyboard shortcuts" );

		//--- Create shortcut editors and hashmaps
		MInputArea inputArea = new MInputArea( "" );
		String key;
		String value; 
		MTextInfo infoRow;

		//--- display flow
		key = EditorPersistance.DISP_FLOW_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo( "Display Composition",  value );
		inputArea.add( infoRow );

		//--- display clip
		key = EditorPersistance.DISP_CLIP_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo( "Display Timeline",  value );
		inputArea.add( infoRow );

		//--- display kf
		key = EditorPersistance.DISP_KF_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo( "Display Spline",  value );
		inputArea.add( infoRow );
		//--- flow arrange
		key = EditorPersistance.FLOW_ARRANGE_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Arrange Composition boxes",  value );
		inputArea.add( infoRow );

		//--- flow connect
		key = EditorPersistance.FLOW_CONNECT_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Connect Composition boxes",  value );
		inputArea.add( infoRow );

		//--- flow disconnect
		key = EditorPersistance.FLOW_DISCONNECT_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Disonnect Composition boxes",  value );
		inputArea.add( infoRow );

		//--- tline zoom in
		key = EditorPersistance.TLINE_ZOOM_IN_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Timeline zoom in",  value );
		inputArea.add( infoRow );

		//--- tline zoom out
		key = EditorPersistance.TLINE_ZOOM_OUT_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Timeline zoom out",  value );
		inputArea.add( infoRow );

		//--- tline prev
		key = EditorPersistance.TLINE_PREV_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Timeline previous frame",  value );
		inputArea.add( infoRow );

		//--- tline next 
		key = EditorPersistance.TLINE_NEXT_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Timeline next frame",  value );
		inputArea.add( infoRow );

		//--- next layer
		key = EditorPersistance.NEXT_LAYER_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Next Layer",  value );
		inputArea.add( infoRow );

		//--- preview
		key = EditorPersistance.RENDER_PRE_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Render preview",  value );
		inputArea.add( infoRow );

		//--- preview frame
		key = EditorPersistance.RENDER_PRE_FRAME_KEY_SC;
		value =  EditorPersistance.getStringPref( key );
		infoRow = new MTextInfo(  "Render frame preview",  value );
		inputArea.add( infoRow );

		editPanel.add( inputArea );

		int retVal = DialogUtils.showMultiInput( editPanel, 400, 500, false );
		if( retVal != DialogUtils.OK_OPTION ) return;

	}

	//------------------------------------------------------ Node
	public static void renameSelected()
	{
		Vector<RenderNode> nodes = GUIComponents.renderFlowPanel.getSelectedNodes();
		if( nodes.size() == 0 ) 
			return;

		RenderNode rn = nodes.elementAt( 0 );
		ImageOperation iop = rn.getImageOperation();
		String newName = DialogUtils.getTextInput( "Rename Node",
							"New name",
							iop.getName(),
							150,
							250);
		if( newName == null ) 
			return;

		iop.setName( newName );

		iopNameChanged( iop, rn );
	}

	private static void iopNameChanged( ImageOperation iop, RenderNode rn )
	{

		if(  ParamEditController.getParamEditIOP() == iop )
		{
			ParamEditController.displayEditFrame( null );
			iop.reCreateEditPanel();
			ParamEditController.displayEditFrame( iop );
			EditorsController.initKeyFrameEditor( iop );
		}
		else
		{
			iop.reCreateEditPanel();
		}

		//--- Get new editlayer with new name.
		ViewEditorLayer newEditLayer = iop.getEditorlayer();
		
		//--- BasicTwoMergeIOP returns null for edit layer because it creates and adds
		//--- them by its self when first properly connected, so it is special cased here.
		if( iop instanceof BasicTwoMergeIOP )
		{
			Rectangle r = ((BasicTwoMergeIOP)iop).getImageSize();
			if( r != null )
				newEditLayer = new MergeEditLayer( (BasicTwoMergeIOP) iop );
		}

		if( newEditLayer != null )
		{
			GUIComponents.viewEditor.replaceEditlayer( newEditLayer );
			GUIComponents.viewEditor.updateLayerSelector();
		}

		TimeLineController.targetIopChanged( iop );
		FlowController.iopNameChanged( rn );
	}

	//--- Clone seöected node
	public static void cloneSelected()
	{
		Vector<RenderNode> nodes = GUIComponents.renderFlowPanel.getSelectedNodes();
		if( nodes.size() == 0 ) 
			return;

		RenderNode rn = nodes.elementAt( 0 );
		cloneNode( rn );
	}

	//--- Clone iop inside RenderNode
	public static void cloneNode( RenderNode rn )
	{
		ImageOperation iop = rn.getImageOperation();

		Element iopElem = ImageOperationXML.getElement( iop );
		ImageOperation cloneIOP = ImageOperationXML.getObject( iopElem, ProjectController.getProject() );
		cloneIOP.loadParentIOP( ProjectController.getFlow() );

		FlowController.addIOPRightAway( cloneIOP );
	}

	public static void disableSelected()
	{
		Vector<RenderNode> nodes = GUIComponents.renderFlowPanel.getSelectedNodes();
		for( Object node : nodes )
		{
			RenderNode rn = (RenderNode) node;
			ImageOperation iop = rn.getImageOperation();
			iop.setOnOffState( false );
		}
		UpdateController.valueChangeUpdate();
	}

	public static void enableSelected()
	{
		Vector<RenderNode> nodes = GUIComponents.renderFlowPanel.getSelectedNodes();
		for( Object node : nodes )
		{
			RenderNode rn = (RenderNode) node;
			ImageOperation iop = rn.getImageOperation();
			iop.setOnOffState( true );
		}
		UpdateController.valueChangeUpdate();
	}

	//------------------------------------------------------ bin

	//------------------------------------------------------ view
	public static void setViewHeight()
	{
		MSlider slider = new MSlider("View editor height",  150, 250, AnimatorFrameLayout.VIEW_H, 200, 800 );

		MInputArea area = new MInputArea( "" );
		area.add( slider );

		MInputPanel panel = new MInputPanel( "Set View Editor Height" );
		panel.add( area );

		DialogUtils.showMultiInput( panel, 450, 110 );
		
		AnimatorFrameLayout.VIEW_H = slider.getIntValue();
		
		GUIComponents.animatorFrame.validate();
		GUIComponents.animatorFrame.repaint();
	}

	public static void setFlowWidth()
	{
		MSlider slider = new MSlider("Composition editor width",  150, 250, ContentPaneLayout.LEFT_WIDTH, 350, 700 );

		MInputArea area = new MInputArea( "" );
		area.add( slider );

		MInputPanel panel = new MInputPanel( "Set Composition Editor Height" );
		panel.add( area );

		DialogUtils.showMultiInput( panel, 450, 110 );
		
		ContentPaneLayout.LEFT_WIDTH = slider.getIntValue();
		
		GUIComponents.animatorFrame.validate();
		GUIComponents.animatorFrame.repaint();
	}

	
	public static void reloadSelected()
	{
		Vector<FileSource> selected = GUIComponents.binsPanel.currentSelectPanel().getSelected();
		try
		{
			GUIComponents.binsPanel.destroyIcons( selected );

			//--- Get image sizes and add GUI components to panels 
			for( FileSource addFS : selected )
			{
				addFS.firstLoadData();
				addFS.clearData();
				if( !addFS.getFile().exists() )
					throw new Exception();
				GUIComponents.binsPanel.currentSelectPanel().reInitSelectPanel();
				GUIComponents.binsPanel.updateGUI();
			}
			GUIComponents.binsPanel.deselectAll();
			//--- MemoryManager needs to update cache.
			MemoryManager.fileSourcesAdded();
		}
		catch( Exception e )
		{
			GUIComponents.binsPanel.addIcons( selected );

			String line1 = "Could not load file(s).";
			String[] tLines = { line1, "Make sure that file(s) exist." };
			DialogUtils.showTwoStyleInfo( "File reload failed", tLines, DialogUtils.WARNING_MESSAGE );
		}
	}

	public static void replaceSelected()
	{
		Vector<FileSource> selected = GUIComponents.binsPanel.currentSelectPanel().getSelected();
		if( selected.size() == 0 ||  selected.size() > 1 )
			return;//info?
		FileSource targetFS = selected.elementAt( 0 );
		boolean wasAvailable = targetFS.hasResourceAvailable();
		File oldFile = targetFS.getFile();
		String[] filters = AppUtils.getAcceptedFileExtensions();
		File newFile = GUIUtils.selectFilteredFile( GUIComponents.animatorFrame, filters, "Replace file:" + targetFS.getName() );

		String wrongChar = AppUtils.testStringForSequences(	AppUtils.forbiddenFileNameChars,
									targetFS.getName() );
		if( wrongChar != null )
		{
			UserActions.displayWrongFileChars( targetFS.getName(), wrongChar );
			return;
		}

		//--- This has multiple failure modes for user doing the wrong things.
		//--- These sould be pretty obivous but very hard to identify here so
		//--- so we'll just display simple info for failure 
		try
		{
			//--- Failure in next lines will set this false.
			targetFS.setResourceAvailable( true );

			if( targetFS instanceof FileSequenceSource )
			{
				targetFS.setFile( newFile );
				((FileSequenceSource)targetFS).loadInit();
			}
			else //single file
			{
				targetFS.setFile( newFile );
				targetFS.firstLoadData();
				targetFS.clearData();
			}

			if( !targetFS.hasResourceAvailable() )
				throw new Exception();

			GUIComponents.binsPanel.currentSelectPanel().updatePanelName( targetFS );
			GUIComponents.binsPanel.updateThumbIcon( targetFS );

			RenderFlow flow = ProjectController.getFlow();
			Vector<RenderNode> nodes = flow.getNodesWithFileSource( targetFS );
			for( RenderNode node : nodes )
			{
				node.getImageOperation().setName( targetFS.getName() );
				iopNameChanged( node.getImageOperation(), node );
			}
		}
		catch( Exception e )
		{
			String line1 = "Could not replace file with " + newFile.getName();
			if (oldFile != null )
				line1 = "Could not replace file " + oldFile.getName() + " with " + newFile.getName();
			String[] tLines = { line1, "Make sure that files are of same type" };
			
			targetFS.setFile( oldFile );
			targetFS.setResourceAvailable( wasAvailable );
			DialogUtils.showTwoStyleInfo( "File replace failed", tLines, DialogUtils.WARNING_MESSAGE );
			return;
		}

		GUIComponents.binsPanel.deselectAll();
		//--- Update GUI
		UpdateController.valueChangeUpdate();
		//--- MemoryManager needs to update cache.
		MemoryManager.fileSourcesAdded();
	}

	//------------------------------------------------------ Node
	//--- Adds new iop to flow
	public static void addIOP( String className  )
	{
		ImageOperation addIOP = IOPLibrary.getNewInstance( className );
		FlowController.addIOPRightAway( addIOP );
	}

	//------------------------------------------------------  Keyframe
	//--- Clears all keyframes from iop
	public static void clearAll()
	{
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;

		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			Vector <AnimationKeyFrame> kfs = kfp.getKeyFrames();
			Vector <AnimationKeyFrame> newKfs = new Vector<AnimationKeyFrame>();
			newKfs.add( kfs.elementAt( 0 ) );
			kfp.setKeyFrames( newKfs );
			//--- undo
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}
	//--- Clears all keyframes from iop andset first ones value to current frame value
	public static void freezeAllToCurrent()
	{
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;

		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			Vector <AnimationKeyFrame> newKfs = new Vector<AnimationKeyFrame>();
			float value = kfp.getValue( TimeLineController.getCurrentFrame() );
			newKfs.add( AnimationKeyFrame.createNewKeyframe( 0, value ) );
			kfp.setKeyFrames( newKfs );
			//--- undo
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}

	public static void addToAllInCurrent()
	{
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;

		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );
		int current = GUIComponents.keyFrameEditPanel.getFocusFrame();

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			float value = kfp.getValue( current );
			kfp.setValue( current, value );
			//--- undo
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}

	private static void addAnimateValueVectorParams( ImageOperation iop, Vector<KeyFrameParam> kfParams )
	{
		Vector<Param> iopParams = iop.getParameters();
		for( Object o : iopParams )
		{
			if( o instanceof AnimatedValueVectorParam )
			{
				Vector<AnimatedValue> animVals = ((AnimatedValueVectorParam) o ).get();
				for( AnimatedValue av : animVals )
					kfParams.add( (KeyFrameParam) av );
			}
		}
	}

	//-- clears all ketframes in current frame from currently selected iop
	public static void clearCurrent()
	{
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;
		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );
		int current = GUIComponents.keyFrameEditPanel.getFocusFrame();

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			kfp.removeKeyFrame( current );
			//--- undo
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}
	//--- Clears all keyframes from iop afterr current frame
	public static void clearAfterCurrent()
	{
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;

		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );
		int current = GUIComponents.keyFrameEditPanel.getFocusFrame() + 1;

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			Vector <AnimationKeyFrame> kfs = kfp.getKeyFrames();
			Vector <AnimationKeyFrame> newKfs = new Vector<AnimationKeyFrame>();
			//--- Frame at clipframe is 0 is alwys there
			newKfs.add( kfs.elementAt( 0 ) );
			//--- Add kfs after current
			for( int i = 1; i < kfs.size(); i++ )
			{
				AnimationKeyFrame kf = kfs.elementAt( i );
				if( iop.getMovieFrame( current ) > kf.getFrame() )
					newKfs.add( kf );
			}
			kfp.setKeyFrames( newKfs );
			//--- undo
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}
	//--- Clears all keyframes from iop before current frame
	public static void clearBeforeCurrent()
	{
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;

		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );
		int current = GUIComponents.keyFrameEditPanel.getFocusFrame() - 1;

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			Vector <AnimationKeyFrame> kfs = kfp.getKeyFrames();
			Vector <AnimationKeyFrame> newKfs = new Vector<AnimationKeyFrame>();
			//--- Frame at clipframe is 0 is alwys there
			newKfs.add( kfs.elementAt( 0 ) );
			//--- Add kfs after current
			for( int i = 1; i < kfs.size(); i++ )
			{
				AnimationKeyFrame kf = kfs.elementAt( i );
				if( iop.getMovieFrame( current ) < kf.getFrame() )
					newKfs.add( kf );
			}
			kfp.setKeyFrames( newKfs );
			//--- undo
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}

	public static void scaleAfterCurrent()
	{
		//--- Get user input
		String tc = TimeLineDisplayPanel.parseTimeCodeString( TimeLineController.getCurrentFrame(), 6, ProjectController.getFramesPerSecond() );
 		MTextField scaleTF = new MTextField( "Scale Factor", 125, Float.toString( 1.0f ) );

		MInputArea area = new MInputArea( "Scale from " + tc );
		area.add( scaleTF );

		MInputPanel panel = new MInputPanel( "Scale keyframes" );
		panel.add( area );

		int retVal = DialogUtils.showMultiInput( panel );
		if( retVal != DialogUtils.OK_OPTION ) return;

		float scale = 0;
		try
		{
			scale = Float.parseFloat( scaleTF.getStringValue() );
		}
		catch( Exception e )
		{
			return;
		}

		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;
		
		//--- Scale params
		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );
		int current = GUIComponents.keyFrameEditPanel.getFocusFrame();

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			Vector <AnimationKeyFrame> kfs = kfp.getKeyFrames();
			for( int i = 1; i < kfs.size(); i++ )//1 means first will never be hanled
			{
				AnimationKeyFrame kf = kfs.elementAt( i );
				if( iop.getBeginFrame() + kf.getFrame() < current )
					continue;
				int currentAsClipFrame = iop.getClipFrame( current );
				int newFrame = currentAsClipFrame + (int) Math.round( (kf.getFrame() - currentAsClipFrame) * scale );
							System.out.println(newFrame);
				if( newFrame <= 0 )
					continue;
				kf.setFrame( newFrame );		
			}

			AnimatedValue av = (AnimatedValue) kfp;
			av.sortKeyframes();

			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}

	public static void moveAfterCurrent()
	{
		//--- Get user input
		String tc = TimeLineDisplayPanel.parseTimeCodeString( TimeLineController.getCurrentFrame(), 6, ProjectController.getFramesPerSecond() );
		MTextField move = new MTextField( "Move by frames", 125, new Integer( 5 ) );

		MInputArea area = new MInputArea( "Move after " + tc );
		area.add( move );

		MInputPanel panel = new MInputPanel( "Move keyframes" );
		panel.add( area );

		int retVal = DialogUtils.showMultiInput( panel );
		if( retVal != DialogUtils.OK_OPTION ) return;

		int delta = 0;
		try
		{
			delta = move.getIntValue();
		}
		catch( Exception e )
		{
			return;
		}

		//--- Move key frames
		//--- Get iop in kf editor
		ImageOperation iop = GUIComponents.keyFrameEditPanel.getIOP();
		if( iop == null )
			return;
		
		Vector<KeyFrameParam> params = iop.getKeyFrameParams();
		addAnimateValueVectorParams( iop, params );
		int currentFrame = GUIComponents.keyFrameEditPanel.getFocusFrame() + 1;

		boolean first = true;
		for( KeyFrameParam kfp : params )
		{
			Vector <AnimationKeyFrame> kfs = kfp.getKeyFrames();
			//--- 1 means first keyframe will never be affected
			for( int i = 1; i < kfs.size(); i++ )
			{
				AnimationKeyFrame kf = kfs.elementAt( i );
				if( iop.getBeginFrame() + kf.getFrame() < currentFrame )
					continue;
				int newFrame = kf.getFrame() + delta; 
				if( newFrame <= 0 )
					continue;
				kf.setFrame( newFrame );

			}
		
			AnimatedValue av = (AnimatedValue) kfp;
			av.sortKeyframes();

			//--- Register only first as significant to package all as 
			//--- one undo.
			if( first )
			{
				first = false;
				kfp.getAsParam().registerUndo( true );
			}
			else
				kfp.getAsParam().registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
	}
	//---
	public static void keyframePreferences()
	{
		//--- Create editors and set values
		final String[] defOptions = { "linear","bezier" };
		final MComboBox defCb = new MComboBox( "Default interpolation", 200, 200, defOptions );
		//--- LINEAR = 1;
		//--- BEZIER = 2;
		defCb.setSelectedIndex( EditorPersistance.getIntPref( EditorPersistance.KF_DEF_INTERP ) - 1 );
		final MTextField defTens = new MTextField( "Default bezier tension", 200, 200,
			(new Float( EditorPersistance.getFloatPref( EditorPersistance.KF_DEF_TENS ) )).toString() );
		defTens.setTextFieldSize( 50 );

		MInputArea area = new MInputArea( "Defaults" );
		area.add( defCb );
		area.add( defTens );

		final MInputPanel panel = new MInputPanel( "Keyframe defaults" );
		panel.add( area );

		int retVal = DialogUtils.showMultiInput( panel, 500, 155 );
		if( retVal != DialogUtils.OK_OPTION ) 
			return;

		//--- Set values and write.
		//--- LINEAR = 1;
		//--- BEZIER = 2;
		EditorPersistance.setPref( EditorPersistance.KF_DEF_TENS, defTens.getStringValue() );
		EditorPersistance.setPref( EditorPersistance.KF_DEF_INTERP, defCb.getSelectedIndex() + 1 );
		EditorPersistance.write();
	}

	//------------------------------------------------------- render
	public static void setMotionBlur()
	{
		final String[] mboptions = { "on","off" };
		final MComboBox globalMB = new MComboBox( "Global motion blur", 75, mboptions );

		final MComboBox passes = new MComboBox( "Render passes", 75,  Blender.selectablePassesOpts );
		int selIndex = 0;
		int origVal = Blender.getPasses();
		for( int i = 0; i < Blender.selectablePasses.length; i++ )
		 	if( Blender.selectablePasses[ i ] == origVal ) selIndex = i;
		passes.setSelectedIndex( selIndex );
		final MTextField angle = new MTextField( "Shutter angle", 125, new Integer( Blender.getShutterAngle() ) );

		MInputArea mbArea = new MInputArea( "Motion blur" );
		mbArea.add( globalMB );
		mbArea.add( passes );
		mbArea.add( angle );

		final MInputPanel mbPanel = new MInputPanel( "Motion Blur" );
		mbPanel.add( mbArea );

		//--- Set initial state
		if( ProjectController.getMotionBlur() )
		{
			globalMB.setSelectedIndex( 0 );
			passes.setEnabled( true );
			angle.setEnabled( true );
		}
		else
		{
			globalMB.setSelectedIndex( 1 );
			passes.setEnabled( false );
			angle.setEnabled( false );
		}

		globalMB.addActionListener
		(
			new MActionListener() 
			{
				public void actionPerformed(ActionEvent e)
				{
 					if( globalMB.getSelectedIndex() == 0 )
					{
						passes.setEnabled( true );
						angle.setEnabled( true );
					}
					else
					{
						passes.setEnabled( false );
						angle.setEnabled( false );
					}
					mbPanel.repaint();
				}
			}
		);

		int retVal = DialogUtils.showMultiInput( mbPanel );
		if( retVal != DialogUtils.OK_OPTION ) return;

		int shutterVal = angle.getIntValue();

		//--- Check shutter value
		boolean shutterGood = false;
		if( shutterVal >= Blender.minShutterAngle && shutterVal <= Blender.maxShutterAngle )
			shutterGood = true;
		
		if( !shutterGood )
		{
			DialogUtils.showTwoStyleInfo( "Motion blur setting info","You have to set shutter value angle between " 
				+ Blender.minShutterAngle + " and " + Blender.maxShutterAngle + " degrees.",
				 DialogUtils.WARNING_MESSAGE);
			setMotionBlur();
		}
		else
		{
			int passVal = Blender.selectablePasses[ passes.getSelectedIndex() ];
			Blender.setPasses( passVal );
			Blender.setShutterAngle( shutterVal );

			if( globalMB.getSelectedIndex() == 0 )
				ProjectController.setMotionBlur( true );
			else
				ProjectController.setMotionBlur( false );
		}
	}

	public static void setFrameOutputSettings()
	{
		//--- Save settings
		String[] fileTypes = new String[ 2 ];
		fileTypes[ 0 ] = "png";
		fileTypes[ 1 ] = "jpg";

		MComboBox outputFileType = new MComboBox( "Output frame file type", 75, fileTypes );
		File targetFolder = RenderModeController.getWriteFolder();
		MFileSelect tfs = new MFileSelect( "Folder for frames", "Select folder for frames", 25, targetFolder, null );
		tfs.setType( JFileChooser.DIRECTORIES_ONLY );
		String fname = RenderModeController.getFrameName();
		if( fname == null ) fname = "frame";
		MTextField framename = new MTextField( "Frame name", 75, fname );
		String[] padOtps = { "3 digits","4 digits","5 digits", "no padding" };
		MComboBox pad = new MComboBox( "Zero padding", 75, padOtps );
		MCheckBox overWrite = new MCheckBox( "Overwrite without warnig", true );

		MInputArea save = new MInputArea( "Frame output settings" );
		save.add( outputFileType );
		save.add( tfs );
		save.add( framename );
		save.add( pad );
		save.add( overWrite );

		MInputPanel fosPanel = new MInputPanel( "Frame Output" );
		fosPanel.add( save );

		int retVal = DialogUtils.showMultiInput( fosPanel );
		if( retVal != DialogUtils.OK_OPTION ) return;

		//--- Set render values.
		//RenderModeController.setOutFrameType( (String) outputFileType.getValue() );
		RenderModeController.setFrameName( framename.getStringValue() );
		RenderModeController.setWriteFolder( tfs.getSelectedFile() );
		int zpad = pad.getSelectedIndex();
		if( zpad == 3 ) zpad = -1;
		else zpad = zpad + 3;
		RenderModeController.setZeroPadding( zpad );
	}

	public static void setThreadsAndBlenders()
	{		
 		String[] qualityOpts = { "normal","draft" };
		MComboBox quality = new MComboBox( "Render quality", 75, qualityOpts );

		MInputArea qArea = new MInputArea( "Quality" );
		qArea.add( quality );
		
		String[] mboptions = { "on","off" };
		MComboBox globalMB = new MComboBox( "Global motion blur", 75, mboptions );

		MComboBox passes = new MComboBox( "Render passes", 75,  Blender.selectablePassesOpts );
		int selIndex = 0;
		int origVal = Blender.getPasses();
		for( int i = 0; i < Blender.selectablePasses.length; i++ )
		 	if( Blender.selectablePasses[ i ] == origVal ) selIndex = i;
		passes.setSelectedIndex( selIndex );

		MTextField angle = new MTextField( "Shutter angle", 125, new Integer( Blender.getShutterAngle() ) );

		MInputArea mbArea = new MInputArea( "Motion blur" );
		mbArea.add( globalMB );
		mbArea.add( passes );
		mbArea.add( angle );
		
		//--- Quality and size
		int LEFT = 190;
		int RIGHT = 160;
 		String[] threadOpts = { "1","2", "3", "4" };
		MComboBox threads = new MComboBox( "Threads", LEFT, RIGHT, threadOpts );
		int currentThreads = RenderModeController.getRenderThreadsCount();
		threads.setSelectedIndex( currentThreads - 1 );

 		String[] blenderOpts = { "1","2", "3", "4" };
		MComboBox blenders = new MComboBox( "Blenders", LEFT, RIGHT, blenderOpts );
		int currentBlenders = RenderModeController.getBlendersCount();
		blenders.setSelectedIndex( currentBlenders - 1 );

		MInputArea ia = new MInputArea( "Parallerism" );
		ia.add( threads );
		ia.add( blenders );

		MInputPanel panel = new MInputPanel( "Rendering Settings" );
		panel.add( qArea );
		panel.add( mbArea );
		panel.add( ia );

		int retVal = DialogUtils.showMultiInput( panel, 450, 350 );
		if( retVal != DialogUtils.OK_OPTION ) return;

		RenderModeController.setRenderThreadsCount( threads.getSelectedIndex() + 1 );
		int newBlendersCount = blenders.getSelectedIndex() + 1;
		if( currentBlenders != newBlendersCount )
		{
			RenderModeController.setBlendersCount( newBlendersCount );
			Blender.initBlenders();
		}
		//--- Set render output values.
		RenderModeController.setWriteQuality( quality.getSelectedIndex() );
	}

	//--- ???????? values arent going anywhere ? 
	public static void setMemorySettings()
	{
		MTextInfo previewFrame = new MTextInfo( "Available preview frames", MemoryManager.getPreviewSizeEstimate() );
		//MTextInfo previewMem = new MTextInfo( "Available preview memory", MemoryManager.getMBString( MemoryManager.getPreviewSizeEstimate()) );
		MTextInfo freeMem = new MTextInfo( "Free memory after start-up",  MemoryManager.getMBString( MemoryManager.getAppFreeMem() ) );
		
		MInputArea museArea = new MInputArea( "Memory Use Estimates" );
		museArea.add( previewFrame );
		//museArea.add( previewMem );
		museArea.add( freeMem );
		
		MTextField maxMem = new MTextField( "Max. memory consumption MB", 60, 1024 );
		maxMem.setTextFieldSize( 60 );
		MInputArea mArea = new MInputArea( "Memory Settings" );
		mArea.add( maxMem );

 		MTextField cacheShare = new MTextField( "Cache / (cache + preview) %", 125, new Integer( (int) (( 1.0f - MemoryManager.PREVIEW_SHARE ) * 100.0f ) ));
 		cacheShare.setTextFieldSize( 60 );
		MTextField viewshare = new MTextField( "View Editor cache share %", 125, new Integer( ((int) (MemoryManager.VIEW_EDITOR_SHARE * 100.0f )) ));
		viewshare.setTextFieldSize( 60 );

		MInputArea cArea = new MInputArea( "Memory Cache" );
		cArea.add( cacheShare );
		cArea.add( viewshare );

		MInputPanel panel = new MInputPanel( "" );
		panel.add( museArea );
		panel.add( mArea );
		panel.add( cArea );

		int retVal = DialogUtils.showMultiInput( panel, 450, 330 );
		if( retVal != DialogUtils.OK_OPTION ) return;
	}

	//------------------------------------------------------ help
	public static void displayAbout()
	{
		JLabel logo = new JLabel( GUIResources.getIcon( GUIResources.phantomLogoSmall ) );
		JLabel version = new JLabel( "Ver. 0.4.0" ); 
		JLabel line1 = new JLabel( "Open source bitmap animation and" ); 
		JLabel line2 =  new JLabel( "compositing program." );
		JLabel line3 = new JLabel( "Licenced under GPL3." );

		version.setFont( GUIResources.BOLD_FONT_14 );
		line1.setFont( GUIResources.BASIC_FONT_11 );
		line2.setFont( GUIResources.BASIC_FONT_11 );
		line3.setFont( GUIResources.BASIC_FONT_11 );

		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS) );
		panel.add( logo );
		panel.add( Box.createRigidArea( new Dimension( 0, 4 ) ) );
		panel.add( version );
		panel.add( Box.createRigidArea( new Dimension( 0, 8 ) ) );
		panel.add( line1 );
		panel.add( line2 );
		panel.add( Box.createRigidArea( new Dimension( 0, 8 ) ) );
		panel.add( line3 );
		panel.add( Box.createHorizontalGlue() );

		DialogUtils.showPanelOKDialog( panel, "About Phantom2D", 340, 120 );
	}

}//end class
