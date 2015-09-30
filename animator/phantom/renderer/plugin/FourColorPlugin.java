package animator.phantom.renderer.plugin;

/*
    Copyright Janne Liljeblad.

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

import java.awt.Color;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.ColorParam;

import com.jhlabs.image.FourColorFilter;

public class FourColorPlugin extends PhantomPlugin
{
	public ColorParam topLeft;
	public ColorParam topRight;
	public ColorParam bottomRight;
	public ColorParam bottomLeft;

	public FourColorPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "FourColor" );

		topLeft = new ColorParam( Color.green );
		topRight = new ColorParam( Color.blue );
		bottomRight = new ColorParam( Color.red );
		bottomLeft = new ColorParam( Color.yellow );

		registerParameter( topLeft );
		registerParameter( topRight );
		registerParameter( bottomRight );
		registerParameter( bottomLeft );
	}

	public void buildEditPanel()
	{
		ParamColorSelect topLeftE = new ParamColorSelect( topLeft, "Top left" );
		ParamColorSelect topRightE = new ParamColorSelect( topRight, "Top right");
		ParamColorSelect bottomRightE = new ParamColorSelect( bottomRight, "Bottom left" );
		ParamColorSelect bottomLeftE = new ParamColorSelect( bottomLeft, "Bottom righ" );

		addEditor( topLeftE );
		addRowSeparator();
		addEditor( topRightE );
		addRowSeparator();
		addEditor( bottomRightE );
		addRowSeparator();
		addEditor( bottomLeftE );
	 }
	
	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		FourColorFilter filter = new FourColorFilter();
		filter.setColorNW( topLeft.get().getRGB() );
		filter.setColorNE( topRight.get().getRGB() );
		filter.setColorSW( bottomRight.get().getRGB() );
		filter.setColorSE( bottomLeft.get().getRGB() );
		filter.setDimensions( img.getWidth(), img.getHeight() );

		PluginUtils.filterImage( img, filter );

		sendStaticSource( img, frame );
	}

}//end class