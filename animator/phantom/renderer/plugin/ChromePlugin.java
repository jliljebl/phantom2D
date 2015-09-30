package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.FloatParam;

import com.jhlabs.image.ChromeFilter;

public class ChromePlugin extends PhantomPlugin
{
	public FloatParam amount;
	public FloatParam exposure;

	public ChromePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Chrome" );

		amount = new FloatParam( 0.5f );
		exposure = new FloatParam( 1.0f );

		registerParameter( amount );
		registerParameter( exposure );
	}

	public void buildEditPanel()
	{
		FloatNumberEditor amountEdit = new FloatNumberEditor( "Amount", amount );
		FloatNumberEditor exposureEdit  = new FloatNumberEditor( "Exposure", exposure );

		addEditor( amountEdit );
		addRowSeparator();
		addEditor( exposureEdit );
	}

	public void doImageRendering( int frame )
	{
		ChromeFilter cFilt = new ChromeFilter();
		cFilt.setAmount( amount.get() );
		cFilt.setExposure( exposure.get() );

		applyFilter( cFilt );
	}

}//end class
