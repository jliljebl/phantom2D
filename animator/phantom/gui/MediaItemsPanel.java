package animator.phantom.gui;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


import animator.phantom.controller.KeyStatus;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.UserActions;
import animator.phantom.renderer.FileSource;

public class MediaItemsPanel extends JPanel implements ActionListener, MouseListener
{
		private static final long serialVersionUID = 1L;

		private Vector <MediaListItem> selected = new Vector<MediaListItem>();
		
		//--- GUI components
		private JPanel mediaPanel;
		private JPanel buttonsPanel = new JPanel();
		private JScrollPane scrollPane;
		private JPanel infoPanel = new JPanel();
		private JLabel infoLabel;
		public JLabel iconLabel;

		//--- File source buttons.
		private JButton addImage = new JButton( GUIResources.getIcon( GUIResources.openMediaSmall ) );
		private JButton addVideo = new JButton( GUIResources.getIcon( GUIResources.openVideoSmall ) );
		private JButton addSequence = new JButton(  GUIResources.getIcon( GUIResources.openFileSeqSmall ) );
		private JButton delete = new JButton(  GUIResources.getIcon( GUIResources.deleteFileSmall ) );

		//--- GUI parameters
		private static final int SCROLL_WIDTH_INSET = 0;
		private static final int SCROLL_HEIGHT_INSET = 0;
		private static final int BINPANEL_WIDTH = BigEditorsLayout.MEDIA_PANEL_WIDTH;
		private static int BINPANEL_HEIGHT = 300;
	 	private static final int INFO_HEIGHT = 45;
		public static int ICON_WIDTH = 60;
		public static int ICON_HEIGHT = 45;

		//------------------------------------------------- CONSTRUCTOR	
		public MediaItemsPanel()
		{
			//--- Prepare buttons.
			GUIResources.prepareMediumButton( addImage, this, "Add Graphic" );
			GUIResources.prepareMediumButton( addVideo, this, "Add Video" );
			GUIResources.prepareMediumButton( delete, this, "Delete Media Item" );
			GUIResources.prepareMediumButton( addSequence, this, "Add File Sequence" );

			setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
			updateGUI();
		}
		
		public void updateGUI()
		{
			removeAll();
			
			iconLabel = new JLabel();
			iconLabel.setBorder( BorderFactory.createLineBorder( GUIColors.THUMB_BORDER ) );
			iconLabel.setPreferredSize( new Dimension( ICON_WIDTH, ICON_WIDTH ));
			iconLabel.setMinimumSize( new Dimension( ICON_WIDTH, ICON_WIDTH ) );
			 
			infoLabel = new JLabel();
			infoLabel.setFont( new Font( "SansSerif", Font.PLAIN, 10 ) );
			infoLabel.setVerticalAlignment( SwingConstants.TOP );
	 
			infoPanel.removeAll();
			infoPanel.setPreferredSize( new Dimension( BINPANEL_WIDTH, INFO_HEIGHT ));
			infoPanel.setLayout( new TwoItemRowLayout( ICON_WIDTH + 10, -1, 5, false ));
			infoPanel.add( iconLabel );
			infoPanel.add( infoLabel );
	
			JPanel holder = new JPanel();
			holder.setLayout( new BoxLayout( holder , BoxLayout.X_AXIS) );
			Border ipb = BorderFactory.createEmptyBorder( 2, 6, 6, 8 );
			holder.setBorder( ipb );
			holder.add( infoPanel );
	
			buttonsPanel.removeAll();
			buttonsPanel.setLayout( new BoxLayout( buttonsPanel , BoxLayout.X_AXIS) );
			buttonsPanel.add( addImage );
			buttonsPanel.add( addVideo );		
			buttonsPanel.add( addSequence );
			buttonsPanel.add( delete );
			buttonsPanel.add( Box.createRigidArea(new Dimension(10, 0)));
			buttonsPanel.add( Box.createHorizontalGlue() );
	
			mediaPanel = createMediaListPanel();
			
			//--- Create scrollpane and put selectPanel in it.
			scrollPane = new JScrollPane( mediaPanel,
				 			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
			JScrollBar vsb = scrollPane.getVerticalScrollBar();
			vsb.setUI( new PHScrollUI() );
			
			Border b1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
			Border b2 = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 0, 0, 7, 8 ), b1 );
			scrollPane.setBorder( b2 );
			
			JPanel scrollPanePanel = new JPanel();
			scrollPanePanel.setLayout( new BoxLayout( scrollPanePanel, BoxLayout.X_AXIS) );
			scrollPanePanel.add( scrollPane );
			scrollPanePanel.setPreferredSize( new Dimension( BINPANEL_WIDTH - SCROLL_WIDTH_INSET , BINPANEL_HEIGHT - SCROLL_HEIGHT_INSET));
			scrollPanePanel.setMaximumSize( new Dimension( BINPANEL_WIDTH - SCROLL_WIDTH_INSET, BINPANEL_HEIGHT - SCROLL_HEIGHT_INSET));
			
