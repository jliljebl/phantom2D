package animator.phantom.controller;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import animator.phantom.bezier.CurvePoint;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.FloatParam;
import animator.phantom.renderer.param.IOPParam;
import animator.phantom.renderer.param.IOPVectorParam;
import animator.phantom.renderer.param.IntegerAnimatedValue;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.param.IntegerVectorParam;
import animator.phantom.renderer.param.Param;
import animator.phantom.xml.ImageOperationXML;
import animator.phantom.xml.ParamXML;
import animator.phantom.xml.ValueXML;

public class PhantomServerParameter
{
	public static final String ANIMATED_VALUE;
	public static final String BOOLEAN_VALUE;
	public static final String INTEGER_VALUE;
	public static final String COLOR_VALUE;
	public static final String IOP_PARAM;
	public static final String IOP_VECTOR_PARAM;
	public static final String INTEGER_VECTOR_PARAM;
	public static final String CRCURVE_PARAM;
	public static final String ANIM_VAL_VEC_PARAM;
	public static final String ANIMATED_INTEGER_VALUE;
	public static final String FLOAT_VALUE;

	static
	{
		ANIMATED_VALUE = paramType( (new AnimatedValue()).getClass().getName() );
		BOOLEAN_VALUE = paramType( (new BooleanParam()).getClass().getName() );
		INTEGER_VALUE = paramType( (new IntegerParam()).getClass().getName() );
		COLOR_VALUE = paramType( (new ColorParam()).getClass().getName() );
		IOP_PARAM = paramType( (new IOPParam()).getClass().getName() );
		IOP_VECTOR_PARAM = paramType( (new IOPVectorParam()).getClass().getName() );
		INTEGER_VECTOR_PARAM = paramType( (new IntegerVectorParam()).getClass().getName() );
		CRCURVE_PARAM = paramType( new CRCurveParam().getClass().getName() );
 		ANIM_VAL_VEC_PARAM = paramType( new AnimatedValueVectorParam().getClass().getName() );
		ANIMATED_INTEGER_VALUE = paramType( new IntegerAnimatedValue().getClass().getName() );
		FLOAT_VALUE = paramType( new FloatParam().getClass().getName() );
	}

	public static void writeParamValue( Param p, Vector<String> tokens )
	{
		String type = paramType( p.getClass().getName() );
		
		if( type.equals( BOOLEAN_VALUE ) ) updateBooleanParam( p, tokens );
		else if( type.equals( INTEGER_VALUE ) ) updateIntegerValue( p, tokens );
		else if( type.equals( COLOR_VALUE ) ) updateColorParam( p, tokens);
		else if( type.equals( FLOAT_VALUE ) ) updateFloatParam(  p, tokens );
		/*
		else if( type.equals( ANIMATED_VALUE ) ) ValueXML.readAnimatedValue( e, p );
		else if( type.equals( IOP_PARAM ) ) ValueXML.readIOPParamValue( e, p );
		else if( type.equals( IOP_VECTOR_PARAM ) ) ValueXML.readIOPVectorValue( e, p );
		else if( type.equals( INTEGER_VECTOR_PARAM ) ) ValueXML.readIntVectorValue( e, p );
		else if( type.equals( CRCURVE_PARAM ) ) ValueXML.readCRCurveValue( e, p );
		else if( type.equals( ANIM_VAL_VEC_PARAM ) ) ValueXML.readAnimValVecValue( e, p );
		else if( type.equals( ANIMATED_INTEGER_VALUE ) ) ValueXML.readAnimatedValue( e, p );
		*/
	}

	private static String paramType( String className )
	{
		int lastDot = className.lastIndexOf( "." );
		return className.substring(lastDot + 1);
	}
	
