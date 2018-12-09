package animator.phantom.project;

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

import java.awt.Dimension;
import java.io.File;
import java.util.Vector;

import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;


public class Project
{

	private String name;	
	private File saveFile; 	//--- File that project was last saved.
	private Vector<FileSource> fileSources = new Vector<FileSource>();
	private Vector<ProjectNamedFlow> compositions = new Vector<ProjectNamedFlow>();
	private ProjectNamedFlow currentComposition;
	private int nextCompositionId;
	//--- The main datastructure of the application that contains the rendering structure as
	//--- specified by the user.
	//private RenderFlow renderFlow = new RenderFlow();
	private Dimension screenSize;
	private int movieDefaultLength = 125;
	private int lengthInFrames = movieDefaultLength;
	private int framesPerSecond = 25;
	private Vector<Bin> bins; //--- Not used currently, but keep if later re-activated in some way
	private Bin currentBin;
	private Vector<ImageOperation> loadClips = new Vector<ImageOperation>();
	private String formatName;

	

	public static final String PROJECT_FILE_EXTENSION = "phr"; //--- Project xml files are have with extension .phr


	//--- This constructor is called when projects are loaded.
	public Project(){}

	//--- This constructor called when projects created.
	public Project( String name, MovieFormat movieFormat )
	{
		//--- Capture data.
		this.name = name;
		
		framesPerSecond = movieFormat.getFPS();
		screenSize = movieFormat.getScreenSize();
		formatName = movieFormat.getName();
		
		bins = new Vector<Bin>();
		addBin( new Bin( "bin1" ) );

		nextCompositionId = 0;
		
		currentComposition = addNewDefaultSizeComposition("Comp 1");
		
		System.out.println("PROJECT \"" + name + "\" CREATED IN FORMAT: "  + formatName );
	}

	//-------------------------------------------- PROJECT DATA INTERFACE
	//--- name.
	public void setName( String name ){ this.name = name; }
	public String getName(){ return name; }
	//--- Compositions
	public ProjectNamedFlow addNewDefaultSizeComposition( String name )
	{
		return addNewComposition( name, movieDefaultLength, screenSize );
	}

	public ProjectNamedFlow addNewComposition( String name, int length, Dimension newScreenSize )
	{
		ProjectNamedFlow comp = new ProjectNamedFlow( nextCompositionId, name, length, newScreenSize );
		compositions.add( comp );
		nextCompositionId += 1;

		return comp;
	}
	public ProjectNamedFlow getCurrentComposition(){ return currentComposition; }
	public void setCurrentComposition( int compositionId )
	{ 
		for( ProjectNamedFlow comp : compositions )
		{
			if ( comp.getID() == compositionId );
			{
				currentComposition = comp;
				return;
			}
		}
		// Should unreachable, print protest.
		System.out.println("setCurrentComposition(..) end reached, something went wrong!!");
	}
	public Vector<ProjectNamedFlow> getCompositions() 
	{
		return compositions;
	}
	//--- flow
	public RenderFlow getCurrentRenderFlow(){ return currentComposition.getFlow(); }
	//public void setRenderFlow( RenderFlow flow ){ renderFlow = flow; }
	//--- Movie Screen size, defensive copying.
	public Dimension getDefaultScreenDimensions(){ return new Dimension( screenSize.width, screenSize.height); }
	public Dimension getCurrentScreenDimensions(){ return new Dimension( currentComposition.getSreeenDimensions().width, currentComposition.getSreeenDimensions().height); }
	public void setScreenDimensions( Dimension d ){ screenSize = d; }
	
	public int getFramesPerSecond(){ return framesPerSecond; }
	public void setFramesPerSecond( int fps){ framesPerSecond = fps; }

	public void setLength( int lengthInFrames ){ this.lengthInFrames = lengthInFrames;}
	public int getLength(){ return lengthInFrames; }
	
	public int getDefaultLength() { return movieDefaultLength; }
	
	//--- Last project save file.
	public File getSaveFile(){ return saveFile; }
	public void setSaveFile( File newSaveFile ){ saveFile = newSaveFile; }

	public String getFormatName(){ return formatName; }
	public void setFormatName( String fname ){ formatName = fname; }

	//----------------------------------------------- BINS
	public Vector<Bin> getBins(){ return bins; }
	public void setBins( Vector<Bin> newBins ){ bins = newBins; }
	public void addBin( Bin bin )
	{
		bins.add( bin );
	}
	//--- Delete bin and set new current bin.
	//--- 1 bin must remain.
	public void deleteBin( Bin bin )
	{
		int binIndex = bins.indexOf( bin );

		if( bins.size() > 1 ) bins.remove( bin );
		else return;

		if( currentBin == bin ) 
			if( binIndex > 0 ) currentBin = bins.elementAt( binIndex - 1 );
			else currentBin = bins.elementAt( 0 );
	}

	//--- CLIPS
	public void setLoadClips( Vector<ImageOperation> clips ){ loadClips = clips; }
	public Vector<ImageOperation>  getLoadClips( ){ return loadClips; }

	//--- FILE SOURCES
	//--- All file sources are given a continually increasing id.
	public void addFileSourcesToProject( Vector<FileSource> addSources )
	{
		for( FileSource fs : addSources )
		{
			int nextFileSourceId = getNextFileSourceID();
			fs.setID( nextFileSourceId );
			fileSources.add( fs );
		}
	}
	public void deleteFileSourcesFromProject( Vector<FileSource> deleteSources )
	{
		fileSources.removeAll( deleteSources );
	}
	public Vector<FileSource> getFileSources(){ return fileSources; }
	public void setFileSources( Vector<FileSource> addFileSources )
	{
		fileSources = addFileSources;
	}
	private int getNextFileSourceID()
	{
		int nextFileSourceId = 0;
		for( FileSource fs : fileSources )
		{
			if( fs.getID() >= nextFileSourceId )
				nextFileSourceId =  fs.getID() + 1;
		}
		return nextFileSourceId;
	}
	public FileSource getFileSource( int id )
	{
		for( FileSource fs : fileSources )
			if( fs.getID() == id ) return fs;

		return null;
	}

	//------------------------------------------------ PARENTING
	public void setParents()
	{
		Vector<RenderNode> nodes = currentComposition.getFlow().getNodes();
		for( RenderNode node : nodes )
		{
			ImageOperation iop = node.getImageOperation();
			iop.loadParentIOP( currentComposition.getFlow() );
		}
	}


	

}//end class
