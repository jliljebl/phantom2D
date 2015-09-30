package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.BooleanComboBox;
import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.FloatParam;

import com.jhlabs.image.EmbossFilter;

public class EmbossPlugin extends PhantomPlugin
{
	private float DEGREES_TO_RADIANS = (float) Math.PI / 180;

	private BooleanParam emboss;
	private FloatParam azimuth;
	private FloatParam elevation;
	private FloatParam width45;

	public EmbossPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Emboss" );

		emboss = new BooleanParam(false);
		azimuth = new FloatParam( 135, 0.0f, 360.0f );
		elevation = new FloatParam( 30, 0.0f, 360.0f );
		width45 = new FloatParam( 3.0f );

		registerParameter( emboss );
		registerParameter( azimuth );
		registerParameter( elevation );
		registerParameter( width45 );
	}

	public void buildEditPanel()
	{
		FloatNumberEditor azimuthEdit = new FloatNumberEditor( "Azimuth", azimuth );
		FloatNumberEditor elevationEdit = new FloatNumberEditor( "Elevation", elevation );
		FloatNumberEditor widthEdit = new FloatNumberEditor( "Bump Height", width45 );
		BooleanComboBox embossEdit = new BooleanComboBox( emboss, "Emboss", "on", "off", true );

		addEditor( azimuthEdit );
		addRowSeparator();
		addEditor( elevationEdit );
		addRowSeparator();
		addEditor( widthEdit );
		addRowSeparator();
		addEditor( embossEdit );
	}

	public void doImageRendering( int frame )
	{
		EmbossFilter embossFilter = new EmbossFilter();
		embossFilter.setAzimuth( azimuth.get() * DEGREES_TO_RADIANS );
		embossFilter.setElevation( elevation.get() * DEGREES_TO_RADIANS );
		embossFilter.setBumpHeight( width45.get() );
		embossFilter.setEmboss( emboss.get() );

		applyFilter( embossFilter );
	}

}//end class
