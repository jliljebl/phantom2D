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

import giotto2D.filters.noise.Spread;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.IntegerParam;

public class SpreadPlugin extends PhantomPlugin
{
	private IntegerParam amountX;
	private IntegerParam amountY;

	public SpreadPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Spread" );

		 amountX = new IntegerParam( 5 );
		 amountY = new IntegerParam( 5 );

		registerParameter( amountX );
		registerParameter( amountY );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor amountXEdit = new  IntegerNumberEditor( "Amount X", amountX );
		IntegerNumberEditor amountYEdit = new  IntegerNumberEditor( "Amount Y", amountY );

		addEditor( amountXEdit );
		addRowSeparator();
		addEditor( amountYEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();
		BufferedImage filteredImage = PluginUtils.createScreenCanvas();

		Spread noise = new Spread();
		noise.setAmountX( amountX.get() > 0 ? amountX.get() : 0  );
		noise.setAmountY( amountY.get() > 0 ? amountY.get() : 0  );
		noise.filter( flowImg, filteredImage );

		sendFilteredImage( filteredImage, frame );
	}

}//end class
