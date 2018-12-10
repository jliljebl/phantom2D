package animator.phantom.project;

import java.util.Vector;

import animator.phantom.controller.AppData;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

public class LayerCompositorLayer 
{
	private RenderNode node;
	private Vector<RenderNode> preCompLayers;
	
	public LayerCompositorLayer( RenderNode node )
	{
		this.node = node;
		this.setPreCompLayers(new Vector<RenderNode>());
	}

	public RenderNode getNode(){ return this.node; }

	public ImageOperation getIop(){ return this.node.getImageOperation(); }

	public Vector<RenderNode> getPreCompLayers() { return preCompLayers; }

	public void setPreCompLayers(Vector<RenderNode> layerMasks) { this.preCompLayers = layerMasks; }
	
	public void addMaskLayer( ImageOperation precompIop )
	{
		disconnectMaskLayers();
		
		RenderNode maskNode = new RenderNode( precompIop );
		this.preCompLayers.add( maskNode );
		AppData.getCurrentFlow().addNode( maskNode );

		connectMaskLayers();
	}

	public void addMaskLayerForNode( RenderNode maskNode )
	{
		this.preCompLayers.add( maskNode );
	}
	
	
	public void deleteMaskLLayer(  ImageOperation maskIop )
	{
		
	}
			
	private void connectMaskLayers()
	{
		if ( this.preCompLayers.size() > 1 )
		{
			for( int i = 0; i < this.preCompLayers.size() - 1; i++ )
			{
				RenderNode maskNode1 = this.preCompLayers.elementAt( i );
				RenderNode maskNode2 = this.preCompLayers.elementAt( i + 1 );
				AppData.getCurrentFlow().connectNodes( maskNode1, maskNode2, 0, 0);
			}
		}

		if ( this.preCompLayers.size() > 0 )
		{
			int maskInput = node.getImageOperation().getMaskInputIndex();
			RenderNode masksOutNode = preCompLayers.elementAt( preCompLayers.size() - 1 );
			AppData.getCurrentFlow().connectNodes( masksOutNode, node, 0, maskInput);
		}
	}

	private void disconnectMaskLayers()
	{
		if ( this.preCompLayers.size() > 1 )
		{
			for( int i = 0; i < this.preCompLayers.size() - 1; i++ )
			{
				RenderNode maskNode1 = this.preCompLayers.elementAt( i );
				RenderNode maskNode2 = this.preCompLayers.elementAt( i + 1 );
				AppData.getCurrentFlow().disconnectNodes( maskNode1, maskNode2, 0, 0);
			}
		}
	}

}//end class

