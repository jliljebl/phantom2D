package animator.phantom.renderer;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import animator.phantom.gui.GUIResources;

public class VideoClipSource extends FileSource
{
	private static ImageIcon movieIcon = GUIResources.getIcon( GUIResources.movieicon );

	//--- for loading.
	public VideoClipSource(){}

	public VideoClipSource( File  f )
	{
		type = VIDEO_FILE;
		init( f );
	}

	public void loadInit()
	{
		init( file );
	}

	private void init( File clip )
	{
		
		
	}
	
	public BufferedImage getClipImage( int clipFrame )
	{
		return null;
	}
	
	public int getProgramLength()
	{
		return 10;
	}

	public ImageIcon getThumbnailIcon()
	{
		return null;
	}

	public void firstLoadData()
	{
		
	}
	public void loadData(){}//--- not cached
	public boolean dataInMemory(){ return false; }//--- not cached
	public void clearData(){}//--- not cached
	public void cacheOrClearData(){}//--- not cached

	public BufferedImage getBufferedImage(){ return null; }
	public ImageIcon getFileTypeIcon(){ return movieIcon; }
	
}//end class
