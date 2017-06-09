package animator.phantom.project;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

public class LayerCompositorLayer 
{
	private RenderNode node;
	
	public LayerCompositorLayer( RenderNode node )
	{
		this.node = node;
	}

	public RenderNode getNode(){ return this.node; }
	public ImageOperation getIop(){ return this.node.getImageOperation(); }
	
}//end class
