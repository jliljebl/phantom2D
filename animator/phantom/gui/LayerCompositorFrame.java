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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import animator.phantom.controller.AppData;
import animator.phantom.controller.AppUtils;
import animator.phantom.controller.EditorPersistance;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.LayerCompositorApplication;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.keyaction.CTRLPressedAction;
import animator.phantom.controller.keyaction.CTRLReleasedAction;
import animator.phantom.controller.keyaction.CopyAction;
import animator.phantom.controller.keyaction.DeleteAction;
import animator.phantom.controller.keyaction.PlayStopAction;
import animator.phantom.controller.keyaction.KeyUtils;
import animator.phantom.controller.keyaction.NextLayerAction;
import animator.phantom.controller.keyaction.PasteAction;
import animator.phantom.controller.keyaction.RenderPreviewAction;
import animator.phantom.controller.keyaction.RenderPreviewFrameAction;
import animator.phantom.controller.keyaction.TimelineNextAction;
import animator.phantom.controller.keyaction.TimelinePrevAction;
import animator.phantom.controller.keyaction.TimelineZoomInAction;
import animator.phantom.controller.keyaction.TimelineZoomOutAction;
import animator.phantom.gui.keyframe.KFColumnPanel;
import animator.phantom.gui.keyframe.KFToolButtons;
import animator.phantom.gui.keyframe.KeyFrameEditorPanel;
import animator.phantom.gui.preview.PreViewControlPanel;
import animator.phantom.gui.preview.PreViewUpdater;
import animator.phantom.gui.timeline.TCDisplay;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.gui.timeline.TimeLineEditorLayout;
import animator.phantom.gui.timeline.TimeLineEditorPanel;
import animator.phantom.gui.timeline.TimeLineIOPColumnPanel2;
import animator.phantom.gui.timeline.TimeLineRowLayout;
import animator.phantom.gui.timeline.TimeLineToolButtons;
import animator.phantom.gui.timeline.TimeLineControls;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.gui.view.component.ViewEditor;
import animator.phantom.renderer.ImageOperation;

