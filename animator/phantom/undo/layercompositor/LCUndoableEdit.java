package animator.phantom.undo.layercompositor;


import animator.phantom.controller.AppData;
import animator.phantom.project.LayerCompositorComposition;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.undo.PhantomUndoableEdit;

public abstract class LCUndoableEdit extends PhantomUndoableEdit
{
	public void doEdit()
	{
		redo();
		PhantomUndoManager.addUndoEdit( this );
	}

	public LayerCompositorComposition layerComposition()
	{
		return AppData.getLayerComposition();
	}
	
}//end class
