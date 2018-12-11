package animator.phantom.gui.timeline;

import java.awt.Color;

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

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import animator.phantom.controller.AppData;
import animator.phantom.controller.KeyStatus;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

public class TimeLineIOPBox2 extends JPanel implements MouseListener, ItemListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	
	public static final int LAYER_BOX = 0;
	public static final int PRECOMP_BOX = 1;
	public static final int FILTER_BOX = 3;

	private ImageOperation iop;
	private JLabel idLabel;
	private JLabel nameLabel;
	private JCheckBox activeCheckBox;
	private int type;
	
	private JPopupMenu popupMenu;
	private JMenuItem moveUp;
	private JMenuItem moveDown;
	private JMenuItem delete;
	private JMenuItem fileSourceInfo;
	
	public TimeLineIOPBox2( ImageOperation iop )
	{
		initBox(  iop, LAYER_BOX);
	}
	
	public TimeLineIOPBox2( ImageOperation iop, int boxtype )
	{
		initBox(  iop, boxtype );
	}
	
	private void initBox(  ImageOperation iop, int boxtype )
	{
		this.iop = iop;
		this.type = boxtype;
		RenderNode node = AppData.getCurrentFlow().getNode( iop );
		String idStr = null;
		if ( boxtype == LAYER_BOX )
		{
			idStr = "#" + Integer.toString( node.getID() );
		}
		else if( boxtype == PRECOMP_BOX  )
		{
			idStr = "  ";
			
		}
		else
		{
			idStr = "  ";
		}
		
		this.idLabel = new JLabel(  idStr );
		this.idLabel.setForeground(new Color(140, 140, 140));
		this.nameLabel = new JLabel( iop.getName() );
		this.activeCheckBox = new JCheckBox();
		this.activeCheckBox.setSelected( iop. isOn() );
		this.activeCheckBox.addItemListener(this);
		this.activeCheckBox.setOpaque( false );
		IntegerComboBox blendSelect = null;
		if ( boxtype == LAYER_BOX )
		{
			blendSelect = new IntegerComboBox(  iop.blendMode,
												"",
												ImageOperation.blendModes );//so that blenders can be changed without recompiling
			blendSelect.getComboBox().setOpaque( false );
			blendSelect.setTransparent();
		}
		
		setLayout(new TimeLineColumnRowLayout());
	

		add( this.idLabel );
		add( this.nameLabel );
		add( this.activeCheckBox );
		
		if ( blendSelect != null )
		{
			add( blendSelect );
			blendSelect.addMouseListener( this );
		}
		else
		{
			add( new JLabel() );
		}

		this.idLabel.addMouseListener( this );
		this.nameLabel.addMouseListener( this );
		this.activeCheckBox.addMouseListener( this );	
	}
	
	public ImageOperation getIop(){ return iop; }

	
	//------------------------------------------------- MOUSE EVENTS
	public void mousePressed(MouseEvent e)
	{
		if( e.getButton() == MouseEvent.BUTTON3 )
		{
			showPopup(e);
			return;
		}

		//--- if ctrl is not pressed, set as only selected.
		if( !KeyStatus.ctrlIsPressed() ) 
			TimeLineController.setAsSingleSelectedClip( this.iop );
		//--- if ctrl is pressed, add to selected.
		else
			TimeLineController.addToSelectedClips( this.iop );

		TimeLineController.clipEditorRepaint();
		UpdateController.editTargetIOPChangedFromClipEditor( this.iop  );
		ParamEditController.displayEditFrame( this.iop );
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

	public void itemStateChanged(ItemEvent e)
 	{
		Object source = e.getItemSelectable();
		int stateChange = e.getStateChange();
		if( source == this.activeCheckBox )
		{
			if( stateChange == ItemEvent.SELECTED) iop.setOnOffState( true );
			else iop.setOnOffState( false );
			UpdateController.valueChangeUpdate();
		}
	}

	public void paintComponent( Graphics g )
	{

		if( this.type != LAYER_BOX )
		{
			g.setColor( Color.red );
			g.fillRect( 0,0, AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH, AnimFrameGUIParams.TE_ROW_HEIGHT );
		}
		
		if( AppData.getParamEditFrame().getIOP() == this.iop )
		{
			g.setColor( GUIColors.MEDIA_ITEM_SELECTED_BG );
			g.fillRect( 0,0, AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH, AnimFrameGUIParams.TE_ROW_HEIGHT );
		}

	}


	//----------------------------------------- popup menu items
	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == moveUp ) TimeLineController.moveSelectedClipsUp();
		if( e.getSource() == moveDown ) TimeLineController.moveSelectedClipsDown();
		if( e.getSource() == fileSourceInfo ) { DialogUtils.showFileSourceInfoDialog( iop.getFileSource() ); }
	}
	
	private void showPopup( MouseEvent e ) 
	{
		popupMenu = new JPopupMenu();
		
		moveUp = new JMenuItem("Move Up");
		moveUp.addActionListener( this );
		popupMenu.add( moveUp );

		moveDown = new JMenuItem("Move Down");
		moveDown.addActionListener( this );
		popupMenu.add( moveDown );

		popupMenu.addSeparator();
		
		delete = new JMenuItem("Delete");
		delete.addActionListener( this );
		popupMenu.add( delete );

		popupMenu.addSeparator();

		fileSourceInfo = new JMenuItem("File Source Info");
		fileSourceInfo.addActionListener( this );
		if ( iop.getFileSource() == null )
		{
			fileSourceInfo.setEnabled(false);
			fileSourceInfo.setForeground(Color.red);
		}
		popupMenu.add( fileSourceInfo );
		
		popupMenu.show( e.getComponent(), e.getX(), e.getY() );
	}
	
}//end class