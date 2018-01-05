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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.LayerCompositorMenuActions;
import animator.phantom.controller.UserActions;
//import animator.phantom.controller.KeyActionController;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.renderer.FileSource;

//--- GUI component for selecting a FileSource.
public class MediaListItem extends JPanel implements MouseListener, ActionListener
{
	private FileSource fileSource;
	private boolean isSelected = false;

	private JLabel nameLabel;

	private  JPopupMenu popup;
	private  JMenuItem sendToFlow;
	private  JMenuItem fileSourceInfo;
	private  JMenuItem deleteFileSource;

	private static final int HEIGHT = 22;
	private static final int WIDTH = BigEditorsLayout.MEDIA_PANEL_WIDTH;
	private static final Dimension COMP_SIZE = new Dimension( WIDTH, HEIGHT );
	private static final Dimension NAME_SIZE = new Dimension( WIDTH - 35, HEIGHT );
	private static final Dimension PAD = new Dimension( 5, 0);

	private boolean isFirst = false;
	private boolean isPressed = false;
	private boolean dragOn = false;

	public MediaListItem( FileSource fs, MouseListener l )
	{
		fileSource = fs;

		nameLabel = new JLabel( fs.getName() );
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameLabel.setPreferredSize( NAME_SIZE );

		setLayout( new BoxLayout( this , BoxLayout.X_AXIS) );
		add( Box.createRigidArea( PAD ) );
		add( new JLabel( fileSource.getFileTypeIcon() ) );
		add( Box.createRigidArea( PAD ) );
		add( nameLabel );
		add( Box.createHorizontalGlue() );
		add( Box.createRigidArea( PAD ) );

		// Create the popup menu.
		popup = new JPopupMenu();
		sendToFlow = new JMenuItem("Add To Composition");
		sendToFlow.addActionListener(this);
		popup.add( sendToFlow );
		deleteFileSource = new JMenuItem("Delete");
		deleteFileSource.addActionListener(this);
		popup.add( deleteFileSource );
		
		popup.addSeparator();

		fileSourceInfo = new JMenuItem("Info");
		fileSourceInfo.addActionListener(this);
		popup.add( fileSourceInfo );

		// Component building 
		setBackground( GUIColors.BIN_BG );
		setPreferredSize( COMP_SIZE );
		setMinimumSize( COMP_SIZE );
		addMouseListener( l );
		addMouseListener( this );

		setBackground( GUIColors.MEDIA_ITEM_BG );
		
		validate();
		repaint();
	}

	public void setSelected( boolean isSelected )
	{
		this.isSelected = isSelected;
		if( isSelected) 
			setBackground( GUIColors.MEDIA_ITEM_SELECTED_BG );
		else  
			setBackground( GUIColors.MEDIA_ITEM_BG );
	}

	public void setName( String newName )
	{
		nameLabel.setText( newName );
	}

	public boolean isSelected(){ return isSelected; }	
	public FileSource getFileSource(){ return fileSource; }
	public void setMouseListener( MouseListener l ){ addMouseListener( l ); }
	public void setFirst( boolean value ){ isFirst = value; }

	public void paintComponent( Graphics g )
	{
		if( !fileSource.hasResourceAvailable() )
			nameLabel.setForeground( Color.red );
		else
			nameLabel.setForeground( GUIColors.MEDIA_ITEM_TEXT_COLOR );

		super.paintComponent( g );
		g.setColor( GUIColors.lineBorderColor );
		Dimension d = getSize();
		g.drawLine( 0, 0, 0, d.height - 1 );
		g.drawLine( 0, d.height - 1, d.width - 1, d.height - 1 );
		g.drawLine( d.width - 1, d.height - 1, d.width - 1, 0 );
		if( isFirst ) g.drawLine( 0, 0, d.width - 1, 0 );
	}
	public void mouseClicked(MouseEvent e)
	{
		requestFocusInWindow();
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e)
	{
		if( isPressed )
		{
			Point hotSpot = new Point(0,0);  
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Cursor cursor = toolkit.createCustomCursor( fileSource.getThumbnailIcon().getImage(), hotSpot, "dnd"); 
			GUIComponents.animatorFrame.setCursor(cursor);
			dragOn = true;
		}
	}
	//public void mouseMoved(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		if( e.getButton() == MouseEvent.BUTTON3 )
		{
			maybeShowPopup(e);
			return;
		}
		requestFocusInWindow();
		isPressed = true;
	}
	public void mouseReleased(MouseEvent e)
	{
		
		if( e.getButton() == MouseEvent.BUTTON3 )
		{
			maybeShowPopup(e);
			return;
		}

		isPressed = false;
		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		GUIComponents.animatorFrame.setCursor(normalCursor);

		//Point pt = new Point(GUIComponents.renderFlowPanel.getLocation());
		//SwingUtilities.convertPointToScreen(pt, GUIComponents.renderFlowPanel);

		/*
		// Do drag drop if on top of FlowEditor at release
		if( dragOn )
		{
			if( e.getXOnScreen() > pt.x && e.getXOnScreen() < pt.x + GUIComponents.renderFlowPanel.getWidth()
				&& e.getYOnScreen() > pt.y && e.getYOnScreen() < pt.y + GUIComponents.renderFlowPanel.getHeight() )
			{
				FlowController.addIOPFromFileSourceRightAway( fileSource, new Point(e.getXOnScreen(), e.getYOnScreen()));
			}
		}
		dragOn = false;
		*/
	}

	public void actionPerformed(ActionEvent e)
	{
		
		if( e.getSource() == sendToFlow )
		{
			LayerCompositorMenuActions.addFileSourceLayer( fileSource );
		}
		/*
		if( e.getSource() == sendToFlowFileMerge )
		{

			FlowController.addFileMergeFromFileSource( fileSource );
		}
		*/
		if( e.getSource() == deleteFileSource )
		{
			//KeyActionController.deleteItems( GUIComponents.binsPanel );
			Vector<FileSource> delVec = new Vector<FileSource>();
			delVec.add( fileSource );
			UserActions.deleteFileSources( delVec  );
		}
		
		if( e.getSource() == fileSourceInfo )
		{
			DialogUtils.showFileSourceInfoDialog( fileSource );
		}
	}

	private void maybeShowPopup(MouseEvent e) 
	{
		popup.show( e.getComponent(), e.getX(), e.getY() );
	}

}//end class
