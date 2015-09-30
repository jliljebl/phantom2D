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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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

import animator.phantom.controller.AppUtils;
import animator.phantom.controller.Application;
import animator.phantom.controller.EditorPersistance;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.keyaction.CTRLPressedAction;
import animator.phantom.controller.keyaction.CTRLReleasedAction;
import animator.phantom.controller.keyaction.CopyAction;
import animator.phantom.controller.keyaction.DeSelectAllAction;
import animator.phantom.controller.keyaction.DeleteAction;
import animator.phantom.controller.keyaction.DisplayClipEditorAction;
import animator.phantom.controller.keyaction.DisplayFlowEditorAction;
import animator.phantom.controller.keyaction.DisplayKFEditorAction;
import animator.phantom.controller.keyaction.FlowArrangeAction;
import animator.phantom.controller.keyaction.FlowConnectAction;
import animator.phantom.controller.keyaction.FlowDisconnectAction;
import animator.phantom.controller.keyaction.KeyUtils;
import animator.phantom.controller.keyaction.NextLayerAction;
import animator.phantom.controller.keyaction.PasteAction;
import animator.phantom.controller.keyaction.RenderPreviewAction;
import animator.phantom.controller.keyaction.RenderPreviewFrameAction;
import animator.phantom.controller.keyaction.SelectAllAction;
import animator.phantom.controller.keyaction.TimelineNextAction;
import animator.phantom.controller.keyaction.TimelinePrevAction;
import animator.phantom.controller.keyaction.TimelineZoomInAction;
import animator.phantom.controller.keyaction.TimelineZoomOutAction;
import animator.phantom.gui.flow.FlowBox;
import animator.phantom.gui.flow.FlowEditPanel;
import animator.phantom.gui.flow.RenderFlowViewButtons;
import animator.phantom.gui.keyframe.KFColumnPanel;
import animator.phantom.gui.keyframe.KFToolButtons;
import animator.phantom.gui.keyframe.KeyFrameEditorPanel;
import animator.phantom.gui.preview.PreViewControlPanel;
import animator.phantom.gui.preview.PreViewUpdater;
//import animator.phantom.gui.preview.PreViewPanel;
import animator.phantom.gui.timeline.NormalizedTimelineDisplay;
import animator.phantom.gui.timeline.TCDisplay;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.gui.timeline.TimeLineEditButtons;
import animator.phantom.gui.timeline.TimeLineEditorLayout;
import animator.phantom.gui.timeline.TimeLineEditorPanel;
import animator.phantom.gui.timeline.TimeLineIOPColumnPanel;
import animator.phantom.gui.timeline.TimeLineRowLayout;
import animator.phantom.gui.timeline.TimeLineToolButtons;
import animator.phantom.gui.timeline.TimeLineControls;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.gui.view.component.ViewEditor;

public class AnimatorFrame extends JFrame implements ActionListener
{
	private JViewport flowViewPort;
	private JPanel contentPane = null;
	private JPanel editorsPane = null;

	private JPanel buttonRowHolder;
	private JPanel middleRow;
	private JPanel bottomRow;
	private PreViewUpdater previewUpdater;

	private RenderAreaPanel renderArea;
	private BinsAreaPanel binAreaPanel;
	private JPanel topLeftHolder;
	private JPanel bottomLeftHolder;
			
	public JToggleButton flowButton;
	public JToggleButton timelineButton;
	public JToggleButton splineButton;

	private JPanel flowHolder;
	private	JPanel timelinePanel;
	private JPanel keyEditorPanel;
	private TimeLineControls tlineControls;
	
	private JPanel flowButtonsPane;
	private JPanel tlineButtonsPane;
	private JPanel kfButtonsPane;
	
	private ParamEditFrame editFrame;
	private NodesPanel nodesPanel;
		
	private FlowEditPanel renderFlowPanel;

	private JPanel screenViewsPanel;
	private JPanel viewPanel;

