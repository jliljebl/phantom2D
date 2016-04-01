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

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;


//--- Misc. Util methods used somewhere in application
public class AppUtils
{
	private static String TITLE = "//--------------------------------------------------------------------//";

	public static String[] forbiddenFileNameChars = { " ", ":", ",", "\\", "/","*","?","\"","|","<",">" };

	public static void printTitle( String s )
	{
		int len = ( TITLE.length()) / 2 - ( s.length() / 2 ) ;
		System.out.println( TITLE.substring( 0, len ) + " " + s + " " + TITLE.substring( TITLE.length() - len, TITLE.length() )  );
	}	
 
	public static void printOneTab( String s )
	{
		System.out.println("        " + s );
	}

	//--- Returns estimate of memory consumption for BufferedImage
	public static long getImageSizeEstimate( BufferedImage image )
	{ 
		DataBuffer db = image.getRaster().getDataBuffer();
		int bp = (int) Math.ceil( DataBuffer.getDataTypeSize( db.getDataType() ) / 8f );
        	return bp * db.getSize();
	}
	//--- Returns file extension
	public static String getExtension(File f) 
	{
		if(f != null) 
		{
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if(i>0 && i<filename.length()-1) return filename.substring(i+1).toLowerCase();
		}
		return null;
	}

	public static String getFileExtension( String fileName )
	{
		int i = fileName.lastIndexOf('.');
		if(i>0 && i<fileName.length()-1) 
			return fileName.substring(i+1).toLowerCase();
		else 
			return null;
	}
	//--- String with amount of of memory in megabytes.
	public static String getMBString( long bytes )
	{
		float mb = (float) bytes / (1024.0f * 1024.0f);
		String str1 = (new Float( mb )).toString();
		int pointIndex = str1.lastIndexOf('.');
		String str2;
		if( pointIndex <= str1.length() + 1 ) str2 = str1.substring( 0, pointIndex + 2 );
		else str2 = str1;

		return str2 + " MB";
	
	}
	//--- Extensions of image/movie files that can be opened by this app.
	public static String[] getAcceptedImageExtensions()
	{
		String[] extensions = { "jpg", "jpeg", "gif", "png", "bmp", "svg", "mov", "avi", "ogg" };
		return extensions;
	}
	//---
	public static boolean isMovieExtension( String ext )
	{
		String extLower = ext.toLowerCase();
		String[] movieExtensions = { "avi","dv","flv","mkv","mpg","mpeg","m2t","mov","mp4","qt","vob","webm",
									 "3gp","3g2","asf","divx","dirac","f4v","h264","hdmov","hdv","m2p","m2ts",
									 "m2v","m4e","mjpg","mp4v","mts","m21","m2p","m4v","mj2","m1v","mpv","m4v",
									 "mxf","mpegts","mpegtsraw","mpegvideo","nsv","ogv","ogx","ps","ts","tsv","tsa",
									 "vfw","video","wtv","wm","wmv","xvid","y4m","yuv"};
        
		for (String movieExt :  movieExtensions)
			if (movieExt.equals( extLower )) return true;
 		
		return false;
	}

	public static String[] getAcceptedFileExtensions()
	{
		String[] imgExts = getAcceptedImageExtensions();
		
		String[] retArray = new String[ imgExts.length ];

		for ( int i = 0; i < imgExts.length; i++)
		{
			retArray[ i ] = imgExts[ i ];
		}
		return retArray;
	}

	public static String testStringForSequences( String[] seqs, String test )
	{
		for( int i = 0; i < seqs.length; i++ )
			if( test.contains( seqs[ i ] ) ) return seqs[ i ];

		return null;
	}

}//end class