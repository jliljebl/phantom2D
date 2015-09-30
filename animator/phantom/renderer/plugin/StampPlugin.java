package animator.phantom.renderer.plugin;

import java.awt.Color;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ColorParam;

import com.jhlabs.image.StampFilter;

public class StampPlugin extends PhantomPlugin
{
	public AnimatedValue threshold;
	public AnimatedValue softness;
   	public AnimatedValue radius;
	public ColorParam lightColor;
	public ColorParam darkColor;

	public StampPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Stamp" );

		threshold = new AnimatedValue( 0.5f );
		softness = new AnimatedValue( 0 );
		radius = new AnimatedValue( 5 );
		lightColor = new ColorParam( new Color( 255, 255, 255 ) );
		darkColor = new ColorParam( new Color( 0,0,0 ) );
		
		registerParameter( threshold );
		registerParameter( softness );
		registerParameter( radius );
		registerParameter( lightColor );
		registerParameter( darkColor );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor thresEdit = new AnimValueNumberEditor("Threshold" ,threshold );
		AnimValueNumberEditor softnessEdit = new AnimValueNumberEditor("Softness" , softness );
		AnimValueNumberEditor radiusEdit = new AnimValueNumberEditor("Radius" , radius);
		ParamColorSelect lightColorEdit = new ParamColorSelect( lightColor, "Light Color" );
		ParamColorSelect darkColorEdit = new ParamColorSelect( darkColor, "Dark Color" );

		addEditor( thresEdit );
		addRowSeparator();
		addEditor( softnessEdit);
		addRowSeparator();
		addEditor( radiusEdit );
		addRowSeparator();
		addEditor( lightColorEdit);
		addRowSeparator();
		addEditor( darkColorEdit );
	}

	public void doImageRendering( int frame )
	{
		StampFilter stampFilter = new StampFilter();
		stampFilter.setRadius( radius.getValue( frame) );
		stampFilter.setThreshold( threshold.getValue( frame) );
		stampFilter.setSoftness( softness.getValue( frame) );
		stampFilter.setWhite( lightColor.get().getRGB() );
		stampFilter.setBlack( darkColor.get().getRGB() );

		applyFilter( stampFilter );
	}

}//end class
