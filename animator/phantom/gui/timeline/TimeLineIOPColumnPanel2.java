package animator.phantom.gui.timeline;

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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;

import animator.phantom.controller.AppData;
import animator.phantom.controller.TimeLineController;
import animator.phantom.project.LayerCompositorLayer;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

//--- Left side panel of time line editor. Holds column of TimeLineIOPBoxes.
public class TimeLineIOPColumnPanel2 extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 1L;
	
	private Vector <TimeLineIOPBox2> iopBoxes = new Vector <TimeLineIOPBox2>();

	//------------------------------------------------- CONSTRUCTOR	
	public TimeLineIOPColumnPanel2()
	{	
		setLayout( new TimeLineColumnColumnLayout() );
		//addMouseListener( this );
	}

	//------------------------------------------------- INTERFACE
	public void initGUI()
	{
		removeAll();
		
		iopBoxes = new Vector <TimeLineIOPBox2>();
		Vector<ImageOperation> clips = TimeLineController.getCurrentClips();
		for( int i = 0; i < clips.size(); i++ )
		{
			// layer boxes
			ImageOperation layerIop = clips.elementAt( i );
			TimeLineIOPBox2 addBox = new TimeLineIOPBox2( layerIop );
			iopBoxes.addElement( addBox );
			add( addBox );
			
			// filter boxes
			Vector<ImageOperation> filterStack = layerIop.getFilterStack();
			for( int k = 0; k < filterStack.size(); k++ )
			{
				System.out.println("in filter stack" );
				ImageOperation filterStackIop = filterStack.elementAt( k );
				TimeLineIOPBox2 filterStackAddBox = new TimeLineIOPBox2( filterStackIop, TimeLineIOPBox2.FILTER_BOX );
				iopBoxes.addElement( filterStackAddBox );
				add( filterStackAddBox );
			}
			
			// masks boxes
			LayerCompositorLayer layer = AppData.getLayerComposition().getLayer( layerIop );
			Vector<RenderNode> preCompNodes = layer.getPreCompLayers();
			for( int j = 0; j < preCompNodes.size(); j++ )
			{
				System.out.println("in preCompNodes" );
				ImageOperation preCompIop = preCompNodes.elementAt( j ).getImageOperation();
				TimeLineIOPBox2 precompAddBox = new TimeLineIOPBox2( preCompIop,  TimeLineIOPBox2.PRECOMP_BOX );
				iopBoxes.addElement( precompAddBox );
				add( precompAddBox );
			}
		}
		
		//add( Box.createVerticalGlue() );
		repaint();
	}

	//------------------------------------------------- MOUSE EVENTS
	public void mousePressed(MouseEvent e)
	{
		/*
		//--- Get click coordinates.
 		int y = e.getY() - GUIComponents.timeLineEditorPanel.getVerticalPos();
		//--- Clik is not on any iopBox if too big y, deslect all and leave
		if( y > ( iopBoxes.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) - 1 )
		{
			TimeLineController.unselectAllClips();
			TimeLineController.clipEditorRepaint();
			UpdateController.editTargetIOPChangedFromClipEditor( null );
			return;
		}
		//--- Get clicked box
		int selectedIndex = y / AnimFrameGUIParams.TE_ROW_HEIGHT;
		TimeLineIOPBox clickedBox = iopBoxes.elementAt( selectedIndex );

		//--- if ctrl is not pressed, set as only selected.
		if( !KeyStatus.ctrlIsPressed() ) 
			TimeLineController.setAsSingleSelectedClip( clickedBox.getIop() );
		//--- if ctrl is pressed, add to selected.
		else
			TimeLineController.addToSelectedClips( clickedBox.getIop() );

		TimeLineController.clipEditorRepaint();
		UpdateController.editTargetIOPChangedFromClipEditor( clickedBox.getIop() );
		*/
	}
	
	//Only clicks are handled
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	//public void mouseMoved(MouseEvent e){}
	//--- Double click opens iop in ParamEditor
	public void mouseClicked(MouseEvent e)
	{
		/*
		int y = e.getY() - GUIComponents.timeLineEditorPanel.getVerticalPos();
		if( y > ( iopBoxes.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) - 1 )
			return;
		//if( e.getClickCount() == 2 )
		//{
			//--- Get clicked box
			int selectedIndex = y / AnimFrameGUIParams.TE_ROW_HEIGHT;
			TimeLineIOPBox clickedBox = iopBoxes.elementAt( selectedIndex );
			ParamEditController.displayEditFrame( clickedBox.getIop() );
			repaint();
		//}
		 * */
		 
	}
	public void mouseReleased(MouseEvent e){}

	//------------------------------------------------- GRAPHICS 
	/*
	public void paintComponent( Graphics g )
	{
		
		//--- Draw bg
		g.setColor( GUIColors.timeLineColumnColor );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		//--- Draw boxes
		TimeLineIOPBox drawBox;
		for( int i = 0; i < iopBoxes.size(); i++ )
		{
			drawBox = iopBoxes.elementAt( i );
			int vertPos = GUIComponents.timeLineEditorPanel.getVerticalPos();
			drawBox.paint( g, 0, i * AnimFrameGUIParams.TE_ROW_HEIGHT + vertPos );
		}
		//--- Draw closing horizontal line.
		g.setColor( GUIColors.lineBorderColor );
		if( iopBoxes.size() > 0 )
			g.drawLine( 0, ( iopBoxes.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) + 1,
					0 + AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH,
					( iopBoxes.size() * AnimFrameGUIParams.TE_ROW_HEIGHT ) + 1 );
	}
	*/

}//end class
