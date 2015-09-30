package animator.phantom.controller;

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

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import animator.phantom.xml.PhantomDocUtils;

//--- This class reads plugin info when loading plugins from jar.
public class PluginInfo extends PhantomDocUtils
{
	//--- Always in file named DOC_NAME
	public static final String DOC_NAME = "plugininfo.xml";

	//--- XML
	public static final String ROOT_ELEMENT = "phantomplugin";
	public static final String PLUGIN_ELEMENT = "plugin";
	public static final String AUTHOR_ELEMENT = "author";

	public static final String CLASS_ATTR = "class";
	public static final String GROUP_ATTR = "group";
	public static final String VERSION_ATTR = "version";
	public static final String REQUIRED_API_VERSION_ATTR = "required_api_version";
	public static final String NAME_ATTR = "name";

	public String pluginClass;
	public String group;
	public String version;
	public int apiVersion;
	public String author;

	public PluginInfo(){}

	//--- read and write for xml file
	public void read( InputStream is) throws Exception
	{
		Document doc = loadXMLDoc( is );
		if( doc == null )
			throw new Exception();

		//--- root
		Element root = doc.getDocumentElement();

		//--- plugin
		Element pluginE = getFirstChild( root, PLUGIN_ELEMENT );
		pluginClass = pluginE.getAttribute( CLASS_ATTR );
		group = pluginE.getAttribute( GROUP_ATTR );
		version =  pluginE.getAttribute( VERSION_ATTR );
		apiVersion = getInt( pluginE, REQUIRED_API_VERSION_ATTR );
		Element authorE = getFirstChild( root, AUTHOR_ELEMENT );
		author = authorE.getAttribute( NAME_ATTR );
	}

}//end class