public class LayerCompositorFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JViewport flowViewPort;
	private JPanel contentPane = null;
	private JPanel editorsPane = null;

	private JPanel buttonRowHolder;
	private JPanel bottomRow;
	private PreViewUpdater previewUpdater;

	private JPanel paramEditHolder;

	public JToggleButton timelineButton;
	public JToggleButton splineButton;

	private	JPanel timelinePanel;
	private JPanel keyEditorPanel;
	private TimeLineControls tlineControls;

	private JPanel tlineButtonsPane;
	private JPanel kfButtonsPane;

	private ParamEditFrame editFrame;

	private JPanel screenViewsPanel;
	private JPanel viewPanel;
	private JScrollPane viewScrollPane;

	private JPanel editSwitchButtons;

	private JLabel projectInfoLabel;
	
	private boolean initializing = false;
	private int displayedEditor = 0;

	public LayerCompositorFrame()
	{
		super();
		setVisible( false );//set visible when properly initialized.
	}

	//--- Inits editor. Called when program started or
	//--- project loaded.
	public void initializeEditor()
	{
		setTitle(  AppData.getProject().getName() + " - LayerCompositor" );

		try
		{
			setIconImage( GUIResources.getResourceBufferedImage( GUIResources.phantomIcon ) );
		}
		catch( Exception e ){}

		createGui();
		requestFocus();
	}

	//--- Create gui. All components are recreated.
	private void createGui()
	{
		AppUtils.printTitle( "INIT GUI" );

		initializing = true;

		//--- main menu
		LayerCompositorMenu menuBar = new LayerCompositorMenu();
		setJMenuBar( menuBar );

		//-------------------------------------- Timecode display
		TCDisplay timecodeDisplay = new TCDisplay("00:00:00");

		//---------------------------------------- view editor
		ViewEditor viewEdit = new ViewEditor( ProjectController.getCurrentScreenSize() );

		screenViewsPanel = new JPanel();
		screenViewsPanel.setLayout( new BigEditorsLayout() );

		ViewControlButtons viewControlButtons = new ViewControlButtons(screenViewsPanel, timecodeDisplay);

		viewScrollPane = new JScrollPane( viewEdit,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		JScrollBar vb = viewScrollPane.getVerticalScrollBar();
		vb.setUI(new PHScrollUI());
		JScrollBar hb = viewScrollPane.getHorizontalScrollBar();
		hb.setUI(new PHScrollUI());

		viewPanel = new JPanel();
		viewPanel.add( viewScrollPane );

		//----------------------------------------------- preview
		previewUpdater = new PreViewUpdater();
		PreViewControlPanel previewControl = new PreViewControlPanel();

		//--------------------------------------------------- Panel holding all the middle row buttons
		buttonRowHolder = new JPanel();
		buttonRowHolder.setLayout(new ButtonsRowLayout());
		buttonRowHolder.add( viewControlButtons );

		//-------------------------------------------- param edit
		editFrame = new ParamEditFrame();

		paramEditHolder = new JPanel();
		paramEditHolder.setLayout(new BoxLayout( paramEditHolder, BoxLayout.Y_AXIS));
		paramEditHolder.add( editFrame );

		JPanel paramEditPanelPanel = new JPanel();
		paramEditPanelPanel.setLayout(new BoxLayout( paramEditPanelPanel, BoxLayout.Y_AXIS));

		paramEditPanelPanel.add( Box.createRigidArea( new Dimension( 0, 4 ) ) );
		paramEditPanelPanel.add( previewControl );
		paramEditPanelPanel.add( Box.createRigidArea( new Dimension( 0, 4 ) ) );
		paramEditPanelPanel.add( paramEditHolder );

		//--------------------------------------------- view editor + button + param edit
		screenViewsPanel.add( viewPanel );
		screenViewsPanel.add( buttonRowHolder );
		screenViewsPanel.add( paramEditPanelPanel );

		//--------------------------------------- timeline editor
		TimeLineDisplayPanel timeLineDisplay = new TimeLineDisplayPanel();
		TimeLineIOPColumnPanel2 iopColumn = new TimeLineIOPColumnPanel2();
		TimeLineEditorPanel timelineEditor = new TimeLineEditorPanel();

		JLabel compNameLabel = new JLabel();
		JPanel scaleStrip = new JPanel();
		scaleStrip.setLayout( new TimeLineRowLayout( compNameLabel, timeLineDisplay ) );
		scaleStrip.add( compNameLabel );
		scaleStrip.add( timeLineDisplay ); 

		JPanel timeLineEditorStrip = new JPanel();
		timeLineEditorStrip.setLayout( new TimeLineRowLayout( iopColumn, timelineEditor ) );
		timeLineEditorStrip.add( iopColumn );
		timeLineEditorStrip.add( timelineEditor );

		Dimension newSize = new Dimension(1000, 1000);
		timeLineEditorStrip.setSize( newSize );
		timeLineEditorStrip.setPreferredSize( newSize );
		timeLineEditorStrip.setMinimumSize( newSize );
		timeLineEditorStrip.setMaximumSize( newSize );

		JScrollPane timeEditorScrollPane = new JScrollPane( timeLineEditorStrip,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		JScrollBar tvb = timeEditorScrollPane.getVerticalScrollBar();
		tvb.setUI(new PHScrollUI());

		timelinePanel = new JPanel();
		timelinePanel.setLayout( new TimeLineEditorLayout( LayerCompositorApplication.SMALL_WINDOW_WIDTH ) );
		timelinePanel.add( scaleStrip );
		timelinePanel.add( timeEditorScrollPane );

		//----------------------------------------------- keyframe editor
		KFToolButtons kfButtons = new KFToolButtons();
		TimeLineDisplayPanel KFtimeLineDisplay = new TimeLineDisplayPanel();
		KFColumnPanel kfColumnPanel = new KFColumnPanel();
		KeyFrameEditorPanel kfEditPanel = new KeyFrameEditorPanel();

		JPanel kfNamePanel = new JPanel();
		kfNamePanel.setLayout(new BoxLayout( kfNamePanel, BoxLayout.X_AXIS));

		JPanel innerPanel = new JPanel();
		JPanel namePanel = new JPanel();
		innerPanel.setLayout(new BoxLayout( innerPanel, BoxLayout.Y_AXIS));
		innerPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
		innerPanel.add( namePanel );
		innerPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

		JLabel nameLabel = new JLabel();
		nameLabel.setForeground( Color.black );
		namePanel.add( nameLabel );
		namePanel.setBackground( GUIColors.selectedColor );
		namePanel.setOpaque( true );

		kfNamePanel.add( innerPanel );
		kfNamePanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

		JPanel keyButtonsStrip = new JPanel();
		keyButtonsStrip.add( kfNamePanel );

		JPanel keyEditorScaleStrip = new JPanel();
 		JLabel layerNameLabel = new JLabel();
		keyEditorScaleStrip.setLayout( new TimeLineRowLayout( layerNameLabel, KFtimeLineDisplay ) );
		keyEditorScaleStrip.add( layerNameLabel );
		keyEditorScaleStrip.add( KFtimeLineDisplay );

		JPanel keyFrameEditorStrip = new JPanel();
		keyFrameEditorStrip.setLayout( new TimeLineRowLayout( kfColumnPanel, kfEditPanel ) );
		keyFrameEditorStrip.add( kfColumnPanel );
		keyFrameEditorStrip.add( kfEditPanel );

		JScrollPane keyEditorScrollPane = new JScrollPane( keyFrameEditorStrip,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollBar kvb = keyEditorScrollPane.getVerticalScrollBar();
		kvb.setUI(new PHScrollUI());

		keyEditorPanel = new JPanel();
		keyEditorPanel.setLayout( new TimeLineEditorLayout( LayerCompositorApplication.SMALL_WINDOW_WIDTH ) );
		keyEditorPanel.add( keyEditorScaleStrip );
		keyEditorPanel.add( keyEditorScrollPane );

		//-------------------------------------- editor switch buttons.
		timelineButton = PHButtonFactory.getToggleButton("Timeline", 90 );
		splineButton = PHButtonFactory.getToggleButton("Spline", 90 );

		timelineButton.setFont( GUIResources.BIG_BUTTONS_FONT );
		splineButton.setFont( GUIResources.BIG_BUTTONS_FONT );

		timelineButton.addActionListener( this );
		splineButton.addActionListener( this );

		ButtonGroup beGroup = new ButtonGroup();
		beGroup.add( timelineButton );
		beGroup.add( splineButton );
		beGroup.setSelected( timelineButton.getModel(), true );

		editSwitchButtons = new JPanel();
		editSwitchButtons.setLayout(new BoxLayout( editSwitchButtons, BoxLayout.X_AXIS));
		editSwitchButtons.add( timelineButton );
		editSwitchButtons.add( splineButton );

		tlineControls = new TimeLineControls();

		//---------------------------------------------------------- editor buttons panes
		TimeLineToolButtons timeLineToolButtons = new TimeLineToolButtons();
		tlineButtonsPane = new JPanel();
		tlineButtonsPane.setLayout(new BoxLayout( tlineButtonsPane, BoxLayout.X_AXIS));
		tlineButtonsPane.add( timeLineToolButtons );

		kfButtonsPane = new JPanel();
		kfButtonsPane.setLayout(new BoxLayout( kfButtonsPane, BoxLayout.X_AXIS));
		kfButtonsPane.add( kfButtons );

		//------------------------------------------- bottom row
		bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout( bottomRow, BoxLayout.X_AXIS));

		//------------------------------------------------------------------- build all
		contentPane = new JPanel();

		editorsPane = new JPanel();
		editorsPane.setLayout( new EditorsLayout() );
		editorsPane.add( timelinePanel );

		AnimatorFrameLayout frameLayout = new AnimatorFrameLayout( 	screenViewsPanel,
										editorsPane,
										bottomRow );
		contentPane.setLayout( frameLayout );
 		contentPane.add( screenViewsPanel );
		contentPane.add( editorsPane );
		contentPane.add( bottomRow );

		projectInfoLabel = new JLabel();
		projectInfoLabel.setFont( GUIResources.BOLD_FONT_12 );

		//--- global keyactions.
		KeyUtils.clearGlobalActions();
		KeyUtils.setGlobalAction( new CTRLPressedAction(), "ctrl pressed CONTROL" );
		KeyUtils.setGlobalAction( new CTRLReleasedAction(), "released CONTROL" );
		KeyUtils.setGlobalAction( new PlayStopAction(), EditorPersistance.PLAY_STOP_ACTION_KEY_SC_DEFAULT );
		KeyUtils.setGlobalAction( new RenderPreviewAction(), EditorPersistance.RENDER_PRE_KEY_SC_DEFAULT );
		KeyUtils.setGlobalAction( new RenderPreviewFrameAction(), EditorPersistance.RENDER_PRE_FRAME_KEY_SC_DEFAULT );
		 /*
		KeyUtils.setGlobalAction( new FlowArrangeAction(), EditorPersistance.FLOW_ARRANGE_KEY_SC_DEFAULT );
		KeyUtils.setGlobalAction( new FlowConnectAction(), EditorPersistance.FLOW_CONNECT_KEY_SC_DEFAULT );
		KeyUtils.setGlobalAction( new FlowDisconnectAction(), EditorPersistance.FLOW_DISCONNECT_KEY_SC_DEFAULT );
		*/
		KeyUtils.setGlobalAction( new TimelineZoomInAction(), "control UP" );
		KeyUtils.setGlobalAction( new TimelineZoomOutAction(), "control DOWN" );
		KeyUtils.setGlobalAction( new TimelinePrevAction(), EditorPersistance.TLINE_PREV_KEY_SC_DEFAULT );
		KeyUtils.setGlobalAction( new TimelineNextAction(), EditorPersistance.TLINE_NEXT_KEY_SC_DEFAULT );
		KeyUtils.setGlobalAction( new NextLayerAction(), EditorPersistance.NEXT_LAYER_KEY_SC_DEFAULT );

		//--- Ancestor focus actions
		KeyUtils.setAncestorFocusAction( keyEditorPanel, new CopyAction(), "control C" );
		KeyUtils.setAncestorFocusAction( keyEditorPanel, new PasteAction(), "control V" );
		KeyUtils.setAncestorFocusAction( keyEditorPanel, new DeleteAction(), "DELETE" );

		//--- Connect GUI components to be accessed elsewhere.
		GUIComponents.animatorFrame = this;
		GUIComponents.timeLineIOPColumnPanel = iopColumn;
		GUIComponents.timeLineEditorPanel = timelineEditor;
		GUIComponents.timeLineScaleDisplays.add( timeLineDisplay );
		GUIComponents.timeLineScaleDisplays.add( KFtimeLineDisplay );
		GUIComponents.tcDisplay = timecodeDisplay;
		ParamEditController.paramEditFrame = editFrame;
		AppData.setParamEditFrame( editFrame );
		GUIComponents.viewEditor = viewEdit;
		GUIComponents.kfColumnPanel = kfColumnPanel;
		GUIComponents.keyFrameEditPanel = kfEditPanel;
		GUIComponents.kfNamePanel = namePanel;
		GUIComponents.keyEditorContainerPanel = keyEditorPanel;
		GUIComponents.previewUpdater = previewUpdater;
		GUIComponents.previewControls = previewControl;
		GUIComponents.viewControlButtons = viewControlButtons;
		GUIComponents.viewScrollPane = viewScrollPane;
		GUIComponents.kfControl = kfButtons;
		GUIComponents.tlineControls = tlineControls;
		GUIComponents.projectInfoLabel = projectInfoLabel;
		GUIComponents.animatorMenu = menuBar;
		GUIComponents.compNameLabel = compNameLabel;
		GUIComponents.layerNameLabel = layerNameLabel;
		
		TimeLineController.initClipEditorGUI();

		//--- Remove all components (why? reload?)
		getContentPane().removeAll();

		add( contentPane );

		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );

		displayTimeline();

		AppUtils.printTitle( "INIT GUI DONE!" );
		initializing = false;

		MinimizeSetTimer minimSet = new MinimizeSetTimer( this );
		minimSet.start();
	}

	//----------------------------------------------- Editor change buttons.
	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == timelineButton ) displayTimeline();
		if( e.getSource() == splineButton ) displaySpline();
	}

	public void displayTimeline()
	{
		editorsPane.removeAll();
		editorsPane.add( timelinePanel );

		bottomRow.removeAll();
		bottomRow.add( Box.createRigidArea(new Dimension( 12, 0 ) ) );
		bottomRow.add( editSwitchButtons );
		bottomRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
		bottomRow.add( tlineButtonsPane );
		tlineControls.addComponentsToPanel( bottomRow );
		bottomRow.add( Box.createHorizontalGlue() );
		bottomRow.add( projectInfoLabel );
		bottomRow.add( Box.createRigidArea(new Dimension( 12, 0 ) ) );

		if( !initializing ) bottomRow.validate();
		if( !initializing ) editorsPane.validate();
		if( !initializing ) repaint();
	}

	public void displaySpline()
	{
		editorsPane.removeAll();
		editorsPane.add( keyEditorPanel );

		bottomRow.removeAll();
		bottomRow.add( Box.createRigidArea(new Dimension( 12, 0 ) ) );
		bottomRow.add( editSwitchButtons );
		bottomRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
		bottomRow.add( kfButtonsPane );
		tlineControls.addComponentsToPanel( bottomRow );
		bottomRow.add( Box.createHorizontalGlue() );
		bottomRow.add( projectInfoLabel );
		bottomRow.add( Box.createRigidArea(new Dimension( 12, 0 ) ) );

		ImageOperation editedIOP = GUIComponents.keyFrameEditPanel.getIOP();
		if ( editedIOP != null )
			GUIComponents.layerNameLabel.setText(editedIOP.getName());
		else
			GUIComponents.layerNameLabel.setText("");
		
		if( !initializing ) bottomRow.validate();
		if( !initializing ) editorsPane.validate();
		if( !initializing ) repaint();
	}

	public void setEditorTabSelected( int index )
	{
		PreviewController.stopPlaybackRequest();
		displayedEditor = index;
	}

	public int getSelectedEditorTab()
	{
		return displayedEditor;
	}

	//------------------------------------------------ render flow view scroll stuff for box placement
	//--- Return x,y of left up corner postion of viewport in RenderFlowViewPanel
	public Point getScrollPos(){ return flowViewPort.getViewPosition(); }
	//--- Return middlepoint for current viewport size.
	public Point getViewPortMiddlePoint()
	{
		 return new Point( flowViewPort.getExtentSize().width / 2,
					flowViewPort.getExtentSize().height / 2 );
	}
	//--- Returns view port size.
	public Dimension getViewPortSize(){ return flowViewPort.getExtentSize(); }

	public Dimension getViewEditorSize()
	{
		return viewScrollPane.getViewport().getExtentSize();
	}

	public void centerViewEditor()
	{
		Rectangle bounds = viewScrollPane.getViewport().getViewRect();
		Dimension size = viewScrollPane.getViewport().getViewSize();

		int x = (size.width - bounds.width) / 2;
		int y = (size.height - bounds.height) / 2;

		viewScrollPane.getViewport().setViewPosition(new Point(x, y));
	}

	//--- Used to avoid opening flicker.
	public class MinimizeSetTimer extends Timer
	{
		private SetMinimizeEvent setEvent;
		private JFrame animFrame;

		public MinimizeSetTimer( JFrame frame )
		{
			animFrame = frame;
		}

		public void start()
		{
			setEvent = new SetMinimizeEvent();
			schedule( setEvent, (long) 3000 );
		}

		class SetMinimizeEvent extends TimerTask
		{
			public void run()
			{
				animFrame.setMinimumSize( new Dimension( 1150, 700 ));
			}
		}
	}

	static JPanel packVerticalLinedUp( Component c )
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.Y_AXIS));
		p.add( Box.createVerticalGlue() );
		p.add( c );
		p.add( Box.createVerticalGlue() );
		return p;
	}

}//end class
