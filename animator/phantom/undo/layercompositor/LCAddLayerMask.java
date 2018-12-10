package animator.phantom.undo.layercompositor;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.undo.PhantomUndoManager;


public class LCAddLayerMask extends LCUndoableEdit
{
	//private ImageOperation addLayer;
	private ImageOperation layerIop;
	
	public LCAddLayerMask( ImageOperation filterIOP, ImageOperation layerIop )
	{
		this.iop = filterIOP;
		this.iop.initIOPTimelineValues();
		PhantomUndoManager.newIOPCreated( iop ); //--- Create initial state for Paramvalue change undos
		
		this.layerIop = layerIop;
	}
	
	public void undo()
	{
		
		layerComposition().deleteMaskLLayer( this.iop, this.layerIop );
	}
	
	public void redo()
	{
		layerComposition().addMaskLayer( this.iop, this.layerIop );
	}

}//end class
