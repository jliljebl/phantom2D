package animator.phantom.project;

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
	
	//------------------------------------------------------------------------ edits
	public LayerCompositorLayer addLayer( ImageOperation iop )
	{
		disconnectLayers();
		System.out.println("1");
		RenderNode addNode = new RenderNode( iop );
		System.out.println("1");
		LayerCompositorLayer addLayer = new LayerCompositorLayer( addNode );
		System.out.println("1");
		this.layers.add( addLayer );
		System.out.println("1");
		getFlow().addNode( addLayer.getNode() );
		System.out.println("1");

		connectLayers();
		System.out.println("1");
		LayerCompositorUpdater.layerAddUpdate( iop );
		System.out.println("1");
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

	public void addPreCompLayer( ImageOperation precompIop, ImageOperation layerIop )
	{
	
		precompIop.copyTimeParams( layerIop );
		LayerCompositorLayer layer = getLayer( layerIop );
		layer.addPreCompLayer( precompIop );
		
		LayerCompositorUpdater.layerMaskAddUpdate( precompIop, layerIop );
	}
	
	public void deletePreCompLLayer( ImageOperation maskIop, ImageOperation layerIop )
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
