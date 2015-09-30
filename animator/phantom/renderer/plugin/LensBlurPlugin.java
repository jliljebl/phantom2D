package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.LensBlurFilter;

public class LensBlurPlugin extends PhantomPlugin
{
	public IntegerParam radius;
	public IntegerParam sides;
	public IntegerParam bloom;
	public IntegerParam bloomThreshold;

	public LensBlurPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "LensBlur" );

		radius = new IntegerParam(10,0,50);
		sides = new IntegerParam(5,3,12);
		bloom = new IntegerParam(2,0,8);
		bloomThreshold = new IntegerParam(255,0,255);

		registerParameter( radius );
		registerParameter( sides );
		registerParameter( bloom );
		registerParameter( bloomThreshold );	
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor radiusE = new IntegerValueSliderEditor( "Radius" , radius );
		IntegerValueSliderEditor sidesE = new IntegerValueSliderEditor( "Sides" , sides );
		IntegerValueSliderEditor bloomE = new IntegerValueSliderEditor( "Bloom" , bloom );
		IntegerValueSliderEditor bloomThresholdE = new IntegerValueSliderEditor( "BloomThrs" , bloomThreshold );

		addEditor( radiusE );
		addRowSeparator();
		addEditor( sidesE );
		addRowSeparator();
		addEditor( bloomE );
		addRowSeparator();
		addEditor( bloomThresholdE );
	}

	public void doImageRendering( int frame )
	{
		LensBlurFilter filter = new LensBlurFilter();
		filter.setRadius( (float)radius.get() );
		filter.setSides( sides.get() );
		filter.setBloom( (float)bloom.get() );
		filter.setBloomThreshold( (float)bloomThreshold.get() );

		applyFilter( filter );
	}

}//end class
