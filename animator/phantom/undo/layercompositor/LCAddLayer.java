package animator.phantom.undo.layercompositor;

import animator.phantom.project.LayerCompositorLayer;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.undo.PhantomUndoManager;


public class LCAddLayer extends LCUndoableEdit
{
	private LayerCompositorLayer addLayer;
	
	
	public LCAddLayer( ImageOperation addIOP )
	{
		this.iop = addIOP;
		this.iop.initIOPTimelineValues();
		PhantomUndoManager.newIOPCreated( this.iop ); //--- Create initial state for Paramvalue change  undos
	}
	
	public void undo()
	{
		layerComposition().deleteLayer( this.addLayer );
	}
	
	public void redo()
	{
		this.addLayer = layerComposition().addLayer( this.iop );
	}

}//end class
