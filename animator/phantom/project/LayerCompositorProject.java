package animator.phantom.project;

import animator.phantom.renderer.RenderNode;
import animator.phantom.undo.PhantomUndoManager;

import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;

public class LayerCompositorProject 
{
	private Vector<LayerCompositorLayer> layers;
	
	
	public LayerCompositorProject()
	{
		this.layers = new Vector<LayerCompositorLayer>();
	}
	
	public void addLayer( ImageOperation iop )
	{
		RenderNode addNode = new RenderNode( iop );
		LayerCompositorLayer addLayer = new LayerCompositorLayer( addNode );
		this.layers.add( addLayer );
		
		getFlow().addNode( addLayer.getNode() );

		connectLayers();

		//--- Update GUI
		TimeLineController.targetIopChanged( iop );
		EditorsController.addLayerForIop( iop );
		//--- If new iop does not have edit layer we need render view editor
		//--- because layer add did not trigger render.
		if( iop.getEditorlayer() == null )
			 EditorsController.displayCurrentInViewEditor( false );

		//--- Request editpanel so params will be named.
		//--- Params are named by editors and names are used in save/load and kfeditor
		//--- Do this in thread because might take 500ms+
		final ImageOperation fholder = iop;
		new Thread()
		{
			public void run()
			{
				fholder.getEditFrame( false );
				EditorsController.initKeyFrameEditor( fholder );
			}
		}.start();
		

	}
	
	private Project getNodeProject()
	{
		return ProjectController.getProject();
	}
	
	private RenderFlow getFlow()
	{
		return getNodeProject().getRenderFlow();
	}
	
	private void connectLayers()
	{
		if ( this.layers.size() < 2 ) return;
		
		for( int i = 0; i < this.layers.size() - 1; i++ )
		{
			RenderNode layerNode1 = this.layers.elementAt( i ).getNode();
			RenderNode layerNode2 = this.layers.elementAt( i + 1 ).getNode();
			getFlow().disconnectNodes( layerNode1, layerNode2, 0, 0);
			getFlow().connectNodes( layerNode1, layerNode2, 0, 0);
		}	
	}

}//end class