			JPanel binPanel = new JPanel();
			binPanel.setLayout( new BoxLayout( binPanel, BoxLayout.Y_AXIS) );
			binPanel.add( Box.createRigidArea(new Dimension(0, 3)));
			binPanel.add( scrollPanePanel );
			BINPANEL_HEIGHT = BigEditorsLayout.getLastMediaPanelHeight();
			binPanel.setPreferredSize( new Dimension( BINPANEL_WIDTH, BINPANEL_HEIGHT ));
			binPanel.setMaximumSize( new Dimension( BINPANEL_WIDTH, BINPANEL_HEIGHT ));
			
			//add( holder );
			add( buttonsPanel );
			add( binPanel );
			
			//--- Validate and display.
			validate();
			repaint();
		}

		public JPanel createMediaListPanel()
		{
			JPanel panel = new JPanel();
			panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS) );
			
			Vector<FileSource> fileSources = ProjectController.getFileSources();
			for( FileSource f : fileSources )
			{
				MediaListItem listItem = new MediaListItem( f, this );
				panel.add(listItem);
			}
			panel.add(  Box.createVerticalGlue() );
			
			return panel;
		}

		
		@Override
		public void actionPerformed( ActionEvent e ) 
		{
			//--------------------------------------------- Media menu
			if( e.getSource() == addImage )
			{
				new Thread()
				{
					public void run()
					{
						UserActions.addSingleFileSources(FileSource.IMAGE_FILE, -1, -1);
					}
				}.start();
			}
			if( e.getSource() == addSequence )
			{
				new Thread()
				{
					public void run()
					{
						UserActions.addFileSequenceSource();
					}
				}.start();
			}
			if( e.getSource() == addVideo )
			{
				new Thread()
				{
					public void run()
					{
						UserActions.addSingleFileSources(FileSource.VIDEO_FILE, -1, -1);
					}
				}.start();
			}
			
		}

		public void mouseClicked(MouseEvent e){	}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseMoved(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		
		public void mousePressed(MouseEvent e)
		{
			if( !KeyStatus.ctrlIsPressed() ) 
			{
				deselectAll();
			}
			
			if( e.getSource() == this )
			{
				deselectAll();
				return;
			}
			
			MediaListItem clickSource = ( MediaListItem ) e.getSource();
			if( clickSource == null ) return;
			else 
			{
				clickSource.requestFocusInWindow();
				clickSource.setSelected( true );
				//ProjectController.displayFileSourceInfo( clickSource.getFileSource() );
				selected.add( clickSource );
				clickSource.repaint();
			}
		}
	
		
		public void deselectAll()
		{
			for( MediaListItem i : selected )
			{
				i.setSelected( false );
			}
			selected.clear();

			repaint();
		}
		
		/*
		//--- Create select panels for all bins.
		private void createBinSelectPanels()
		{
			binPanels = new Vector<FileSourceSelectPanel>();
			for( int i = 0; i < bins.size(); i++ )
			{
				Bin bin = bins.elementAt( i );
				binPanels.add( getNewSelectPanelForBin( bin ) );
			}
		}

		//------------------------------------------------- INTERFACE



		//--- Returns new initialized instance of current type of FileSourceSelectedPanel.
		/* 
		public FileSourceSelectPanel getNewSelectPanelForBin( Bin bin )
		{
			FileSourceSelectPanel retPanel = new ListFileSourceSelectPanel();
			retPanel.init( bin );
			return  retPanel;
		}

		public void recreateFromBinContents()
		{
			for( FileSourceSelectPanel panel : binPanels )
				panel.reInitFromBinContents();
			updateGUI();
		}

		public void setInfoLabelText( String text )
		{
			infoLabel.setText( text );
			repaint();
		}

		public void updateThumbIcon( FileSource fileSource )
		{
			ImageIcon ii = fileSource.getThumbnailIcon();
			thumbIcons.put( fileSource, ii );
		}

		public void setThumbIcon( FileSource fs )
		{
			ImageIcon ti = (ImageIcon) thumbIcons.get( fs );
			if( ti == null )
			{
				IconLoadThread iLoad = new IconLoadThread( fs, this );
				iLoad.start();
				return;
			}
			iconLabel.setIcon( ti );
		}

		public void clearThumbIcon()
		{
		 
			iconLabel.setIcon( GUIResources.getIcon( GUIResources.emptyIcon )  );
		}

		public void destroyIcons( Vector<FileSource> fsVec )
		{
			for( FileSource fs : fsVec )
				thumbIcons.remove( fs );
		}

		public void addIcons( Vector<FileSource> fsVec )
		{
			for( FileSource fs : fsVec )
			{
				ImageIcon ii = fs.getThumbnailIcon();
				thumbIcons.put( fs, ii );
			}
		}

		public void deselectAll()
		{
			currentSelectPanel().deselectAll();
			currentSelectPanel().requestFocusInWindow();
		}
	*
	*/
		
}//end class
