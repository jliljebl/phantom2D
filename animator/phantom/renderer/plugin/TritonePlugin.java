package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.ArrayColormap;

public class TritonePlugin extends PhantomPlugin
{
	public ColorParam lightColor = new ColorParam( new Color( 255, 255, 255 ) );
	public ColorParam darkColor = new ColorParam( new Color( 0,0,0 ) );
	public ColorParam middleColor = new ColorParam( new Color( 128,128,128 ) );
	public IntegerParam middleValue = new IntegerParam( 128 );

	private int[] rbgLook = new int[256];

	public static final int alpha_mask = 0x00ffffff;
	public static final int alpha = 24;
	public static final int red = 16;
	public static final int green = 8;
	public static final int MAX_RGB = 3 * 255;

	public TritonePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "TriTone" );

		registerParameter( lightColor );
		registerParameter( middleColor );
		registerParameter( darkColor );
		registerParameter( middleValue );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor mVal = new IntegerNumberEditor( "Middle threshold", middleValue);
		ParamColorSelect lightEditor = new ParamColorSelect( lightColor, "Light Color" );
		ParamColorSelect darkEditor = new ParamColorSelect( darkColor, "Dark Color" );
		ParamColorSelect middleEditor = new ParamColorSelect( middleColor, "Middle Color" );

		addEditor( lightEditor );
		addRowSeparator();
		addEditor( mVal );
		addRowSeparator();
		addEditor( middleEditor );
		addRowSeparator();
		addEditor( darkEditor );
	}

	public void doImageRendering( int frame )
	{
		//--- Crate lookup for color replacement	
		ArrayColormap cmap = new ArrayColormap();
		cmap.setColor( 0, darkColor.get().getRGB() );
		cmap.setColor( 255, lightColor.get().getRGB() );
		cmap.setColorInterpolated(middleValue.get(), 0, 255, middleColor.get().getRGB() );

		for( int j = 0; j < 256; j++ )
			rbgLook[ j ] = cmap.getColor( (float) j / 255.0f ) & alpha_mask;

		//--- Do color replacement.
		BufferedImage source  = getFlowImage();
		int[] pix = getBank( source );
		int rbg;
		int lumaValue;
		int a;
		int r;
		int g;
		int b;

		for( int i = 0; i < pix.length; i++ )
		{
			a = ( pix[ i ] >> alpha ) & 0xff;
			r = ( pix[ i ] >> red ) & 0xff;
			g = ( pix[ i ] >> green ) & 0xff;
			b = ( pix[ i ] & 0xff );

			lumaValue = (( r + g + b ) * 255 ) / MAX_RGB;
			rbg = rbgLook[ lumaValue ];

			pix[ i ] =  a << alpha | rbg;
		}

		sendFilteredImage( source, frame );

	}

	public static int[] getBank( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();
		DataBufferInt dbuf = (DataBufferInt) imgRaster.getDataBuffer();
		return dbuf.getData( 0 );
	}

}//end class
