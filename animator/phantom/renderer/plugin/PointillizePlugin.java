package animator.phantom.renderer.plugin;

import java.awt.Color;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;

import com.jhlabs.image.PointillizeFilter;

public class PointillizePlugin extends PhantomPlugin
{
	public AnimatedValue edgeThickness;
	public BooleanParam fadeEdges;
	public ColorParam edgeColor;
	public AnimatedValue fuzziness;

	public PointillizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Pointillize" );

		edgeThickness = new AnimatedValue( 0.4f );
		fadeEdges = new BooleanParam( false );
		edgeColor = new ColorParam( new Color( 100,100,100 ) );
		fuzziness = new AnimatedValue( 0.1f  );

		registerParameter( edgeThickness );
		registerParameter( fadeEdges );
		registerParameter( edgeColor );
		registerParameter( fuzziness );	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor edgeThicknessE = new AnimValueNumberEditor( "Edge thickness", edgeThickness );
		CheckBoxEditor fadeEdgesE = new CheckBoxEditor( fadeEdges, "Fade edges", true );
		ParamColorSelect ecolE = new ParamColorSelect( edgeColor, "Edge color" );
		AnimValueNumberEditor fuzzinessE = new  AnimValueNumberEditor( "Fuzziness", fuzziness );

		addEditor( edgeThicknessE );
		addRowSeparator();
		addEditor( fadeEdgesE );
		addRowSeparator();
		addEditor( ecolE );
		addRowSeparator();
		addEditor( fuzzinessE );
	}

	public void doImageRendering( int frame )
	{
		PointillizeFilter pointillizeFilter = new PointillizeFilter();
		pointillizeFilter.setEdgeThickness( edgeThickness.getValue( frame ) );
		pointillizeFilter.setFadeEdges( fadeEdges.get() );
		pointillizeFilter.setEdgeColor( edgeColor.get().getRGB() );
		pointillizeFilter.setFuzziness( fuzziness.getValue( frame )  );

		applyFilter( pointillizeFilter );
	}

}//end class
