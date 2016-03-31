package animator.phantom.controller;

import animator.phantom.renderer.VideoClipSource;

public class MLTFrameServerController 
{

	private static MLTFrameServer frameServer;
	private static final String LOAD = "LOAD";
	private static final String ERROR = "ERROR";
	private static final String RENDER_FRAME = "RENDER_FRAME";
	
	public static void init( String rootPath, String workDir)
	{
		frameServer = new MLTFrameServer( rootPath, workDir );
		frameServer.launchServer();
		boolean success = frameServer.waitForServer();
		if (success == true )
		{
			frameServer.connect();
			//String answer = frameServer.sendCommand("LOAD /home/janne/tr_final.mpg");
		}
	}
	
	public static String loadClipIntoServer( VideoClipSource movieSource )
	{
		String command = LOAD + " " + movieSource.getFile().getAbsolutePath();
		String answer = frameServer.sendCommand(command);
		return sendAnswerOrNullForError( answer );
	}

	public static String renderFrame( VideoClipSource movieSource, int frame )
	{
		String command = RENDER_FRAME + " " + movieSource.getFile().getAbsolutePath() + " " + Integer.toString( frame );
		String answer = frameServer.sendCommand(command);
		return sendAnswerOrNullForError( answer );
	}
	
	public static String getSourceFramesFolder( VideoClipSource movieSource )
	{
		return frameServer.getDiskCacheDirPath() + "/" + movieSource.getMD5id();
	}


	
	private static String sendAnswerOrNullForError( String answer )
	{
		if ( answer.startsWith(ERROR) )
		{
			return null;
		}
		
		return answer;
	}
	
}//end class
