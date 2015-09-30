package animator.phantom.renderer.plugin;

import giotto2D.filters.color.Threshold;

import java.awt.Color;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

public class ThresholdPlugin extends PhantomPlugin
{
	public ColorParam lightColor = new ColorParam( new Color( 255, 255, 255 ) );
	public ColorParam darkColor = new ColorParam( new Color( 0,0,0 ) );
	public IntegerParam toneLimit = new IntegerParam( 128 );

	public ThresholdPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Threshold" );

		registerParameter( lightColor );
		registerParameter( darkColor );
		registerParameter( toneLimit );

	}

	public void buildEditPanel()
	{
		IntegerNumberEditor limitEditor = new IntegerNumberEditor( "Threshold", toneLimit);
		ParamColorSelect lightEditor = new ParamColorSelect( lightColor, "Light Color" );
		ParamColorSelect darkEditor = new ParamColorSelect( darkColor, "Dark Color" );

		addEditor( limitEditor );
		addRowSeparator();
		addEditor( lightEditor );
		addRowSeparator();
		addEditor( darkEditor );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img  = getFlowImage();
		Threshold.filter( img, toneLimit.get(), darkColor.get(), lightColor.get() );

		sendFilteredImage( img, frame );
	}

}//end class
