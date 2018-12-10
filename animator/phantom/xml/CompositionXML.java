package animator.phantom.xml;

import java.awt.Dimension;

import org.w3c.dom.Element;

import animator.phantom.project.ProjectNamedFlow;


public class CompositionXML extends AbstractXML
{
	public static final String ELEMENT_NAME = "composition";

	public static ProjectNamedFlow getObject( Element e )
	{
		ProjectNamedFlow comp = new ProjectNamedFlow();
		comp.setName( e.getAttribute( "name" ) );
		int w = getInt( e,"width" );
		int h = getInt( e, "height" );
		comp.setScreenDimensions( new Dimension( w, h ) );
		comp.setLength( getInt( e, "length" ) );
		return comp;
	}

	public static Element getElement( ProjectNamedFlow comp )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( "name", comp.getName() );
		e.setAttribute( "width" , intStr( comp.getScreenDimensions().width  ) );
		e.setAttribute( "height", intStr( comp.getScreenDimensions().height )  );
		e.setAttribute( "length", intStr( comp.getLength() ) );
		return e;
	}

}//end class
