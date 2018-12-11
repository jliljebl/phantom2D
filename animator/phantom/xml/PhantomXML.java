package animator.phantom.xml;

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

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import animator.phantom.controller.ProjectController;
import animator.phantom.project.Bin;
import animator.phantom.project.Project;
import animator.phantom.project.ProjectNamedFlow;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;
import animator.phantom.undo.PhantomUndoManager;

//--- This class reads and writes project data.
public class PhantomXML extends PhantomDocUtils
{
	public static final String ROOT_ELEMENT_NAME = "phantom2D";
	public static final String FILE_SOURCE_LIB_ELEM = "filesourcelib";
	public static final String BINS_ELEM = "bins";
	public static final String COMPOSITIONS_ELEM = "compositions";
	public static final String FLOW_ELEM = "flow";
	public static final String TIMELINECLIPS_ELEM = "timelineclips";
	public static final String TIMELINE_MARK_ELEM = "timelinemark";

	private static Vector<Integer> loadMarks = new Vector<Integer>();

	public static Project loadProject( Document doc )
	{
		//--- Root
		Element root = doc.getDocumentElement();

		//--- Project
		Element projE = getFirstChild( root, ProjectXML.ELEMENT_NAME  );
		Project project = ProjectXML.getObject( projE );

		//--- FileSources
		Element fslibE = getFirstChild( projE, FILE_SOURCE_LIB_ELEM );
		Vector<FileSource> fileSources = new Vector<FileSource>();
		initIter( fslibE, FileSourceXML.ELEMENT_NAME );
		while( iterMore() )
		{
			Element fsource = iterNext();
			FileSource fs = FileSourceXML.getObject( fsource );
			fileSources.add( fs );
		}
		project.setFileSources( fileSources );

		//--- Bins
		Element binsE = getFirstChild( projE, BINS_ELEM );
		Vector<Bin> bins = new Vector<Bin>();
		initIter( binsE, BinXML.ELEMENT_NAME );
		while( iterMore() )
		{
			Element binE = iterNext();
			Bin bin = BinXML.getObject( binE, project );
			bins.add( bin );
		}
		project.setBins( bins );

		//--- Compositions
		Element compositionsE = getFirstChild( projE, COMPOSITIONS_ELEM );
		Vector<ProjectNamedFlow> comps = new Vector<ProjectNamedFlow>();

		NodeList nodeIterList = compositionsE.getElementsByTagName(  CompositionXML.ELEMENT_NAME );
		int iterIndex = 0;
		while( iterIndex < nodeIterList.getLength() )
		{
			Element compE = (Element) nodeIterList.item( iterIndex );
			iterIndex++;
			ProjectNamedFlow comp = CompositionXML.getObject( compE );

			//--- Flow
			Element flowE = getFirstChild( compE, FLOW_ELEM );
			RenderFlow flow = new RenderFlow();
			flow.setNextNodeID( AbstractXML.getInt( flowE, "nextid" ) );

			//--- RenderNodes
			Vector<RenderNode> rnodes =  new Vector<RenderNode>();
			initIter( flowE, RenderNodeXML.ELEMENT_NAME);
			while( iterMore() )
			{
				Element rnodeE = iterNext();
				Element iopE = getFirstChild( rnodeE, ImageOperationXML.ELEMENT_NAME );

				RenderNode rnode = RenderNodeXML.getObject( rnodeE );
				ImageOperation iop = ImageOperationXML.getObject( iopE, project, false );
				if (iop == null) System.out.println("iop null");
				PhantomUndoManager.newIOPCreated( iop );
				rnode.setImageOperation( iop );
				rnodes.add( rnode );
			}
			flow.setNodes( rnodes );

			//--- Build connections
			for( RenderNode node : rnodes )
				node.buildConnections( rnodes );
			
			comp.setFlow( flow );

			//--- Timeline clips
			Element tlcE = getFirstChild( compE, TIMELINECLIPS_ELEM );
			Vector<ImageOperation> clips = new Vector<ImageOperation>();
			if ( tlcE != null ) // this get dropped by lib if no clips at save time, some optimization maybe.
			{
				initIter( tlcE, TimeLineClipXML.ELEMENT_NAME );
				while( iterMore() )
				{
					Element clip = iterNext();
					ImageOperation iop = TimeLineClipXML.getObject( clip, project, flow );
					clips.add( iop );
				}
				comp.setTimelineClips( clips );
			}
			
			comps.add( comp );
		}
		project.setCompositions( comps );

		//--- Timeline marks
		/*
		loadMarks = new Vector<Integer>();
		initIter( root, TIMELINE_MARK_ELEM);
		while( iterMore() )
		{
			Element markE = iterNext();
			addMark( AbstractXML.getInt( markE, "frame" ) );
		}
		*/
		
		//--- Render out values
		RenderOutXML.setRenderOutValues( projE );

		project.setParents();
		project.makeCurrentCompositionAvailable();

		return project;
	}

