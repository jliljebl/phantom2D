package animator.phantom.renderer.plugin;

/*
    Copyright Janne Liljeblad 2006,2007,2008

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
import giotto2D.core.GTTRectangle;
import giotto2D.core.GeometricFunctions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ColorParam;

public class StripesPlugin extends PhantomPlugin
{

	//private ColorParam bgColor = new ColorParam(Color.white);
	private ColorParam fgColor = new ColorParam(Color.white);
	private AnimatedValue y = new AnimatedValue( 0 );
	private AnimatedValue width = new AnimatedValue( 20 );
	private AnimatedValue size = new AnimatedValue( 50 );
	private AnimatedValue rotation = new AnimatedValue( 0 );

	public StripesPlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
 		setName( "Stripes" );
		registerParameter( y );
		registerParameter( rotation );
		registerParameter( width );
		registerParameter( fgColor  );
		//registerParameter( bgColor  );
		registerParameter( size );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor yEdit = new AnimValueNumberEditor( "Y Position", y );
		AnimValueNumberEditor sizeEdit = new AnimValueNumberEditor( "Line size", size );
		AnimValueNumberEditor wEdit = new AnimValueNumberEditor( "Line max width", width );
		AnimValueNumberEditor rotationEdit = new AnimValueNumberEditor( "Rotation", rotation );

		addEditor( new ParamColorSelect( fgColor, "Line color" ) );
		//addRowSeparator();
		//addEditor( new ParamColorSelect( bgColor, "Background color" ) );
		addRowSeparator();
		addEditor( wEdit );
		addRowSeparator();
		addEditor( sizeEdit );
		addRowSeparator();
		addEditor( rotationEdit );
		addRowSeparator();
		addEditor( yEdit );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D graphics, int canvasWidth, int canvasHeight )
	{
		//--- Paint bg
		//graphics.setColor( bgColor.get() );
		//graphics.fillRect( 0, 0, canvasWidth, canvasHeight );

		//--- x, y, rotation
		float yd = y.getValue( frameTime );
		float rVal = rotation.getValue( frameTime );
		float sizeVal = size.getValue( frameTime );

		//--- Get graphics and transform it 
		graphics.rotate( Math.toRadians( (double) rVal ) );
		graphics.translate( 0, yd );

		//--- Get rect of output img in untranslated space
		Point2D.Float topLeft = new Point2D.Float( 0, 0 );
		Point2D.Float topRight = new Point2D.Float( canvasWidth, 0);
		Point2D.Float bottomRight = new Point2D.Float( 0, canvasHeight );
		Point2D.Float bottomLeft = new Point2D.Float( canvasWidth, canvasHeight );

		//--- rotate output rect
		topRight = GeometricFunctions.rotatePointAroundOrigo( -rVal, topRight );
		bottomRight = GeometricFunctions.rotatePointAroundOrigo( -rVal, bottomRight );
		bottomLeft = GeometricFunctions.rotatePointAroundOrigo( -rVal, bottomLeft );

		//--- move output rect
		move( topLeft, 0, -yd );
		move( topRight, 0, -yd );
		move( bottomRight, 0, -yd );
		move( bottomLeft, 0, -yd );

		//--- Get min and max x and y
		Point2D.Float[] points = { topLeft, topRight, bottomRight, bottomLeft };
		float minY = GeometricFunctions.getMinY( points );
		float maxY = GeometricFunctions.getMaxY( points );
		float maxX = GeometricFunctions.getMaxX( points );
		float minX = GeometricFunctions.getMinX( points );

		//---
		float cellWidth = Math.max((maxX - minX), (maxY - minY));
		float cellHeight = width.getValue( frameTime );//width == line width == shape heioght

		//--- 
		float shapeWidth = cellWidth;
		float shapeHeight = cellHeight * (sizeVal / 100.f);

		//--- Create line object for drawing
		GTTObject shape = new GTTRectangle( shapeWidth, shapeHeight );
		shape.setFillPaint( fgColor.get() );
		shape.setAnchorPoint( 0, cellHeight / 2 );//into center of shape

		int endY = ((int)((maxY - minY) / cellHeight) + 4);
		float Ypos = yd % cellHeight;
		for( int j = 0; j < endY; j++)
		{
			//--- center pos for shape in grid box
			float x = minX;
			float y = minY -cellHeight + j * cellHeight + Ypos;
			//--- draw
			shape.setPos( x, y );
			shape.draw( graphics, new Rectangle( 0, 0, canvasWidth, canvasHeight ));
			
		}
	}
	
	private static void move( Point2D.Float p, float dx, float dy )
	{
		float sx = (float) p.getX();
		float sy = (float) p.getY();
		p.setLocation( sx + dx, sy + dy ); 
	}

}//end class