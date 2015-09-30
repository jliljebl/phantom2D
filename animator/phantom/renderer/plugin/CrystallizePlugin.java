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

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.BooleanComboBox;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;

import com.jhlabs.image.CrystallizeFilter;

public class CrystallizePlugin extends PhantomPlugin
{
	public AnimatedValue edgThick;
	public BooleanParam edgFade;
	public ColorParam edgColor;

	public CrystallizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Crystallize" );

		edgThick = new AnimatedValue( 0.4f );
		edgFade = new BooleanParam( false );
		edgColor = new ColorParam( Color.black );

		registerParameter( edgThick );
		registerParameter( edgFade );
		registerParameter( edgColor );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor widthEdit = new AnimValueNumberEditor( "Edge thickness", edgThick );
		ParamColorSelect eColorEdit = new ParamColorSelect( edgColor, "Edge Color" );
 		BooleanComboBox fadeEdit = new BooleanComboBox( edgFade, "Fade edges", "Yes","No", false );

		addEditor( widthEdit );
		addRowSeparator();
		addEditor( eColorEdit );
		addRowSeparator();
		addEditor( fadeEdit );
	}

	public void doImageRendering( int frame )
	{
		CrystallizeFilter f = new CrystallizeFilter();

		f.setEdgeThickness(edgThick.getValue( frame ) );
		f.setFadeEdges(edgFade.get() );
		f.setEdgeColor(edgColor.get().getRGB() );

		applyFilter( f );
	}

}//end class