	//------------------------------------------------------------ UPDATE
	public static void updateBooleanParam( Param p, Vector<String> valueTokens )
	{
		boolean value = Boolean.parseBoolean( valueTokens.elementAt( 0 ) );
		((BooleanParam) p).set( value );
	}
	public static void updateIntegerValue( Param p, Vector<String> valueTokens )
	{
		int value  = Integer.parseInt( valueTokens.elementAt( 0 ) );
		((IntegerParam) p).set( value );
	}
	public static void updateFloatParam( Param p,  Vector<String> valueTokens )
	{
		float value = Float.parseFloat( valueTokens.elementAt( 0 ) );
		((FloatParam) p).set( value );
	}
	public static void updateColorParam(  Param p, Vector<String> valueTokens )
	{
		ColorParam cv = (ColorParam)p;
		int r  = Integer.parseInt( valueTokens.elementAt( 0 ) );
		int g  = Integer.parseInt( valueTokens.elementAt( 1) );
		int b  = Integer.parseInt( valueTokens.elementAt( 2 ) );
		Color col = new Color( r, g, b );
		cv.set( col );
	}
	/*
	public static void readAnimatedValue( Element e, Param p )
	{
		AnimatedValue av = (AnimatedValue)p;
		av.setLocked( getBoolean( e, "l" ) );
		av.setRestrictedValueRange( getBoolean( e, "r" ) );
		av.setStepped( getBoolean( e, "stepped" ) );
		av.setMinValue( getFloat( e, "min" ) );
		av.setMaxValue( getFloat( e, "max" ) );
		//--- keyframes
		NodeList kflist = e.getElementsByTagName( KF_ELEMENT_NAME );
		Vector <AnimationKeyFrame> kframes = new Vector <AnimationKeyFrame>();
		for( int i = 0; i < kflist.getLength(); i++ )
		{
			Element kfE = (Element) kflist.item( i );
			AnimationKeyFrame kf = getKF( kfE );
			kframes.add( kf );
		}
		av.setKeyFrames( kframes );
	}
	public static void readIOPParamValue( Element e, Param p )
	{
		IOPParam iopP = (IOPParam) p;
 		iopP.setNodeID( getInt( e, "nodeid" ) );
	}
	public static void readIOPVectorValue( Element e, Param p )
	{
		IOPVectorParam iopVP = (IOPVectorParam) p;
 		Vector<Integer>  iopNodeIDs = new Vector<Integer>();
		NodeList idlist = e.getElementsByTagName( NODE_ID_ELEMENT_NAME );
		for( int i = 0; i < idlist.getLength(); i++ )
		{
			Element idE = (Element) idlist.item( i );
			iopNodeIDs.add( new Integer( getInt( idE, "id" ) ));
		}
		iopVP.setNodeIDs( iopNodeIDs );
	}

	public static void readIntVectorValue( Element e, Param p )
	{
		IntegerVectorParam iVec = (IntegerVectorParam) p;
		Vector<Integer> valVec = new Vector<Integer>(); 
		String val = e.getAttribute("arraydata");
		StringTokenizer tok = new StringTokenizer(val, ";" );
		String token;
		while (tok.hasMoreTokens()) 
		{
			token = tok.nextToken();
			valVec.add( new Integer( token ));
		}
		System.out.println( "valVec.size()" +  valVec.size() );
		iVec.set( valVec );
	}

	public static void readCRCurveValue( Element e, Param p )
	{
		CRCurveParam cr = (CRCurveParam) p;
		cr.curve.clear();//for undo, we only add points here, in undo might want to remove.
		NodeList pointlist = e.getElementsByTagName( CRPOINT_ELEMENT_NAME );

		for( int i = 0; i < pointlist.getLength(); i++ )
		{
			Element pointE = (Element) pointlist.item( i );
			CurvePoint cp = getCurvePoint( pointE );
			cr.curve.addCurvePointOnLoad( cp );
		}
	}
	public static void readAnimValVecValue( Element e, Param p )
	{
		AnimatedValueVectorParam avvp = (AnimatedValueVectorParam) p;
		NodeList avValsNodes = e.getElementsByTagName( ParamXML.ELEMENT_NAME );
		Vector<AnimatedValue> avs = new Vector<AnimatedValue>();
		for( int i = 0; i < avValsNodes.getLength(); i++ )
		{
			Element avElem = (Element) avValsNodes.item( i );
			AnimatedValue av = new AnimatedValue( ImageOperationXML.currentIop );
			ParamXML.readParamValue( avElem, av );
			avs.add( av );
		}
		avvp.set( avs );
	}
	*/
}
