package animator.phantom.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MLTFrameServer 
{
	private String rootPath;
	private String diskCacheDirPath;
	private String sessionID;
	private ServerLauncher serverLauncher;
	private boolean serverRunning = false;
	private int port;
	private int timeOutSec = 5;
	
	private String SERVER_LAUNCH_SCRIPT = "/serverlaunch";
	
	public MLTFrameServer( String rootPath, String diskCacheDirPath )
	{
		this.rootPath = rootPath;
		this.diskCacheDirPath = diskCacheDirPath;
		this.sessionID = Long.toString(System.currentTimeMillis());//good enough
	}

	public void launchServer()
	{
		File f = new File( rootPath + SERVER_LAUNCH_SCRIPT);
		if(f.exists() && !f.isDirectory()) 
		{
			serverLauncher = new ServerLauncher();
			serverLauncher.start();
		}
		else
		{
			System.out.println( "NO VIDEO CLIP SUPPORT! MLTServer launch script not found.");
		}
	}

	public void waitForServer()
	{
		boolean waiting = true;
		long start = System.currentTimeMillis();
		while ( waiting )
		{
			System.out.println( "Waiting for MLTServer socket file at " + sessionFilePath() );
			
			File f = new File( sessionFilePath() );
			if(f.exists() && !f.isDirectory()) 
			{
				waiting = false;
			}
			else
			{
				long now = System.currentTimeMillis();
				if( (now - start) > (1000 * timeOutSec) )
				{
					System.out.println( "NO VIDEO CLIP SUPPORT! MLTServer launch timed out.");
					return;
				}
				else
				{
					try {
					    Thread.sleep(200);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
			}
		}
		
		// read port from socket file
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new FileReader(sessionFilePath()));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
	
		    while (line != null) {
		        sb.append(line);
		        line = br.readLine();
		    }
		    String contents = sb.toString();
		    br.close();
		    
		    port = Integer.parseInt(contents);
			System.out.println("VIDEO CLIPS ARE SUPPORTED! MLT frame server found, port is " + Integer.toString(port));
		} 
		catch( Exception e ) 
		{
			System.out.println("Exception opening socket file.");
			e.printStackTrace();
		}
	}
	
	// mltframeserver.py creates this file when up and running.
	public String sessionFilePath(){ return diskCacheDirPath + "/session_" + sessionID; }
	
	public boolean serverRunning(){ return serverRunning; }
	public int getPort(){ return port; }

	class ServerLauncher extends Thread
	{		
		public void run()
		{
		   Process p = null;
		   
		   // launch mltframeserver.py
		   try 
		   {
			   String launchFile  = rootPath + SERVER_LAUNCH_SCRIPT;
			   
			   ProcessBuilder pb = new ProcessBuilder(launchFile, sessionID);
			   p = pb.start();
			   serverRunning = true;
		   } 
		   catch (IOException e1) 
		   {
				e1.printStackTrace();
		   }
		   
		   // wait for it to end
		   try 
		   {
			   p.waitFor();
		   }
		   catch (InterruptedException e)
		   {
				e.printStackTrace();
		   }
		}
	}//end class ServerLauncher

}//end class