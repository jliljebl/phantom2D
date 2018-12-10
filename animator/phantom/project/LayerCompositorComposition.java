package animator.phantom.project;

import java.util.Collections;
import java.util.Vector;

import animator.phantom.controller.AppData;
import animator.phantom.controller.LayerCompositorUpdater;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;

public class LayerCompositorComposition 
{
	
	private Vector<LayerCompositorLayer> layers;
	
	
	public LayerCompositorComposition()
	{
		this.layers = new Vector<LayerCompositorLayer>();
	}
	
	// When composition is opened (after composition change or project load) we get layer clip iops from ProjectNamdFlow object 
	// with Project.getCurrentComposition()) here so that layers can be initially created.
	//
	// When layers are added or removed during editing layer clip iops go to ProjectNamdFlow object from here, see TimelineContoller.loadClips() called after all
	// layer changes.
	public void loadLayersFromCurrentCompositionTimelineClips()
	{
		Vector<ImageOperation> clips = AppData.getProject().getCurrentComposition().getTimelineClips();
		Collections.reverse(clips);
		
		for( int i = 0; i < clips.size(); i++ )
		{
			// Layer clips, image sources and adjustment layers.
			ImageOperation layerIop = clips.elementAt( i );
			RenderNode layerNode = AppData.getCurrentFlow().getNode( layerIop );
			LayerCompositorLayer addLayer = addLayerForNode( layerNode );
	
			// filter clips are not represented as layers here, they are represented as layers only in timeline GUI,
			// where they are created from filter stack data.
			
			// Masks clips
			Vector<RenderNode> maskNodes = layerNode.getMaskInputFirstNodes();
			for( int j = 0; j < maskNodes.size(); j++ )
			{
				RenderNode maskNode = maskNodes.elementAt( j );
				addLayer.addMaskLayerForNode( maskNode );
			}
		}
	}
	
	//------------------------------------------------------------------------ edits
	// Used on user action, we need to create the node and structure.
	public LayerCompositorLayer addLayer( ImageOperation iop )
	{
		disconnectLayers();

		RenderNode addNode = new RenderNode( iop );
		LayerCompositorLayer addLayer = new LayerCompositorLayer( addNode );
		this.layers.add( addLayer );
		getFlow().addNode( addLayer.getNode() );

		connectLayers();
		LayerCompositorUpdater.layerAddUpdate( iop );
		return addLayer;
	}
	// Used on load and composition change, we have the nodes and structure available
	private LayerCompositorLayer addLayerForNode( RenderNode addNode )
	{
 
		LayerCompositorLayer addLayer = new LayerCompositorLayer( addNode );
		this.layers.add( addLayer );
		return addLayer;
	}
	
	public void insertLayer( LayerCompositorLayer insertLayer, int index )
	{
		disconnectLayers();
		
		this.layers.insertElementAt( insertLayer, index );
		
		getFlow().addNode( insertLayer.getNode() );

		connectLayers();

		LayerCompositorUpdater.layerAddUpdate( insertLayer.getIop() );
	}
	
	public void deleteLayer( LayerCompositorLayer deleteLayer )
	{
		disconnectLayers();
		
		RenderNode deleteNode = deleteLayer.getNode();
		Vector<RenderNode> deleteVec = new Vector<RenderNode>();
		deleteVec.add( deleteNode );

		this.layers.removeElement( deleteLayer );
		
		getFlow().removeNodes( deleteVec );

		connectLayers();
		
		LayerCompositorUpdater.layerDeleteUpdate( deleteVec );
	}

	public void addMaskLayer( ImageOperation maskIop, ImageOperation layerIop )
	{
		maskIop.copyTimeParams( layerIop );
		LayerCompositorLayer layer = getLayer( layerIop );
		layer.addMaskLayer( maskIop );
		
		LayerCompositorUpdater.layerMaskAddUpdate( maskIop, layerIop );
	}

	public void addMaskLayerForNode( ImageOperation maskIop, ImageOperation layerIop )
	{
		maskIop.copyTimeParams( layerIop );
		LayerCompositorLayer layer = getLayer( layerIop );
		layer.addMaskLayer( maskIop );
		

	}
	
	public void deleteMaskLLayer( ImageOperation maskIop, ImageOperation layerIop )
	{
		
	}
	
	//-------------------------------------------------------------------- interface
	public LayerCompositorLayer getLayer( ImageOperation iop )
	{
		for( LayerCompositorLayer layer : layers )
		{
			if( layer.getIop() == iop)
				return layer;
		}
		
		return null;
	}
	
	public int getLayerIndex( LayerCompositorLayer layer ){ return layers.indexOf( layer ); }
	
	public Vector<ImageOperation> getLayerGUIIops()
	{
		Vector<ImageOperation> layersIops = new Vector<ImageOperation>();
		for( int i = this.layers.size() - 1; i > - 1; i-- )
		{
			layersIops.add( layers.elementAt(i).getIop() );
		}
		return layersIops;
	}
	
	//--------------------------------------------------------------------- internal
	private RenderFlow getFlow()
	{
		return AppData.getCurrentFlow();
	}
		
	private void connectLayers()
	{
		if ( this.layers.size() < 2 ) return;
		
		for( int i = 0; i < this.layers.size() - 1; i++ )
		{
			RenderNode layerNode1 = this.layers.elementAt( i ).getNode();
			RenderNode layerNode2 = this.layers.elementAt( i + 1 ).getNode();
			getFlow().connectNodes( layerNode1, layerNode2, 0, 0);
		}	
	}

	private void disconnectLayers()
	{
		if ( this.layers.size() < 2 ) return;
		
		for( int i = 0; i < this.layers.size() - 1; i++ )
		{
			RenderNode layerNode1 = this.layers.elementAt( i ).getNode();
			RenderNode layerNode2 = this.layers.elementAt( i + 1 ).getNode();
			getFlow().disconnectNodes( layerNode1, layerNode2, 0, 0);
		}	
	}
	
}//end class
