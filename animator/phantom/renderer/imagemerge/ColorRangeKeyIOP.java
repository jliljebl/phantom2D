package animator.phantom.renderer.imagemerge;

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

import giotto2D.filters.merge.ColorPointKey;
import giotto2D.filters.merge.MatteClean;
import giotto2D.libcolor.GiottoRGBInt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.ColorPickListener;
import animator.phantom.gui.view.editlayer.ColorRangeKeyEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.ParamColorDisplay;
import animator.phantom.paramedit.imagemerge.ColorRangeKeyEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerVectorParam;

public class ColorRangeKeyIOP extends ImageOperation implements ColorPickListener
{
	//--- Key
	public Vector<GiottoRGBInt> colors = new Vector<GiottoRGBInt>();
	private IntegerVectorParam colors_red = new IntegerVectorParam("colorsred");
	private IntegerVectorParam colors_green = new IntegerVectorParam("colorsgreen");
	private IntegerVectorParam colors_blue = new IntegerVectorParam("colorsblue");

	public AnimatedValue spread;
	public CRCurveParam alphaCurve = new CRCurveParam("alpha");

	private ColorPointKey cpKey = new ColorPointKey();

	public ColorParam dispColor = new ColorParam( Color.black );
	private ParamColorDisplay cDisp;

	//--- FG/BG Clean
	public static final int FOREGROUND = 0;
	public static final int BACKGROUND = 1;

	public Vector<GiottoRGBInt> fgColors = new Vector<GiottoRGBInt>();//not saved
	private IntegerVectorParam fg_colors_red = new IntegerVectorParam("fgcolorsblue");//saved-params
	private IntegerVectorParam fg_colors_green = new IntegerVectorParam("fgcolorsblue");
	private IntegerVectorParam fg_colors_blue = new IntegerVectorParam("fgcolorsblue");

	public Vector<GiottoRGBInt> bgColors = new Vector<GiottoRGBInt>();//not saved
	private IntegerVectorParam bg_colors_red = new IntegerVectorParam("bgcolorsblue");//saved-params
	private IntegerVectorParam bg_colors_green = new IntegerVectorParam("bgcolorsblue");
	private IntegerVectorParam bg_colors_blue = new IntegerVectorParam("bgcolorsblue");

	public ColorParam fgColor = new ColorParam( Color.black );
	private ParamColorDisplay fgDisp;

	public ColorParam bgColor = new ColorParam( Color.black );
	private ParamColorDisplay bgDisp;

	private MatteClean mcFilter = new MatteClean();

	public ColorRangeKeyIOP()
	{
		name = "ColorRangeKey";
 			
		spread = new AnimatedValue( this, 5, 0, 127  );

		registerParameter( spread );
		registerParameter( alphaCurve );

		registerParameter( colors_red );
		registerParameter( colors_green );
		registerParameter( colors_blue );

		registerParameter( fg_colors_red );
		registerParameter( fg_colors_green );
		registerParameter( fg_colors_blue );

		registerParameter( bg_colors_red );
		registerParameter( bg_colors_green );
		registerParameter( bg_colors_blue );
	}

	public ParamEditPanel getEditPanelInstance()
	{
		ColorRangeKeyEditPanel panel = new ColorRangeKeyEditPanel( this );
		cDisp = panel.cDisp;
		fgDisp = panel.fgDisp;
		bgDisp = panel.bgDisp;
		updateCleanDisp();
		updateColorDisp();
		return panel;
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		//--- At load colors are read into elemnts vecs and
		//--- and colors vec needs to be created.
		if( colors.size() != colors_red.get().size() )
			fillColors();

		//--- key
		cpKey.setColorPoints( colors );
		int[] convTable = alphaCurve.curve.getCurveCopy( true );
		cpKey.setValToAlphaTable( convTable );
		cpKey.setSlopeWidth( (int) spread.getValue( frame ) );
		cpKey.createLookUps();
		cpKey.filter( renderedImage );

		//--- clean
		mcFilter.setColorPoints( bgColors, fgColors );
		mcFilter.filter( renderedImage );
	}

