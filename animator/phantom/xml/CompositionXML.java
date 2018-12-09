package animator.phantom.xml;

import org.w3c.dom.Element;

import animator.phantom.project.ProjectNamedFlow;

public class CompositionXML extends AbstractXML
{
	public static final String ELEMENT_NAME = "composition";
	
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
