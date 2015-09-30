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

import giotto2D.filters.noise.ScatterRGB;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

public class ScatterRGBPlugin extends PhantomPlugin
{
	private BooleanParam correlated;
	private BooleanParam independent;
	private IntegerParam RNoise;
	//private IntegerParam GNoise;
	//private IntegerParam BNoise;

	public ScatterRGBPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "ScatterRGB" );

		correlated = new BooleanParam( false );
		independent = new BooleanParam( false );
		RNoise = new IntegerParam( 20, 0, 100 );
		//GNoise = new IntegerParam( 20, 0, 100 );
		//BNoise = new IntegerParam( 20, 0, 100 );

		registerParameter( correlated );
		registerParameter( independent );
		registerParameter( RNoise );
		//registerParameter( GNoise );
		//registerParameter( BNoise );
	}

	public void buildEditPanel()
	{
		CheckBoxEditor correlatedEdit = new CheckBoxEditor( correlated, "Correlated noise", true );
		CheckBoxEditor independentEdit = new CheckBoxEditor( independent, "Intependent RGB", true );
		IntegerValueSliderEditor rEdit = new IntegerValueSliderEditor( "Amount", RNoise );
		//IntegerValueSliderEditor gEdit = new IntegerValueSliderEditor( "Blue noise", GNoise );
		//IntegerValueSliderEditor bEdit = new IntegerValueSliderEditor( "Green noise", BNoise );

		addEditor( correlatedEdit );
		addRowSeparator();
		addEditor( independentEdit );
		addRowSeparator();
		addEditor( rEdit );
//		addRowSeparator();
//		addEditor( gEdit );
//		addRowSeparator();
//		addEditor( bEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();
		BufferedImage filteredImage = PluginUtils.createScreenCanvas();

		ScatterRGB noise = new ScatterRGB();
		noise.setCorrelated( correlated.get() );
		noise.setIndependent( independent.get() );
		noise.setNoise( RNoise.get() / 100.0, 0.0, 0.0 );
		noise.filter( flowImg, filteredImage );

		sendFilteredImage( filteredImage, frame );
	}

}//end class