	private JPanel editSwitchButtons;

	private boolean initializing = false;
	private int displayedEditor = 0;
		
	public AnimatorFrame()
	{
		super();
		setVisible( false );//set visible when properly initialized.
	}

	//--- Inits editor. Called when program started or
	//--- project loaded.
	public void initializeEditor()
	{
		setTitle(  ProjectController.getName() + " - Phantom2D" );
 
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

		//--- app menu
		AnimatorMenu menuBar = new AnimatorMenu();
		setJMenuBar( menuBar );

		//------------------------------ media panel
		binAreaPanel = new BinsAreaPanel( ProjectController.getBins(), this );
		renderArea = new RenderAreaPanel();


		topLeftHolder = new JPanel();
		topLeftHolder.setLayout(new BoxLayout( topLeftHolder, BoxLayout.Y_AXIS));
		topLeftHolder.add( binAreaPanel );

		JPanel topLeftPanel = new JPanel();
		topLeftPanel.setLayout(new BoxLayout( topLeftPanel, BoxLayout.Y_AXIS));
		topLeftPanel.add( Box.createRigidArea( new Dimension( 0, 6 ) ) );
		topLeftPanel.add( topLeftHolder );
		
		//----------------------------------- flow Editor
		int flowW = EditorPersistance.getIntPref( EditorPersistance.FLOW_WIDTH );
		int flowH = EditorPersistance.getIntPref( EditorPersistance.FLOW_HEIGHT );

 		renderFlowPanel = new FlowEditPanel( flowW, flowH );
		Vector<FlowBox> boxes = ProjectController.getBoxes();
		renderFlowPanel.loadBoxes( boxes );

		RenderFlowViewButtons renderFlowButtons = new RenderFlowViewButtons( this );

		JScrollPane flowScrollPane = new JScrollPane( renderFlowPanel,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		flowScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		JScrollBar vbf = flowScrollPane.getVerticalScrollBar();
		vbf.setUI( new PHScrollUI() );
		JScrollBar hbf = flowScrollPane.getHorizontalScrollBar();
		hbf.setUI( new PHScrollUI() );
		
		flowHolder = new JPanel();
		flowHolder.add( flowScrollPane );
		flowViewPort = flowScrollPane.getViewport();

		//---------------------------------------- view editor
		ViewEditor viewEdit = new ViewEditor( ProjectController.getScreenSize() );

		screenViewsPanel = new JPanel();
		screenViewsPanel.setLayout( new BigEditorsLayout() );

		ViewControlButtons viewControlButtons = new ViewControlButtons(screenViewsPanel);

		JScrollPane viewScrollPane = new JScrollPane( viewEdit,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		JScrollBar vb = viewScrollPane.getVerticalScrollBar();
		vb.setUI(new PHScrollUI());
		JScrollBar hb = viewScrollPane.getHorizontalScrollBar();
		hb.setUI(new PHScrollUI());
		
		viewPanel = new JPanel();
		viewPanel.add( viewScrollPane );

		//--------------------------------------------------- Panel holding all the middle row buttons
		buttonRowHolder = new JPanel();
		buttonRowHolder.setLayout(new BoxLayout( buttonRowHolder, BoxLayout.X_AXIS));
		buttonRowHolder.add( viewControlButtons );
		buttonRowHolder.add( Box.createHorizontalGlue() );
		
		//--------------------------------------------- view editor + button row panel
		screenViewsPanel.add( viewPanel );
		screenViewsPanel.add( buttonRowHolder );

		//-------------------------------------------- bottom left panel
		editFrame = new ParamEditFrame();
		nodesPanel = new NodesPanel();

		bottomLeftHolder = new JPanel();
		bottomLeftHolder.setLayout(new BoxLayout( bottomLeftHolder, BoxLayout.Y_AXIS));
		bottomLeftHolder.add( editFrame );
		
		JPanel bottomLeftPanel = new JPanel();
		bottomLeftPanel.setLayout(new BoxLayout( bottomLeftPanel, BoxLayout.Y_AXIS));

		bottomLeftPanel.add( Box.createRigidArea( new Dimension( 0, 4 ) ) );
		bottomLeftPanel.add( bottomLeftHolder );

		//----------------------------------------------- preview
		previewUpdater = new PreViewUpdater();

		TimeLineToolButtons timeLineToolButtons = new TimeLineToolButtons();
		NormalizedTimelineDisplay normTline = new NormalizedTimelineDisplay();
		TCDisplay timecodeDisplay = new TCDisplay("00:00:00");
		
		JPanel cNTLine_H = new JPanel();
		cNTLine_H.setLayout(new BoxLayout(cNTLine_H, BoxLayout.X_AXIS));
		cNTLine_H.add( normTline );
		JPanel cNTLine = packVerticalLinedUp( cNTLine_H );
		
		PreViewControlPanel previewControl = new PreViewControlPanel( cNTLine, timecodeDisplay);
		
		//--------------------------------------- timeline editor
		TimeLineDisplayPanel timeLineDisplay = new TimeLineDisplayPanel();
		TimeLineIOPColumnPanel iopColumn = new TimeLineIOPColumnPanel();
		TimeLineEditorPanel timelineEditor = new TimeLineEditorPanel();
		TimeLineEditButtons timeLineEditButtons = new TimeLineEditButtons();

		JPanel timeDummyPanelTop = new JPanel();
		JPanel scaleStrip = new JPanel();
		scaleStrip.setLayout( new TimeLineRowLayout( timeDummyPanelTop, timeLineDisplay ) );
		scaleStrip.add( timeDummyPanelTop );
		scaleStrip.add( timeLineDisplay );

		JPanel timeLineEditorStrip = new JPanel();
		timeLineEditorStrip.setLayout( new TimeLineRowLayout( iopColumn, timelineEditor ) );
		timeLineEditorStrip.add( iopColumn );
		timeLineEditorStrip.add( timelineEditor );

		JScrollPane timeEditorScrollPane = new JScrollPane( timeLineEditorStrip,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		timelinePanel = new JPanel();
		timelinePanel.setLayout( new TimeLineEditorLayout( Application.SMALL_WINDOW_WIDTH ) );
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
 		JPanel iopLabel = new JPanel();
		keyEditorScaleStrip.setLayout( new TimeLineRowLayout( iopLabel, KFtimeLineDisplay ) );
		keyEditorScaleStrip.add( iopLabel );
		keyEditorScaleStrip.add( KFtimeLineDisplay );

		JPanel keyFrameEditorStrip = new JPanel();
		keyFrameEditorStrip.setLayout( new TimeLineRowLayout( kfColumnPanel, kfEditPanel ) );
		keyFrameEditorStrip.add( kfColumnPanel );
		keyFrameEditorStrip.add( kfEditPanel );

		JScrollPane keyEditorScrollPane = new JScrollPane( keyFrameEditorStrip,
			 ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
			 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		keyEditorPanel = new JPanel();
		keyEditorPanel.setLayout( new TimeLineEditorLayout( Application.SMALL_WINDOW_WIDTH ) );
		keyEditorPanel.add( keyEditorScaleStrip );
		keyEditorPanel.add( keyEditorScrollPane );

		//-------------------------------------- editor switch buttons.
		flowButton = PHButtonFactory.getToggleButton("Composition", 90 );
		timelineButton = PHButtonFactory.getToggleButton("Timeline", 90 );
		splineButton = PHButtonFactory.getToggleButton("Spline", 90 );

		flowButton.setFont( GUIResources.BIG_BUTTONS_FONT );
		timelineButton.setFont( GUIResources.BIG_BUTTONS_FONT );
		splineButton.setFont( GUIResources.BIG_BUTTONS_FONT );

		flowButton.addActionListener( this );
		timelineButton.addActionListener( this );
		splineButton.addActionListener( this );
		
		ButtonGroup beGroup = new ButtonGroup();
		beGroup.add( flowButton );
		beGroup.add( timelineButton );
		beGroup.add( splineButton );
		beGroup.setSelected( flowButton.getModel(), true );

		editSwitchButtons = new JPanel();
		editSwitchButtons.setLayout(new BoxLayout( editSwitchButtons, BoxLayout.X_AXIS));
		editSwitchButtons.add( flowButton );
		editSwitchButtons.add( timelineButton );
		editSwitchButtons.add( splineButton );

		tlineControls = new TimeLineControls();
		 
		//---------------------------------------------------------- editor buttons panes
		flowButtonsPane = new JPanel();
		flowButtonsPane.setLayout( new BoxLayout( flowButtonsPane, BoxLayout.X_AXIS));
		flowButtonsPane.add( Box.createRigidArea(new Dimension( 5, 0 ) ) );
		flowButtonsPane.add( renderFlowButtons );

		tlineButtonsPane = new JPanel();
		tlineButtonsPane.setLayout(new BoxLayout( tlineButtonsPane, BoxLayout.X_AXIS));
		tlineButtonsPane.add( timeLineToolButtons );
		tlineButtonsPane.add( timeLineEditButtons );

		kfButtonsPane = new JPanel();
		kfButtonsPane.setLayout(new BoxLayout( kfButtonsPane, BoxLayout.X_AXIS));
		kfButtonsPane.add( Box.createRigidArea(new Dimension( 5, 0 ) ) );
		kfButtonsPane.add( kfButtons );

		//------------------------------------------- middle row, left side
		middleRow = new JPanel();
		middleRow.setLayout(new BoxLayout( middleRow, BoxLayout.X_AXIS));
		middleRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
 		buttonRowHolder.add( Box.createHorizontalGlue() );
		buttonRowHolder.add( middleRow );
		
		//------------------------------------------- bottom row
		bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout( bottomRow, BoxLayout.X_AXIS));
		bottomRow.add( editSwitchButtons );
		bottomRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
		bottomRow.add( flowButtonsPane );
		bottomRow.add( Box.createHorizontalGlue() );

		//------------------------------------------------------------------- build all
		contentPane = new JPanel();
		
		editorsPane = new JPanel();
		editorsPane.setLayout( new EditorsLayout() );
		editorsPane.add( flowHolder );
		 
		AnimatorFrameLayout frameLayout = new AnimatorFrameLayout( 	screenViewsPanel,
										editorsPane,
										bottomRow );
		contentPane.setLayout( frameLayout );
 		contentPane.add( screenViewsPanel );
		contentPane.add( editorsPane );
		contentPane.add( bottomRow );
		
		JPanel realContentPanel = new JPanel();
		ContentPaneLayout rclayout = new ContentPaneLayout();
		realContentPanel.setLayout( rclayout );
		realContentPanel.add( topLeftPanel );
		realContentPanel.add( bottomLeftPanel );
		realContentPanel.add( previewControl );
		realContentPanel.add( contentPane );

		//--- global keyactions.
		KeyUtils.clearGlobalActions();
		KeyUtils.setGlobalAction( new CTRLPressedAction(), "ctrl pressed CONTROL" );
		KeyUtils.setGlobalAction( new CTRLReleasedAction(), "released CONTROL" );
		KeyUtils.setGlobalAction( new DisplayFlowEditorAction(), EditorPersistance.getStringPref( EditorPersistance.DISP_FLOW_KEY_SC ));
		KeyUtils.setGlobalAction( new DisplayClipEditorAction(),  EditorPersistance.getStringPref( EditorPersistance.DISP_CLIP_KEY_SC ) );
		KeyUtils.setGlobalAction( new DisplayKFEditorAction(),  EditorPersistance.getStringPref( EditorPersistance.DISP_KF_KEY_SC ) );
		KeyUtils.setGlobalAction( new RenderPreviewAction(), EditorPersistance.getStringPref( EditorPersistance.RENDER_PRE_KEY_SC ));
		KeyUtils.setGlobalAction( new RenderPreviewFrameAction(), EditorPersistance.getStringPref( EditorPersistance.RENDER_PRE_FRAME_KEY_SC ));
		KeyUtils.setGlobalAction( new FlowArrangeAction(), EditorPersistance.getStringPref( EditorPersistance.FLOW_ARRANGE_KEY_SC ) );
		KeyUtils.setGlobalAction( new FlowConnectAction(), EditorPersistance.getStringPref( EditorPersistance.FLOW_CONNECT_KEY_SC ) );
		KeyUtils.setGlobalAction( new FlowDisconnectAction(), EditorPersistance.getStringPref( EditorPersistance.FLOW_DISCONNECT_KEY_SC ) );
		KeyUtils.setGlobalAction( new TimelineZoomInAction(), "control UP" );
		KeyUtils.setGlobalAction( new TimelineZoomOutAction(), "control DOWN" );
		KeyUtils.setGlobalAction( new TimelinePrevAction(), EditorPersistance.getStringPref( EditorPersistance.TLINE_PREV_KEY_SC ) );
		KeyUtils.setGlobalAction( new TimelineNextAction(), EditorPersistance.getStringPref( EditorPersistance.TLINE_NEXT_KEY_SC ) );
		KeyUtils.setGlobalAction( new NextLayerAction(), EditorPersistance.getStringPref( EditorPersistance.NEXT_LAYER_KEY_SC ));

		//---  ancestor focus actions
		KeyUtils.setAncestorFocusAction( keyEditorPanel, new CopyAction(), "control C" );
		KeyUtils.setAncestorFocusAction( keyEditorPanel, new PasteAction(), "control V" );
		KeyUtils.setAncestorFocusAction( keyEditorPanel, new DeleteAction(), "DELETE" );
		KeyUtils.setAncestorFocusAction( binAreaPanel, new DeleteAction(), "DELETE" );
		KeyUtils.setAncestorFocusAction( binAreaPanel, new CopyAction(), "control C" );
		KeyUtils.setAncestorFocusAction( binAreaPanel, new PasteAction(), "control V" );
		KeyUtils.setAncestorFocusAction( binAreaPanel, new SelectAllAction(), "control A" );
		KeyUtils.setAncestorFocusAction( binAreaPanel, new DeSelectAllAction(), "control shift A" );

		//--- Connect GUI components to be accessed elsewhere.
		GUIComponents.animatorFrame = this;
		GUIComponents.animatorMenu = menuBar;
		GUIComponents.renderFlowPanel = renderFlowPanel;//this has to be present when loading boxes.
		GUIComponents.timeLineIOPColumnPanel = iopColumn;
		GUIComponents.timeLineEditorPanel = timelineEditor;
		GUIComponents.timeLineScaleDisplays.add( timeLineDisplay );
		GUIComponents.timeLineScaleDisplays.add( KFtimeLineDisplay );
		GUIComponents.renderFlowButtons = renderFlowButtons;
		GUIComponents.tcDisplay = timecodeDisplay;
		ParamEditController.paramEditFrame = editFrame;
		GUIComponents.viewEditor = viewEdit;
		GUIComponents.kfColumnPanel = kfColumnPanel;
		GUIComponents.keyFrameEditPanel = kfEditPanel;
		GUIComponents.kfNamePanel = namePanel;
		GUIComponents.keyEditorContainerPanel = keyEditorPanel;
		GUIComponents.projectPanel = binAreaPanel;//we have 2 names for this and this is not fixed
		GUIComponents.previewUpdater = previewUpdater;
		GUIComponents.previewControls = previewControl;
		GUIComponents.viewControlButtons = viewControlButtons;
		GUIComponents.viewScrollPane = viewScrollPane;
		GUIComponents.kfControl = kfButtons;
		GUIComponents.binsPanel = binAreaPanel;//we have 2 names for this and this is not fixed
		//GUIComponents.clipVertSlider = clipVSlider;
		GUIComponents.normTlineDisp = normTline;
		GUIComponents.nodesPanel = nodesPanel;
		GUIComponents.renderArea = renderArea;
		GUIComponents.tlineControls = tlineControls;
		//--- These needs init
		TimeLineController.initClipEditorGUI();

		//--- Remove all components (why?)
		getContentPane().removeAll();

		add( realContentPanel );

		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );

		AppUtils.printTitle( "INIT GUI DONE!" );
		initializing = false;

		MinimizeSetTimer minimSet = new MinimizeSetTimer( this );
		minimSet.start();
	}

	//----------------------------------------------- Editor change buttons.
	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == flowButton ) displayFlow();
		if( e.getSource() == timelineButton ) displayTimeline();
		if( e.getSource() == splineButton ) displaySpline();
	}

