package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.jhlabs.image.GlintFilter;
import com.jhlabs.image.OilFilter;
import com.jhlabs.image.SwimFilter;

import animator.phantom.plugin.PhantomPlugin;

public class TestPlugin  extends PhantomPlugin
{

	public TestPlugin()
	{
		initPlugin( FILTER );
	}

	@Override
	public void buildDataModel() 
	{
		setName("Test");
	}

	@Override
	public void buildEditPanel() 
	{

	}

	public void doImageRendering( int frame )
	{
		OilFilter f = new OilFilter();

		applyFilter( f );
	}
	
}//end class
