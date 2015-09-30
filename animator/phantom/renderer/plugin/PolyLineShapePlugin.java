package animator.phantom.renderer.plugin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import animator.phantom.gui.view.editlayer.PolyLineEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

public class PolyLineShapePlugin extends PolyLinePlugin
{
	public ColorParam fill = new ColorParam( Color.lightGray );
	public ColorParam stroke = new ColorParam( Color.white );

	public IntegerParam lineWidth = new IntegerParam( 5 );
	public IntegerParam cap = new IntegerParam( 0 );
	public IntegerParam join = new IntegerParam( 0 );
	public IntegerParam miterLimit = new IntegerParam( 10, 1, 180 );
	public BooleanParam fillOn = new BooleanParam( true );

	public static final String[] CAP_OPTS = { "Butt","Round","Square" };
	public static final String[] JOIN_OPTS = { "Bevel","Miter","Round" };

	public PolyLineShapePlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "LineShape" );

		registerPathParams();
		
		registerParameter( fill );
		registerParameter( stroke );
		registerParameter( lineWidth );
		registerParameter( cap );
		registerParameter( join );
		registerParameter( miterLimit );
		registerParameter( fillOn );
	}

	public void buildEditPanel()
	{
		ParamColorSelect fillEditor = new ParamColorSelect( fill, "Fill Color" );
		ParamColorSelect strokeEditor = new ParamColorSelect( stroke, "Stroke Color" );

		IntegerComboBox joinSelect = new IntegerComboBox( join, "Join", JOIN_OPTS );
		IntegerComboBox capSelect = new IntegerComboBox( cap, "Caps", CAP_OPTS );
		IntegerNumberEditor lineEdit = new IntegerNumberEditor( "Line width", lineWidth );
		IntegerNumberEditor miterLimitE = new IntegerNumberEditor( "Miter Limit", miterLimit );
		CheckBoxEditor doFill = new CheckBoxEditor(fillOn, "Fill shape", true );

		addEditor( fillEditor );
		addRowSeparator();
		addEditor( strokeEditor );
		addRowSeparator();
		addEditor( lineEdit );
		addRowSeparator();
		addEditor( joinSelect );
		addRowSeparator();
		addEditor( capSelect );
		addRowSeparator();
		addEditor( miterLimitE );
		addRowSeparator();
		addEditor( doFill );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		GeneralPath shape = getShape( frameTime );
		g.setColor( fill.get() );
		if( fillOn.get() ) g.fill( shape );
		g.setStroke( getStroke() );
		g.setColor( stroke.get() );
		g.draw( shape );
	}

	private BasicStroke getStroke()
	{
		float width = (float) lineWidth.get();

		int capV;
		if( cap.get() == 0 ) capV = BasicStroke.CAP_BUTT;
		else if( cap.get() == 1 ) capV =  BasicStroke.CAP_ROUND;
		else  capV =  BasicStroke.CAP_SQUARE;

		int joinV;
		if( join.get() == 0 ) joinV = BasicStroke.JOIN_BEVEL;
		else if( join.get() == 1 ) joinV = BasicStroke.JOIN_MITER;
		else joinV = BasicStroke.JOIN_ROUND;

		float miterlimit = 10.0f;
		return new BasicStroke( width, capV, joinV, miterlimit );
	}

	public ViewEditorLayer getEditorLayer()
	{
 		return new PolyLineEditLayer( this );
	}

}//end class
