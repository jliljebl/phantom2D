package animator.phantom.controller;

import animator.phantom.project.LayerCompositorProject;
import animator.phantom.project.Project;


public class AppData
{
		public static LayerCompositorProject layerProject;
		public static Project project;
		
		
		public static void setProject( Project newProject )
		{
			project = newProject;
			
		}
		
}// end class
