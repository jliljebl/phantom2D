package animator.phantom.renderer.plugin;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.CausticsFilter;

public class CausticsPlugin extends PhantomPlugin
{
	public AnimatedValue scale = new AnimatedValue( 32 );
	public AnimatedValue brightness = new AnimatedValue( 10 );
	public AnimatedValue amount = new AnimatedValue( 1.0f );
	public AnimatedValue turbulence = new AnimatedValue( 1.0f );
	public AnimatedValue dispersion = new AnimatedValue( 0.0f );
	public AnimatedValue time = new AnimatedValue( 0.0f );
	public IntegerParam samples = new IntegerParam( 2, 1, 3 );
	public ColorParam bgColor = new ColorParam( 0xff799fff, true );

	public CausticsPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "Caustics" );

		registerParameter( scale );
		registerParameter( brightness );
		registerParameter( amount  );
		registerParameter( turbulence );
		registerParameter( dispersion );
		registerParameter( time );
		registerParameter( samples );
		registerParameter( bgColor );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor scaleE = new AnimValueNumberEditor( "Scale", scale );
		AnimValueNumberEditor brightnessE = new AnimValueNumberEditor( "Brightness", brightness );
		AnimValueNumberEditor amountE = new AnimValueNumberEditor( "Amount", amount );
		AnimValueNumberEditor turbulenceE = new AnimValueNumberEditor( "Turbulence", turbulence );
		AnimValueNumberEditor dispersionE = new AnimValueNumberEditor( "Dispersion", dispersion );
		AnimValueNumberEditor timeE = new AnimValueNumberEditor( "Time", time );
		IntegerNumberEditor samplesE = new IntegerNumberEditor( "Samples", samples );
		ParamColorSelect bgColorE = new ParamColorSelect( bgColor, "Bacground color" );

		addEditor( scaleE );
		addRowSeparator();
		addEditor( brightnessE );
		addRowSeparator();
		addEditor( amountE );
		addRowSeparator();
		addEditor( turbulenceE );
		addRowSeparator();
		addEditor( dispersionE );
		addRowSeparator();
		addEditor( timeE );
		addRowSeparator();
		addEditor( samplesE );
		addRowSeparator();
		addEditor( bgColorE );
	}

	public void doImageRendering( int frame )
	{
		CausticsFilter cFilt = new CausticsFilter();

		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		cFilt.setScale( scale.getValue( frame ) );
		cFilt.setBrightness( (int) brightness.getValue( frame ) );
		cFilt.setTurbulence( turbulence.getValue( frame ) );
		cFilt.setAmount( amount.getValue( frame ) );
		cFilt.setDispersion( dispersion.getValue( frame ) );
		cFilt.setTime( time.getValue( frame )  );
		cFilt.setSamples( samples.get() );
		cFilt.setBgColor( bgColor.get().getRGB() );
	
		PluginUtils.filterImage( img, cFilt );

		sendStaticSource( img, frame );
	}

}//end class