	private void fillColors()
	{
		Vector<Integer> cr = colors_red.get();
		Vector<Integer> cg = colors_green.get();
		Vector<Integer> cb = colors_blue.get();
		for( int i = 0; i < cr.size(); i++ )
		{
			GiottoRGBInt gc = new GiottoRGBInt( 	cr.elementAt( i ).intValue(),
								cg.elementAt( i ).intValue(),
								cb.elementAt( i ).intValue() );
			colors.add( gc );
		}

		Vector<Integer> br = bg_colors_red.get();
		Vector<Integer> bg = bg_colors_green.get();
		Vector<Integer> bb = bg_colors_blue.get();
		for( int i = 0; i < bg_colors_red.get().size(); i++ )
		{
			GiottoRGBInt gc = new GiottoRGBInt( 	br.elementAt( i ).intValue(),
								bg.elementAt( i ).intValue(),
								bb.elementAt( i ).intValue() );
			bgColors.add( gc );
		}

		Vector<Integer> fr = fg_colors_red.get();
		Vector<Integer> fg = fg_colors_green.get();
		Vector<Integer> fb = fg_colors_blue.get();
		for( int i = 0; i < fg_colors_red.get().size(); i++ )
		{
			GiottoRGBInt gc = new GiottoRGBInt( 	fr.elementAt( i ).intValue(),
								fg.elementAt( i ).intValue(),
								fb.elementAt( i ).intValue() );
			fgColors.add( gc );
		}
	}

	public void colorPicked( Color c )
	{
		if( c == null ) return;

		GiottoRGBInt gc = new GiottoRGBInt( c.getRed(), c.getGreen(), c.getBlue() );
		int mode = GUIComponents.getViewEditor().getMode();

		if( mode == ViewEditorLayer.PICK_COLOR_MODE )
		{
			colors.add( gc );
			dispColor.set( c );

			colors_red.get().add( gc.r );
			colors_green.get().add( gc.g );
			colors_blue.get().add( gc.b );
			cDisp.setText( "Samples: " + Integer.toString( colors.size() ) );
		}

		if( mode == ViewEditorLayer.PICK_FG_COLOR_MODE )
		{
			fgColors.add( gc );
			fgColor.set( c );

			fg_colors_red.get().add( gc.r );
			fg_colors_green.get().add( gc.g );
			fg_colors_blue.get().add( gc.b );

			fgDisp.setText( "Samples: " + Integer.toString( fgColors.size() ) );
		}

		if( mode == ViewEditorLayer.PICK_BG_COLOR_MODE )
		{
			bgColors.add( gc );
			bgColor.set( c );

			bg_colors_red.get().add( gc.r );
			bg_colors_green.get().add( gc.g );
			bg_colors_blue.get().add( gc.b );

			bgDisp.setText( "Samples: " + Integer.toString( bgColors.size() ) );
		}
	}

	//--- key
	public void deleteLast()
	{
		if( colors.size() == 0) return;
		colors.remove(colors.size() - 1);
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		updateColorDisp();
	}

	//--- clean
	public void deleteLast( int range )
	{
		if( range == FOREGROUND )
		{
			if( fgColors.size() == 0) return;
			fgColors.remove(fgColors.size() - 1);
		}
		else
		{
			if( bgColors.size() == 0) return;
			bgColors.remove(bgColors.size() - 1);
		}
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		updateCleanDisp();
	}

	//--- key 
	public void deleteAll()
	{
		colors = new Vector<GiottoRGBInt>();
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		updateColorDisp();
	}

	//--- clean
	public void deleteAll( int range )
	{
		if( range == FOREGROUND )
			fgColors = new Vector<GiottoRGBInt>();
		else
			bgColors = new Vector<GiottoRGBInt>();

		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		updateCleanDisp();
	}

	//--- key
	private void updateColorDisp()
	{
		cDisp.setText( "Samples: " + Integer.toString( colors.size() ) );
		if( colors.size() == 0 ) dispColor.set( Color.black );
		else
		{
			GiottoRGBInt c = colors.elementAt( colors.size() - 1 );
			Color col = new Color( c.r, c.g, c.b );
			dispColor.set( col );
		}
		cDisp.frameChanged();
	}

	//--- clean
	private void updateCleanDisp()
	{
		fgDisp.setText( "Samples: " + Integer.toString( fgColors.size() ) );
		bgDisp.setText( "Samples: " + Integer.toString( bgColors.size() ) );

		if( fgColors.size() == 0 ) fgColor.set( Color.black );
		else
		{
			GiottoRGBInt c = fgColors.elementAt( fgColors.size() - 1 );
			Color col = new Color( c.r, c.g, c.b );
			fgColor.set( col );
		}

		if( bgColors.size() == 0 ) bgColor.set( Color.black );
		else
		{
			GiottoRGBInt c = bgColors.elementAt( bgColors.size() - 1 );
			Color col = new Color( c.r, c.g, c.b );
			bgColor.set( col );
		}

		fgDisp.frameChanged();
		bgDisp.frameChanged();
	}

	public ViewEditorLayer getEditorlayer()
	{
		return new ColorRangeKeyEditLayer( this, this );
	}

}//end class
