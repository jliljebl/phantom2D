package animator.phantom.gui;

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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

//import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.KeyStatus;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.keyaction.DeleteAction;
import animator.phantom.controller.keyaction.KeyUtils;
import animator.phantom.project.Bin;
import animator.phantom.renderer.FileSource;

//--- Panel for selecting and manipulating FileSources in a bin.
public class ListFileSourceSelectPanel extends FileSourceSelectPanel implements MouseListener
{
	//--- The bin that has its contents displayed here.
	private Bin bin;
	//--- Panels for FileSources that can be selected.
	private Vector <ListFileSourcePanel> panels;
	//--- Panels for currently selected filesources.
	private Vector <ListFileSourcePanel> selected = new Vector<ListFileSourcePanel>();

	public ListFileSourceSelectPanel(){}

	public void init( Bin bin )
	{
		this.bin = bin;
		addMouseListener( this );
		createPanels();
		initSelectPanel();

		KeyUtils.setFocusAction( this, new DeleteAction(), "DELETE" );
	}

	public void initSelectPanel()
	{
		removeAll();
		JPanel selectPanel = createSelectPanel();
		add( selectPanel );
	}
	
	private JPanel createSelectPanel()
	{
		JPanel p = new JPanel();
		p.setLayout( new BoxLayout( p,  BoxLayout.Y_AXIS ));
		for( ListFileSourcePanel panel : panels )
			p.add( panel );
		p.add(  Box.createVerticalGlue() );

		return p;
	}

	private void createPanels()
	{
		Vector<FileSource> fileSources = bin.getFileSources();
		panels = new Vector<ListFileSourcePanel>();

		for( int i = 0; i < fileSources.size(); i++ )
		{
			FileSource fs = fileSources.elementAt( i );
			if( fs == null )System.out.println("fs == null");
			ListFileSourcePanel addPanel = new ListFileSourcePanel( fs, this );
			panels.add( addPanel );
		}
	}

	public void reInitSelectPanel()
	{
		removeAll();
		JPanel selectPanel = createSelectPanel();
	 	add( selectPanel );
		validate();
		repaint();
	}

	public void addFileSource( FileSource fs )
	{
		ListFileSourcePanel addPanel = new ListFileSourcePanel( fs, this );
		if( panels.size() == 0 ) addPanel.setFirst( true );
		panels.add( addPanel );
	}

	public void addFileSources( Vector<FileSource> addFileSources )
	{
		for( int i = 0; i < addFileSources.size(); i++ )
		{
			FileSource fs = addFileSources.elementAt( i );
			ListFileSourcePanel addPanel = new ListFileSourcePanel( fs, this );
			if( i == 0 ) addPanel.setFirst( true );
			panels.add( addPanel );
		}
		reInitSelectPanel();
	}
	
	public void deleteSelected()
	{
		panels.removeAll( selected );
		selected.clear();
		reInitSelectPanel();
	}

	public FileSource getLastSelectionFileSource()
	{
		if( selected.size() == 0 ) return null;
		else return selected.elementAt( 0 ).getFileSource();
	}

	public void reInitFromBinContents()
	{
		selected = new Vector<ListFileSourcePanel>();
		createPanels();
		initSelectPanel();
	}

	public Vector<FileSource> getSelected()
	{
		Vector<FileSource> retVec = new Vector<FileSource>();
		for( ListFileSourcePanel fsPanel : selected )
			retVec.add( fsPanel.getFileSource() );
		Vector<FileSource> setVec = new Vector<FileSource>(new LinkedHashSet<FileSource>(retVec));//we can has duplicates here, quick fix
		return setVec;
	}

	public Vector<ListFileSourcePanel> getSelectedPanels()
	{
		Vector<ListFileSourcePanel> retVec = new Vector<ListFileSourcePanel>(new LinkedHashSet<ListFileSourcePanel>(selected));//we can has duplicates here, quick fix
		return retVec; 
	}

	public void addPanel( Object panel )
	{
		panels.add( (ListFileSourcePanel) panel );
		((ListFileSourcePanel) panel).setMouseListener( this );
	}

	public void removePanel( Object panel )
	{
		panels.remove( (ListFileSourcePanel) panel );
	}

	public boolean isEmpty(){ return (panels.size() == 0); }

	public void deselectAll()
	{
		for( ListFileSourcePanel fsPanel : selected )
		{
			fsPanel.setSelected( false );
			fsPanel.repaint();
		}
		selected.clear();
		//GUIComponents.binsPanel.setInfoLabelText( "" );
		//GUIComponents.binsPanel.clearThumbIcon();
	}

	public void selectAll()
	{
		selected.clear();

		for( ListFileSourcePanel fsPanel : panels )
		{
			fsPanel.setSelected( true );
			selected.add( fsPanel );
			fsPanel.repaint();
		}
	}

	public void updatePanelName( FileSource panelFileSource )
	{
		for( ListFileSourcePanel panel : panels )
			if( panel.getFileSource() == panelFileSource )
				panel.setName( panelFileSource.getName() );
	}

	//---------------------------------------- MOUSE EVENTS
	public void mouseClicked(MouseEvent e)
	{
		if( !KeyStatus.ctrlIsPressed() ) deselectAll();
		if( e.getSource() == this )
		{
			deselectAll();
			return;
		}
		
		ListFileSourcePanel clickSource = ( ListFileSourcePanel ) e.getSource();
		if( clickSource == null ) return;
		else 
		{
			clickSource.setSelected( true );
			ProjectController.displayFileSourceInfo( clickSource.getFileSource() );
			selected.add( clickSource );
			clickSource.repaint();
		}

		//--- Double click sends to FlowEditor
		/*
		if( e.getClickCount() == 2 )
			GUIComponents.binsPanel.sendLastSelectionFileSourceToFlow();
		*/
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		if( !KeyStatus.ctrlIsPressed() ) deselectAll();
		if( e.getSource() == this )
		{
			deselectAll();
			return;
		}
		
		ListFileSourcePanel clickSource = ( ListFileSourcePanel ) e.getSource();
		if( clickSource == null ) return;
		else 
		{
			clickSource.requestFocusInWindow();
			clickSource.setSelected( true );
			ProjectController.displayFileSourceInfo( clickSource.getFileSource() );
			selected.add( clickSource );
			clickSource.repaint();
		}
	}
	public void mouseReleased(MouseEvent e){}

}//end class
