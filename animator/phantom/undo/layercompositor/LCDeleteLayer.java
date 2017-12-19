package animator.phantom.undo.layercompositor;

import animator.phantom.project.LayerCompositorLayer;
import animator.phantom.renderer.ImageOperation;

public class LCDeleteLayer extends LCUndoableEdit
{
	private LayerCompositorLayer deleteLayer;
	private int layerIndex;
	
	public LCDeleteLayer( ImageOperation deleteIOP )
	{
		this.iop = deleteIOP;
	}
	
	public void undo()
	{
		layerProject().insertLayer( this.deleteLayer, this.layerIndex );
	}
	
	public void redo()
	{
		this.deleteLayer = layerProject().getLayer( this.iop );
		this.layerIndex = layerProject().getLayerIndex( this.deleteLayer );
		layerProject().deleteLayer( this.deleteLayer );
	}

}//end class