	public void displayFlow()
	{
		editorsPane.removeAll();
		editorsPane.add( flowHolder );
		
		bottomRow.removeAll();
		bottomRow.add( editSwitchButtons );
		bottomRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
		bottomRow.add( flowButtonsPane );
		bottomRow.add( Box.createHorizontalGlue() );

		if( !initializing ) bottomRow.validate();
		if( !initializing ) editorsPane.validate();
		if( !initializing ) repaint();
	}

	public void displayTimeline()
	{
		editorsPane.removeAll();
		editorsPane.add( timelinePanel );
	
		bottomRow.removeAll();
		bottomRow.add( editSwitchButtons );
		bottomRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
		bottomRow.add( tlineButtonsPane );
		tlineControls.addComponentsToPanel( bottomRow );
		bottomRow.add( Box.createHorizontalGlue() );

		if( !initializing ) bottomRow.validate();
		if( !initializing ) editorsPane.validate();
		if( !initializing ) repaint();
	}

	public void displaySpline()
	{
		editorsPane.removeAll();
		editorsPane.add( keyEditorPanel );
	
		bottomRow.removeAll();
		bottomRow.add( editSwitchButtons );
		bottomRow.add( Box.createRigidArea(new Dimension( 10, 0 ) ) );
		bottomRow.add( kfButtonsPane );
		tlineControls.addComponentsToPanel( bottomRow );
		bottomRow.add( Box.createHorizontalGlue() );

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

	/*
	//--- Helper method
	private JSlider getVertPosSlider()
	{
		JSlider positionSlider = new JSlider( JSlider.HORIZONTAL, 0, 100, 50 );
		positionSlider.addChangeListener( this );
		positionSlider.setPaintTicks( false );
		positionSlider.setPaintLabels(false);
		positionSlider.setPreferredSize( 
			new Dimension( AnimFrameGUIParams.VERTICAL_POS_SLIDER_WIDTH, AnimFrameGUIParams.TE_BOTTOM_STRIP_HEIGHT ) );
		positionSlider.setMaximumSize( 
			new Dimension( AnimFrameGUIParams.VERTICAL_POS_SLIDER_WIDTH, AnimFrameGUIParams.TE_BOTTOM_STRIP_HEIGHT ) );
		return positionSlider;
	}
	//--- Listen to the sliders and forward values.
	public void stateChanged(ChangeEvent e) 
	{
		JSlider source = (JSlider)e.getSource();
		if( e.getSource() == GUIComponents.kfVertSlider )
			GUIComponents.keyFrameEditPanel.setVerticalPos( GUIComponents.kfVertSlider.getValue() );
		if( e.getSource() == GUIComponents.clipVertSlider )
			GUIComponents.timeLineEditorPanel.setVerticalPos( GUIComponents.clipVertSlider.getValue() );
	}
	*/
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
