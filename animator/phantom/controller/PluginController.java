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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PhantomPluginInitError;
import animator.phantom.renderer.IOPLibrary;

public class PluginController
{
	private static Hashtable<PluginInfo, PhantomPlugin> plugins = new Hashtable<PluginInfo, PhantomPlugin>();

	public static void loadPlugins()
	{
		String ppath =  EditorPersistance.getStringPref( EditorPersistance.PLUGIN_DIR );
		if( ppath.length() == 0 )
		{
			System.out.println("Plugin directory not set." );
			return;
		}

		plugins = new Hashtable<PluginInfo, PhantomPlugin>();

		File dir = new File( ppath );
 		if( !dir.exists() || !dir.isDirectory() )
		{
			System.out.println("Plugin directory:" + dir.getAbsolutePath() + " does not exist." );
			return;
		}
		
		JarFilter f = new JarFilter();
		File[] plugIns = dir.listFiles( f );
		for( int i = 0; i < plugIns.length; i++ )
		{
			String path = plugIns[ i ].getAbsolutePath();
			PluginInfo info = readPluginInfo( path );
			if( info == null )
				throw new PhantomPluginInitError( "Failed to load "+ PluginInfo.DOC_NAME  + "  file" ); 

			try
			{
				PhantomPlugin plugin = pluginLoad( info,  plugIns[ i ] );
				IOPLibrary.registerPlugin( plugin, info.group );
				plugins.put( info, plugin );

				System.out.println( "Loaded plugin:" + plugin.getName() ); 
			}
			catch( Exception e )
			{
				throw new PhantomPluginInitError( "Plugin load failed with message:" +  e.getMessage() ); 
			}
		}
	}

	public static PhantomPlugin pluginLoad( PluginInfo info, File jarFile ) throws Exception
	{
		//--- Get jar path
		String jarPath = jarFile.getAbsolutePath();

		Class<?> pluginClass = null;
		try
		{
			//--- Load plugin class
			pluginClass = loadClass( jarPath, info.pluginClass );
			return (PhantomPlugin) pluginClass.newInstance();
		}
		catch( Exception e )
		{
			System.out.println(e.getMessage() );
			System.out.println("Plugin instantation failed for " + pluginClass.getName() );
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> loadClass( String jarPath, String className )  throws Exception
	{
		//--- Create URL
		URI uri = new File(jarPath).toURI();
		URL url = uri.toURL();
		//--- Create class loader
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { url });
		//--- Return class
		return classLoader.loadClass( className );
	}

	public static PluginInfo readPluginInfo( String pathToJar )
	{
		try
		{
			JarFile jarFile = new JarFile( pathToJar );
			JarEntry entry = jarFile.getJarEntry( PluginInfo.DOC_NAME );

			PluginInfo pInfo = new PluginInfo();
			pInfo.read( jarFile.getInputStream(entry) );

			jarFile.close();
			return pInfo;
		}
		catch (Exception e) 
		{
			//--- handle read error
		}
		return null;
	}

	public static int getLoadedPluginsSize()
	{
		return plugins.size();
	}

	public static Hashtable<PluginInfo, PhantomPlugin> getPluginsTable(){ return plugins; }
	
}//end class
