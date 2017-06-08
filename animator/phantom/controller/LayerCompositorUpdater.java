package animator.phantom.controller;

import java.util.Vector;

import animator.phantom.renderer.ImageOperation;

public class LayerCompositorUpdater 
{

	public static void layerAddUpdate( ImageOperation addIOP )
	{
		//--- Update GUI
		TimeLineController.targetIopChanged( addIOP );
		EditorsController.addLayerForIop( addIOP );
		//--- If new iop does not have edit layer we need render view editor
		//--- because layer add did not trigger render.
		if( addIOP.getEditorlayer() == null )
			 EditorsController.displayCurrentInViewEditor( false );

		//--- Request editpanel so params will be named.
		//--- Params are named by editors and names are used in save/load and kfeditor
		//--- Do this in thread because might take 500ms+
		final ImageOperation fholder = addIOP;
		new Thread()
		{
			public void run()
			{
				fholder.getEditFrame( false );
				EditorsController.initKeyFrameEditor( fholder );
			}
		}.start();
		
		Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
		addClips.add( addIOP );
		
		TimeLineController.addClips( addClips );
		TimeLineController.initClipEditorGUI();
		ParamEditController.displayEditFrame( addIOP );// ALSO TO INIT PARAM NAMES IN RAW IOPS, plugins do this by them selves
	}
		
}//end class