	public static Document buildXMLDoc( Project projObj )
	{
		Document doc = null;

	  	try
		{
			//--- Create Document
			doc = createDocument();

			//--- Set doc available for element creators.
			AbstractXML.setDoc( doc );

			//--- Root element.
			Element root = doc.createElement(ROOT_ELEMENT_NAME);
			doc.appendChild(root);

			//--- Project
			Element project = ProjectXML.getElement( projObj );
			root.appendChild( project );

			//--- FileSources
			Element fileSourceLib = doc.createElement( FILE_SOURCE_LIB_ELEM );
			project.appendChild( fileSourceLib );
			Vector<FileSource> fsources = projObj.getFileSources();
			for( FileSource fs : fsources )
				fileSourceLib.appendChild( FileSourceXML.getElement( fs ) );

			//--- Bins
			Element bins = doc.createElement( BINS_ELEM );
			Vector<Bin> binObjs = projObj.getBins();
			for( Bin bin : binObjs )
				bins.appendChild( BinXML.getElement( bin ) );
			project.appendChild( bins );

			//--- ProjectNamdFlow vec a.k.k compositions
			Element compositions =  doc.createElement( COMPOSITIONS_ELEM );
			project.appendChild( compositions );
			Vector<ProjectNamedFlow> projComps = projObj.getCompositions();
			for( ProjectNamedFlow comp : projComps )
			{
				Element compElem = CompositionXML.getElement( comp );
				compositions.appendChild(compElem);
				
				//--- Flow
				Element flow = doc.createElement( FLOW_ELEM );
				flow.setAttribute( "nextid", AbstractXML.intStr(comp.getFlow().getNextNodeID() ) );

				//--- Nodes
				Vector<RenderNode> nodes = comp.getFlow().getNodes();
				for( RenderNode nodeObj : nodes )
				{
					Element node = RenderNodeXML.getElement( nodeObj );
					Element iop = ImageOperationXML.getElement( nodeObj.getImageOperation() );
					node.appendChild( iop );
					flow.appendChild( node );
				}
				compElem.appendChild( flow );

				//--- Timeline Clips
				Element clipsE = doc.createElement( TIMELINECLIPS_ELEM );
				Vector<ImageOperation> clips = comp.getTimelineClips();
				for( ImageOperation iop : clips )
					clipsE.appendChild( TimeLineClipXML.getElement( ProjectController.getFlow().getNode( iop ) ));
				compElem.appendChild( clipsE );
			}
			
			//--- Timeline marks
			/*
			Vector<Integer> marks = TimeLineController.getMarks();
			for( Integer mark : marks )
			{
				Element markE = doc.createElement( TIMELINE_MARK_ELEM );
				markE.setAttribute( "frame", AbstractXML.intStr( mark ) );
				root.appendChild( markE );
			}
			*/
			//--- Render out values
			Element rOutE = RenderOutXML.getElement();
			project.appendChild( rOutE );

		}
		catch (Exception e)
		{
			System.out.println("in catch");
			System.out.println(e);
        }

		return doc;
	}

	/*
	public static Vector <Integer> getMarks()
	{
		Vector <Integer> retMarks = loadMarks;
		loadMarks = new Vector <Integer>();
		return retMarks;
	}
	*/
	public static void addMark( int mark ){ loadMarks.add( new Integer( mark ) ); }

}//end class
