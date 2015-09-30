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

import giotto2D.filters.blur.Pixelize;
import giotto2D.filters.color.MaxRGB;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.EdgeFilter;

public class PredatorPlugin extends PhantomPlugin
{
	private IntegerParam size;


	public PredatorPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Predator" );

		size = new IntegerParam( 10, 1, 50 );
		registerParameter( size );
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor amountEdit = new  IntegerValueSliderEditor( "Grid size", size );
		addEditor( amountEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();

		MaxRGB f = new MaxRGB();
		f.filter( flowImg );

		Pixelize pf = new Pixelize();
		pf.setXSize( size.get() );
		pf.setYSize( size.get() );
		pf.filter( flowImg );

		EdgeFilter edgeFilter = new EdgeFilter();
		edgeFilter.setVEdgeMatrix( EdgeFilter.SOBEL_V  );
		edgeFilter.setHEdgeMatrix( EdgeFilter.SOBEL_H );
		Graphics2D gc = flowImg.createGraphics();
		gc.drawImage( flowImg, edgeFilter, 0, 0 );
		gc.dispose();

		sendFilteredImage( flowImg, frame );
	}

}//end class
