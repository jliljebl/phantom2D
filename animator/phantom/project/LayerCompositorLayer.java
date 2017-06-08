package animator.phantom.project;

import animator.phantom.renderer.RenderNode;

public class LayerCompositorLayer 
{
	private RenderNode node;
	
	public LayerCompositorLayer( RenderNode node )
	{
		this.node = node;
	}

	public RenderNode getNode(){ return this.node; }
	
}//end class
