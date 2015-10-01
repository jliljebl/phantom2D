package animator.phantom.controller;

/*
    Copyright Janne Liljeblad

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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import animator.phantom.xml.PhantomDocUtils;
import animator.phantom.xml.XMLPrefDoc;

//--- This class reads and writens presistant data that is needed for editor, not projects.
//--- Examples: default language, recent projects.
public class EditorPersistance extends PhantomDocUtils
{
	//--- elements and attributes
	public static final String ROOT_ELEMENT = "phantompersistence";
	public static final String RECENT_ELEMENT = "recentprojects";
	public static final String PATH_ATTR = "path";

	//--------------------------------------------- PREF KEYS
	//--- Name of prefs xml file
	public static final String DOC_NAME = "phantomeditor.xml";

	//--- persistant state
	public static final String FIRST_RUN = "firstrun";
	//public static final String IMPORT_DIR = "importdir";
	public static final String PLUGIN_DIR = "plugindir";

	//--- Prefs
	public static final String LAYOUT_MID = "layoutmid";
	public static final String FLOW_WIDTH = "flowwidth";
	public static final String FLOW_HEIGHT = "flowheight";
	public static final String KF_DEF_TENS = "deftens";
	public static final String KF_DEF_INTERP = "definterp";

	//--- keyboard short cuts
	public static final String RENDER_PRE_KEY_SC = "render_key_sc";
	public static final String RENDER_PRE_FRAME_KEY_SC = "render_frame_key_sc";
	public static final String DISP_FLOW_KEY_SC = "disp_flow_key_sc";
	public static final String DISP_CLIP_KEY_SC = "disp_clip_key_sc";
	public static final String DISP_KF_KEY_SC = "disp_kf_key_sc";
	public static final String FLOW_ARRANGE_KEY_SC ="flow_arrange_key_sc";
	public static final String FLOW_CONNECT_KEY_SC = "flow_connect_key_sc";
	public static final String FLOW_DISCONNECT_KEY_SC = "flow_disconnect_key_sc";
	public static final String TLINE_ZOOM_IN_KEY_SC = "tline_zoom_in_key_sc";
	public static final String TLINE_ZOOM_OUT_KEY_SC = "tline_zoom_out_key_sc";
	public static final String TLINE_PREV_KEY_SC = "tline_prev_key_sc";
	public static final String TLINE_NEXT_KEY_SC = "tline_next_key_sc";
	public static final String NEXT_LAYER_KEY_SC = "next_layer_key_sc";
	public static final String SWITCH_BIG_EDITOR_KEY_SC = "switch_big_editor_sc";

	//-------------------------------------------- DEFAULT VALUES
	//--- Prefs default values
	private static final boolean FIRST_RUN_DEFAULT = true;
	private static final String PLUGIN_DIR_DEFAULT = "";
	private static final int RECENT_VEC_MAX = 7;
	private static final int FLOW_PANEL_WIDTH_DEFAULT = 1700; 
	private static final int FLOW_PANEL_HEIGHT_DEFAULT = 1700;
	private static final int LAYOUT_MID_DEFAULT = 500;
	private static final float KF_DEF_TENS_DEFAULT = 0.3f;
	private static final int KF_DEF_INTERP_DEFAULT = 1;// 1 == bez, 0 == linear

	//--- keyboard short cuts default values
	private static final String RENDER_PRE_KEY_SC_DEF = "F9";
	private static final String RENDER_PRE_FRAME_KEY_SC_DEF = "F10";
	private static final String DISP_FLOW_KEY_SC_DEFAULT = "F1";
	private static final String DISP_CLIP_KEY_SC_DEFAULT = "F2";
	private static final String DISP_KF_KEY_SC_DEFAULT = "F3";
	private static final String FLOW_ARRANGE_KEY_SC_DEFAULT = "F5";
	private static final String FLOW_CONNECT_KEY_SC_DEFAULT = "F6";
	private static final String FLOW_DISCONNECT_KEY_SC_DEFAULT = "F7";
	private static final String TLINE_ZOOM_IN_KEY_SC_DEFAULT = "control UP";
	private static final String TLINE_ZOOM_OUT_KEY_SC_DEFAULT = "control DOWN";
	private static final String TLINE_PREV_KEY_SC_DEFAULT = "control LEFT";
	private static final String TLINE_NEXT_KEY_SC_DEFAULT = "control RIGHT";
	private static final String NEXT_LAYER_KEY_SC_DEFAULT = "TAB";
	private static final String SWITCH_BIG_EDITOR_KEY_SC_DEFAULT = "F4";

	//--------------------------------------------- RANGE
	public static final int LAYOUT_MID_MAX = 700;
	public static final int LAYOUT_MID_MIN = 300;
	public static final int FLOW_WIDTH_MAX = 2500;
	public static final int FLOW_WIDTH_MIN = 800;
	public static final int FLOW_HEIGHT_MAX = 2500;
	public static final int FLOW_HEIGHT_MIN = 800;

	//------------------------------------------------- DATA
	//--- Recent opened files
	private static Vector<File> recent = new Vector<File>();

	//--- Document holding pref values
	private static XMLPrefDoc prefs;

	//--- mix and max values for some parameters
	private static Hashtable<String, Integer> minVal = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> maxVal = new Hashtable<String, Integer>();

	//------------------------------------------------------- IO
	//--- read prefs
	public static void read( String path, boolean fromJar )
	{
		//--- reset
		recent = new Vector<File>();
		prefs = new XMLPrefDoc( path, ROOT_ELEMENT );

		//--- Set default values before loading, loading will override
		setDefaultValues( prefs );

		//--- Set ranges for some prefs
		setRanges();

		//--- Load prefence values
		prefs.loadPrefs( fromJar );

		//--- If no doc exists, we didn't load pref file and need to create one.
		//--- This have elements for default values
		if( prefs.getDoc() == null )
		{
			//--- Try to create file and capture exception if already there
			String dirPath = path.substring( 0,  path.length() - 18 );// name part hardcoded out
			File dir = new File( dirPath );
			File file = new File( path );
		
			System.out.println("p:" + path );
			try
			{
				dir.mkdir();
				file.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println( e.getMessage() + " when creating persistance file" );
			}

			//--- Write prefs file
			prefs.createPrefsDoc();
			prefs.writeXMLFile();
		}

		//--- get recent 
		prefs.initIter( prefs.getRootElement(), RECENT_ELEMENT );
		while( prefs.iterMore() )
		{
			Element recentE = prefs.iterNext();
			String recentPath = recentE.getAttribute( PATH_ATTR );
			File f = new File( recentPath );
			if( f.exists() )
				recent.add( f );
		}
	}

	public static void write()
	{
		write( null );
	}
	//--- write prefs
	public static void write( String newPath )
	{
		//--- Get document containing values for preferences and root element
		prefs.createPrefsDoc();
		Document doc = prefs.getDoc();
		Element root = prefs.getRootElement();

		//--- Create elements for recent files
		try 
		{
			//--- Recent projects
			for( File f : recent )
			{
				Element recentE = doc.createElement( RECENT_ELEMENT );
				recentE.setAttribute( PATH_ATTR , f.getAbsolutePath() );
				root.appendChild( recentE );
			}
		}
		catch (Exception e) 
		{
			System.out.println(e);
        	}

		if( newPath == null )
			prefs.writeXMLFile();
		else
			prefs.writeXMLFile( newPath );
	}

	//------------------------------------ PRIVATE METHODS
	private static void setDefaultValues( XMLPrefDoc prefdoc )
	{
		prefdoc.putDefaultValue( FIRST_RUN, FIRST_RUN_DEFAULT );
		prefdoc.putDefaultValue( PLUGIN_DIR, PLUGIN_DIR_DEFAULT );
		prefdoc.putDefaultValue( FLOW_WIDTH, FLOW_PANEL_WIDTH_DEFAULT );
		prefdoc.putDefaultValue( FLOW_HEIGHT, FLOW_PANEL_HEIGHT_DEFAULT );
		prefdoc.putDefaultValue( LAYOUT_MID, LAYOUT_MID_DEFAULT );
		prefdoc.putDefaultValue( KF_DEF_TENS, KF_DEF_TENS_DEFAULT );
		prefdoc.putDefaultValue( KF_DEF_INTERP, KF_DEF_INTERP_DEFAULT );
		prefdoc.putDefaultValue( RENDER_PRE_KEY_SC, RENDER_PRE_KEY_SC_DEF );
		prefdoc.putDefaultValue( RENDER_PRE_FRAME_KEY_SC, RENDER_PRE_FRAME_KEY_SC_DEF );
		prefdoc.putDefaultValue( DISP_FLOW_KEY_SC, DISP_FLOW_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( DISP_CLIP_KEY_SC, DISP_CLIP_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( DISP_KF_KEY_SC, DISP_KF_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( FLOW_ARRANGE_KEY_SC, FLOW_ARRANGE_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( FLOW_CONNECT_KEY_SC, FLOW_CONNECT_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( FLOW_DISCONNECT_KEY_SC, FLOW_DISCONNECT_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( TLINE_ZOOM_IN_KEY_SC, TLINE_ZOOM_IN_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( TLINE_ZOOM_OUT_KEY_SC, TLINE_ZOOM_OUT_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( TLINE_PREV_KEY_SC, TLINE_PREV_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( TLINE_NEXT_KEY_SC, TLINE_NEXT_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( NEXT_LAYER_KEY_SC, NEXT_LAYER_KEY_SC_DEFAULT );
		prefdoc.putDefaultValue( SWITCH_BIG_EDITOR_KEY_SC, SWITCH_BIG_EDITOR_KEY_SC_DEFAULT );
	}

	private static void setRanges()
	{
		minVal = new Hashtable<String, Integer>();
		maxVal = new Hashtable<String, Integer>();

		minVal.put( LAYOUT_MID, LAYOUT_MID_MIN );
		maxVal.put( LAYOUT_MID, LAYOUT_MID_MAX );

		minVal.put( FLOW_WIDTH, FLOW_WIDTH_MIN );
		maxVal.put( FLOW_WIDTH, FLOW_WIDTH_MAX );

		minVal.put( FLOW_HEIGHT, FLOW_HEIGHT_MIN );
		maxVal.put( FLOW_HEIGHT, FLOW_HEIGHT_MAX );
	}

	//--------------------------------------------------- INTERFACE
	//--- Pref set and get
	public static void setPref( String key, int value )
	{
		if( minVal.get( key ) != null )
		{
			int min = ((Integer)minVal.get( key )).intValue();
			int max = ((Integer)maxVal.get( key )).intValue();
			if( value < min ) value = min;
			if( value > max ) value = max;
		}
		prefs.putValue( key, value );
	}
	public static void setPref( String key, String value )
	{
		prefs.putValue( key, value );
	}
	public static void setPref( String key, boolean value )
	{
		prefs.putValue( key, value );
	}

	//--- Pref get
	public static int getIntPref( String key )
	{
		return prefs.getInt( key );
	}
	public static String getStringPref( String key )
	{
		return prefs.getString( key );
	}
	public static float getFloatPref( String key )
	{
		return prefs.getFloat( key );
	}
	public static boolean getBooleanPref( String key )
	{
		return prefs.getBoolean( key );
	}
	
	//--- recent files
	public static Vector<File> getRecentProjects(){ return recent; }
	public static void addRecent( File f )
	{
		//--- remove duplicate, if ever are two or more, removes only one
		int removeindex = -1;
		for( int i = 0; i < recent.size(); i++ )
			if( f.getAbsolutePath().equals( recent.elementAt( i ).getAbsolutePath() ) ) removeindex = i;
		
		if( removeindex != -1 ) recent.remove( removeindex );

		recent.add( 0, f );
		if( recent.size() >= RECENT_VEC_MAX ) recent.remove( recent.size() - 1 );
	}

}//end class
