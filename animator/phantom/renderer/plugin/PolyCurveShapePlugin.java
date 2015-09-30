package animator.phantom.renderer.plugin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import animator.phantom.gui.view.editlayer.PolyCurveEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

public class PolyCurveShapePlugin extends PolyCurvePlugin
{
	public ColorParam fill = new ColorParam( Color.lightGray );
	public ColorParam stroke = new ColorParam( Color.white );
	public IntegerParam lineWidth = new IntegerParam( 5 );
	public BooleanParam fillOn = new BooleanParam( true );

	public PolyCurveShapePlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "CurveShape" );

		registerPathParams();
		registerParameter( fill );
		registerParameter( stroke );
		registerParameter( lineWidth );
		registerParameter( fillOn );
	}

	public void buildEditPanel()
	{
		ParamColorSelect fillEditor = new ParamColorSelect( fill, "Fill Color" );
		ParamColorSelect strokeEditor = new ParamColorSelect( stroke, "Stroke Color" );
		IntegerNumberEditor lineEdit = new IntegerNumberEditor( "Line width", lineWidth );
		CheckBoxEditor doFill = new CheckBoxEditor( fillOn, "Fill shape", true );

		addEditor( fillEditor );
		addRowSeparator();
		addEditor( strokeEditor );
		addRowSeparator();
		addEditor( lineEdit );
		addRowSeparator();
		addEditor( doFill );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		GeneralPath shape = getShape( frameTime );
		g.setColor( fill.get() );
		if( fillOn.get() ) 
			g.fill( shape );
		g.setStroke( new BasicStroke( lineWidth.get() ) );
		g.setColor( stroke.get() );
		g.draw( shape );
	}

	public ViewEditorLayer getEditorLayer()
	{
 		return new PolyCurveEditLayer( this );
	}

}//end class
