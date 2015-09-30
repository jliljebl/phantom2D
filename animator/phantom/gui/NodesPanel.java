package animator.phantom.gui;

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
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

public class NodesPanel extends JPanel
{
	private static final int AREA_BORDER_GAP = 5;
	
	private Vector<String> groups;
	private int groupIndex = 0;

	private Vector<Vector<ImageOperation>> groupIops;
	
	private NodeSelectPanel groupsTable;
	private NodeSelectPanel nodesTable;
	
	private static final BufferedImage dragImg = GUIResources.getResourceBufferedImage( GUIResources.draggedNode );
		
	public NodesPanel()
	{
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS));

		add( Box.createRigidArea(new Dimension( 0, 6 )));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new TwoItemRowLayout( 120, -1,  6, false));
		
		JScrollPane gsp = initGroupsTable();
		contentPanel.add( gsp );

		JScrollPane nsp = initNodesTable();
		contentPanel.add( nsp );
				
		add( contentPanel );
		
		add( Box.createRigidArea(new Dimension( 0, 6 ) ) );
				
		Border b1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border b2 = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 4, AREA_BORDER_GAP + 4, AREA_BORDER_GAP + 2, AREA_BORDER_GAP), b1 );
		
		setBorder( b2 );

		validate();
		repaint();
	}

	private JScrollPane initGroupsTable()
	{
		groups = IOPLibrary.getGroupKeys();
		groupsTable = new NodeSelectPanel( this, false );
		groupsTable.setColor( GUIColors.grayTitle );
		groupsTable.init( groups, null);
		groupsTable.setSelected( groupIndex );
		JScrollPane sp = new JScrollPane( 	groupsTable,		 
							ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		return sp;
	}

	private JScrollPane initNodesTable()
	{
		groupIops = new Vector<Vector<ImageOperation>>();

		for( String group : groups )
		{
			Vector<ImageOperation> iops = new Vector<ImageOperation>();
			@SuppressWarnings("rawtypes")
			Vector iopsObjs = IOPLibrary.getGroupContents( group );
			Collections.sort( iops );

			for( int j = 0; j < iopsObjs.size(); j++ )
			{
				Object o = iopsObjs.elementAt( j );
				if( o instanceof ImageOperation )
				{
					ImageOperation iop = (ImageOperation) o;
					iops.add( iop );	
				}
				else
				{
					PhantomPlugin p = (PhantomPlugin) o;
					iops.add( p.getIOP() );
				}

			}
			groupIops.add( iops );
		}



		nodesTable = new NodeSelectPanel( this, true );
		displayGroupNodes();
		JScrollPane sp = new JScrollPane( nodesTable,		 
						  ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		return sp;
	}

	private void displayGroupNodes()
	{
		Vector<ImageOperation> iopGroup = groupIops.elementAt( groupIndex );
		Vector<String> nodeNames = new Vector<String>();
		for( Object iopObj : iopGroup )
		{
			ImageOperation iop = (ImageOperation) iopObj;
			nodeNames.add( iop.getName() );
		}

		nodesTable.init( nodeNames, dragImg );
	}
	
	public void selectionChanged( NodeSelectPanel source )
	{
		if ( source == groupsTable )
		{
			groupIndex = groupsTable.getSelectedIndex();
			displayGroupNodes();
		}
	}

	public ImageOperation getSelectedIOP()
	{
		Vector<ImageOperation> iopGroup = groupIops.elementAt( groupIndex );
		int nodeIndex = nodesTable.getSelectedIndex();
		ImageOperation iop = iopGroup.elementAt( nodeIndex );
		return iop;
	}
	
}//end class 