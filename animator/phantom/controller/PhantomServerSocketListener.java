package animator.phantom.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PhantomServerSocketListener extends Thread
{
    private PhantomServer server;
    private final static String OK = "OK\n";
	private static final String UNREGONIZED = "UNREGONIZED COMMAND\n";
	
    private final static String SHUTDOWN = "SHUTDOWN";
    private final static String LOAD = "LOAD";
    private final static String RENDER_FRAME = "RENDER_FRAME";
    private final static String SET_FOLDER = "SET_FOLDER";
    
	public PhantomServerSocketListener(PhantomServer server)
	{
		this.server = server;
	}

	public void run()
	{

		try
		{
			ServerSocket serverSocket = new ServerSocket(0);
			System.out.println("Phantom server listening on port: " + serverSocket.getLocalPort());
		    
			boolean waitForNext = true;
	
	         while(waitForNext)
	         {
	            Socket connectionSocket = serverSocket.accept();
	            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	            
	            String clientCommand = inFromClient.readLine();
	            System.out.println("Received: " + clientCommand);

	            String[] tokens = clientCommand.split("\\s");
	            String command = tokens[0];


	            if(command.equals(LOAD))
	            {
	            	String path = tokens[1];
		            server.loadProject( path );
		            outToClient.writeBytes(OK);
	            }
	            else if(command.equals(RENDER_FRAME))
	            {
	    			System.out.println("render frame");
	            	int frame = Integer.parseInt( tokens[1] );
	    			System.out.println(frame);
		            server.renderFrame(frame);
		            outToClient.writeBytes(OK);
	            }
	            else if(command.equals(SHUTDOWN))
	            {
		            outToClient.writeBytes(OK);
	            	waitForNext = false;
	            }
	            else
	            {
		            outToClient.writeBytes(UNREGONIZED);
	            }
	         }
	         serverSocket.close();
		}
		catch (IOException e) 
		{
			System.out.println("IOException");
			e.printStackTrace();
		}
    	server.shutdown();
	}

}//end class
