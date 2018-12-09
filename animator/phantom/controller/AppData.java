package animator.phantom.controller;

import animator.phantom.gui.ParamEditFrame;
import animator.phantom.project.LayerCompositorComposition;
import animator.phantom.project.Project;
import animator.phantom.renderer.RenderFlow;


public class AppData
{
		private static LayerCompositorComposition composition;
		private static Project project;
		private static ParamEditFrame paramEditFrame;
		
		public static void setProject( Project newProject ) { project = newProject; }
		public static Project getProject() { return project; }
		
		public static RenderFlow getCurrentFlow(){ return project.getCurrentRenderFlow(); }
		
		public static void setLayerProject( LayerCompositorComposition newComposition )	{ composition = newComposition; }
		public static LayerCompositorComposition getLayerComposition()	{ return composition; }

		public static void setParamEditFrame( ParamEditFrame newpParamEditFrame )	{ paramEditFrame = newpParamEditFrame; }
		public static ParamEditFrame getParamEditFrame() { return paramEditFrame; }

}// end class
