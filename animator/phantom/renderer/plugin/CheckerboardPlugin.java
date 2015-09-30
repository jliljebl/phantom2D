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

import giotto2D.filters.render.Checkerboard;

import java.awt.Color;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

public class CheckerboardPlugin extends PhantomPlugin
{
	private ColorParam color1;
	private ColorParam color2;

 	private IntegerParam size;
	private BooleanParam isVariable;

	public CheckerboardPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "CheckerBoard" );

		color1 = new ColorParam( new Color( 0,0,0 ) );
		color2  = new ColorParam( new Color( 255,255,255 ) );
		size = new IntegerParam( 20 );
		isVariable = new BooleanParam( false );
	
		registerParameter( color1 );
		registerParameter( color2 );
		registerParameter( size );
		registerParameter( isVariable );
	}

	public void buildEditPanel()
	{
		ParamColorSelect colorEditor1 = new ParamColorSelect( color1, "Select color 1" );
		ParamColorSelect colorEditor2 = new ParamColorSelect( color2, "Select color 1" );

		IntegerNumberEditor sizeEdit = new IntegerNumberEditor( "Size", size );
		CheckBoxEditor variableBox = new CheckBoxEditor( isVariable, "Variable pattern", true );

		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( colorEditor2 );
		addRowSeparator();
		addEditor( sizeEdit );
		addRowSeparator();
		addEditor( variableBox );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );
		
		Checkerboard cb = new Checkerboard();
		int mode = 0;
		if( isVariable.get() )
			mode = 1;
		cb.setFilterValues( mode, size.get(), color1.get(), color2.get() );
		cb.filter( img );

		sendStaticSource( img, frame );
	}

}//end class
