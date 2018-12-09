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
		layerComposition().insertLayer( this.deleteLayer, this.layerIndex );
	}
	
	public void redo()
	{
		this.deleteLayer = layerComposition().getLayer( this.iop );
		this.layerIndex = layerComposition().getLayerIndex( this.deleteLayer );
		layerComposition().deleteLayer( this.deleteLayer );
	}

}//end class
