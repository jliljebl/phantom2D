package animator.phantom.gui;


import javax.swing.JMenuItem;

import animator.phantom.renderer.FileSource;

public class MediaMenuItem extends JMenuItem 
{
	private FileSource fileSource;

	public MediaMenuItem( String title, FileSource f )
	{
		super( title );
		this.fileSource = f;
	}

	public FileSource getFileSource(){ return fileSource; }
	
}//end class
