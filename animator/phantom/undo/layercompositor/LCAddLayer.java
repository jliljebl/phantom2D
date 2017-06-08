package animator.phantom.undo.layercompositor;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.undo.PhantomUndoManager;


public class LCAddLayer extends LCUndoableEdit
{
	
	public LCAddLayer( ImageOperation addIOP )
	{
		this.iop = addIOP;
		this.iop.initIOPTimelineValues();
		PhantomUndoManager.newIOPCreated( iop ); //--- Create initial state for Param undos
	}
	
	public void undo()
	{
		
		
	}
	
	public void redo()
	{

		layerProject().addLayer( this.iop );
	}
	/*
	
	
		public static void addIOP( String className  )
	{
		ImageOperation addIOP = IOPLibrary.getNewInstance( className );
		FlowController.addIOPRightAway( addIOP );



	public static void addIOPNow( ImageOperation addIOP, Point p )
	{
		addIOP.initIOPTimelineValues();
		Point addP = GUIComponents.renderFlowPanel.getAddPos(p);
		GUIComponents.renderFlowPanel.addIOPRightAway( addIOP, addP.x, addP.y );
		ParamEditController.displayEditFrame( addIOP );// ALSO TO INIT PARAM NAMES IN RAW IOPS, plugins do this by them selves
	}
	
	
		public void addIOPRightAway( ImageOperation iop, int x, int y )
	{ 
		addIOP = iop;
		addBox( x, y );
	}

	private void addBox( int x, int y )
	{
		//--- Create box where shadowBox was
		//--- Create node to added.
		RenderNode addNode = new RenderNode( addIOP );
		//--- Create box to be added.
		FlowBox addBox = new FlowBox(   x - ( FlowBox.width / 2 ),
						y - ( FlowBox.height / 2 ),
						addNode );

		NodeAddUndoEdit undoEdit = new NodeAddUndoEdit( addNode, addBox );

		//--- BANG! New iops to flow are added HERE!
		undoEdit.doEdit();

		//--- Register undo
		PhantomUndoManager.addUndoEdit( undoEdit );

		//--- Set all currently selected boxes unselected.
		for(  FlowBox b : selectedBoxes )
			b.setSelected( false );
		selectedBoxes.clear();

		//--- Draw panel
		movingGraphics.clear();
		paintImmediately( 0, 0, getWidth(), getHeight() );//--- to give an impression of a responsive GUI

		//ADD_IN_PROGRESS = false;
	}
	
	
		public NodeAddUndoEdit( RenderNode node, FlowBox box )
	{
		super();
		this.node = node;
		this.box = box;
	}

	public void undo()
	{
		Vector<RenderNode> vec = new Vector<RenderNode>();
		vec.add( node );

		EditorsController.removeLayers( vec );
		EditorsController.clearKFEditIfNecessery( vec );
		GUIComponents.renderFlowPanel.boxes.remove( box );
		GUIComponents.renderFlowPanel.selectedBoxes.remove( box );
		GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( box, box.getArea() );	
		FlowController.deleteRenderNodes( vec );
			
		GUIComponents.renderFlowPanel.repaint();

		ImageOperation iop = node.getImageOperation();
		//if( IOPLibrary.getBoxType( iop ) == IOPLibrary.BOX_SOURCE )
		//{
			Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
			addClips.add( iop );
		
			TimeLineController.removeClips( addClips );
			TimeLineController.initClipEditorGUI();
		//}
	}

	public void redo()
	{
		GUIComponents.renderFlowPanel.boxes.add( box );
		GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( box );
		FlowController.addRenderNode( node );

		GUIComponents.renderFlowPanel.repaint();
		
		ImageOperation iop = node.getImageOperation();
		
		//if( IOPLibrary.getBoxType( iop ) == IOPLibrary.BOX_SOURCE )
		//{
			Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
			addClips.add( iop );
			
			TimeLineController.addClips( addClips );
			TimeLineController.initClipEditorGUI();
		//}
		 
	


	public static void addRenderNode( RenderNode addNode )
	{
		//--- Add node to flow. New node gets its ID.
		ProjectController.getFlow().addNode( addNode );

		//--- Update GUI
		ImageOperation iop = addNode.getImageOperation();
		TimeLineController.targetIopChanged( iop );

		EditorsController.addLayerForIop( iop );
		
		//--- If new iop does not have edit layer we need render view editor bg
		//--- because layer add did not trigger render.
		if( iop.getEditorlayer() == null )
			 EditorsController.displayCurrentInViewEditor( false );
		//--- Create initial state for undos
		PhantomUndoManager.newIOPCreated( iop );
		//--- Request editpanel so params will be named
		//--- Params are named in editors and names are used in save/load
		//--- and kfeditor
		//--- Do in thread because might take 500ms+
		final ImageOperation fholder = iop;
		new Thread()
		{
			public void run()
			{
				fholder.getEditFrame( false );
				EditorsController.initKeyFrameEditor( fholder );
			}
		}.start();
			
	}*/
}//end class
