package animator.phantom.gui;


import javax.swing.JMenuItem;

import animator.phantom.renderer.FileSource;

public class MediaMenuItem extends JMenuItem 
{
	private static final long serialVersionUID = 1L;
	
	private FileSource fileSource;

	public MediaMenuItem( String title, FileSource f )
	{
		super( title );
		this.fileSource = f;
		setFont(GUIResources.BOLD_FONT_12);
	}

	public FileSource getFileSource(){ return fileSource; }
	
}//end class
