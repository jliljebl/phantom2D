package animator.phantom.renderer.plugin;

/*
    Copyright Janne Liljeblad

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import giotto2D.core.GTTObject;
import giotto2D.core.GTTOval;
import giotto2D.core.GTTRectangle;
import giotto2D.core.GTTRoundRectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import animator.phantom.gui.view.editlayer.ShapeEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.FloatParam;
import animator.phantom.renderer.param.IntegerParam;

public class ShapePlugin extends PhantomPlugin
{
	//--- Unscaled width of shape, not user settable.
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;

	public IntegerParam shapeType = new IntegerParam( 0 );
	public ColorParam fill = new ColorParam( Color.lightGray );
	public ColorParam stroke = new ColorParam( Color.white );
	public IntegerParam lineWidth = new IntegerParam( 5 );
	public FloatParam roundness = new FloatParam( 10.0f );
	public BooleanParam fillOn = new BooleanParam( true );

	public ShapePlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
		//getIOP().setCenterable();
	}

	public void buildDataModel()
	{
		setName( "Shape" );

		registerCoords();
		registerParameter( shapeType );
		registerParameter( fill );
		registerParameter( stroke );
		registerParameter( lineWidth );
		registerParameter( roundness );
		registerParameter( fillOn );
	}

	public void buildEditPanel()
	{
		String[] options = { "Rectangle","Oval","Round rextangle" };
		IntegerComboBox shapeSelect = new IntegerComboBox( shapeType,
									"Mask shape",
									 options );
		ParamColorSelect fillEditor = new ParamColorSelect( fill, "Fill Color" );
		ParamColorSelect strokeEditor = new ParamColorSelect( stroke, "Stroke Color" );
		IntegerNumberEditor lineEdit = new IntegerNumberEditor( "Line width", lineWidth );
		CheckBoxEditor doFill = new CheckBoxEditor( fillOn, "Fill shape", true );
		FloatNumberEditor roundEdit = new FloatNumberEditor( "Roundness", roundness, 10 );

		addEditor( shapeSelect );
		addRowSeparator();

		addCoordsEditors();// ComponentsVector( coords.getEditComponents() );

		addEditor( fillEditor );
		addRowSeparator();
		addEditor( strokeEditor );
		addRowSeparator();
		addEditor( lineEdit );
		addRowSeparator();
		addEditor( roundEdit );
		addRowSeparator();
		addEditor( doFill );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		AnimatedImageCoordinates coords = getCoords();

		float x = coords.x.getValue( frameTime );
		float y = coords.y.getValue( frameTime );
		float xScale = coords.xScale.getValue( frameTime ) / AnimatedImageCoordinates.xScaleDefault;// normalize
		float yScale = coords.yScale.getValue( frameTime ) / AnimatedImageCoordinates.yScaleDefault;// normalize
		float xAnch = coords.xAnchor.getValue( frameTime );
		float yAnch = coords.yAnchor.getValue( frameTime );
		float rotation = coords.rotation.getValue( frameTime );

		GTTObject shape;
		if( shapeType.get() == 0 ) 
			shape = new GTTRectangle( WIDTH, HEIGHT );
		else if( shapeType.get() == 2 )
			shape =  new GTTRoundRectangle( (float)WIDTH, (float)HEIGHT, roundness.get());
		else 
			shape = new GTTOval( WIDTH, HEIGHT );

		shape.setScale( xScale, yScale );
		shape.setRotation( rotation );
		shape.setPos( x, y );
		shape.setAnchorPoint( xAnch, yAnch );
		shape.setFillPaint( fill.get() );
		shape.setFillVisible( fillOn.get() );
		shape.setStrokePaint( stroke.get() );
		shape.setStrokeWidth( lineWidth.get() );

		shape.draw( g, new Rectangle( 0, 0, width,  height ));
	}
	
	public ViewEditorLayer getEditorLayer()
	{
 		return new ShapeEditLayer( this );
	}

}//end class