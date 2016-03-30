package animator.phantom.controller;

public class MLTFrameServerController 
{
	private static MLTFrameServer frameServer;
	
	public static void init( String rootPath, String workDir)
	{
		frameServer = new MLTFrameServer( rootPath, workDir);
		frameServer.launchServer();
		frameServer.waitForServer();
	}
	
}//end class
