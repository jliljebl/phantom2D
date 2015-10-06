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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import animator.phantom.controller.Application;
import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.IconLoadThread;
import animator.phantom.controller.UserActions;
import animator.phantom.project.Bin;
import animator.phantom.renderer.FileSource;
//for usable screensize

//--- GUI component for managing media files.
public class BinsAreaPanel extends JPanel implements ActionListener
{
	//--- Global data
	private Vector <Bin> bins;
	private Vector <FileSourceSelectPanel> binPanels;
	private Bin currentBin;
	public Hashtable<FileSource, ImageIcon> thumbIcons;
		
	//--- GUI components
	private JPanel buttonsPanel = new JPanel();
	private JScrollPane scrollPane;
	private JPanel infoPanel = new JPanel();
	private JLabel infoLabel;
	public JLabel iconLabel;

	//--- File source buttons.
	private JButton addImage = new JButton( GUIResources.getIcon( GUIResources.openMediaSmall ) );
	private JButton addSequence = new JButton(  GUIResources.getIcon( GUIResources.openFileSeqSmall ) );
	private JButton delete = new JButton(  GUIResources.getIcon( GUIResources.deleteFileSmall ) );

	//--- GUI parameters
	private static final int SCROLL_WIDTH_INSET = 0;
	private static final int SCROLL_HEIGHT_INSET = 0;
	private static final int BINPANEL_WIDTH = ContentPaneLayout.LEFT_WIDTH;
	private static int BINPANEL_HEIGHT;
	private static final int BINPANEL_HEIGHT_PAD = 121;
 	private static final int INFO_HEIGHT = 45;
	public static int ICON_WIDTH = 60;
	public static int ICON_HEIGHT = 45;

	//------------------------------------------------- CONSTRUCTOR	
	public BinsAreaPanel( Vector<Bin> bins, Component parent)
	{
		this.bins = bins;

		thumbIcons = new Hashtable<FileSource, ImageIcon>();

		BINPANEL_HEIGHT = Application.getUsableScreen().height / 2 - BINPANEL_HEIGHT_PAD;
		createBinSelectPanels();

		//--- Set first bin as current bin. Quaranteed to exist.
		currentBin = bins.elementAt( 0 );

		//--- Prepare buttons.
		GUIResources.prepareMediumButton( addImage, this, "Add File" );
		GUIResources.prepareMediumButton( delete, this, "Delete File" );
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
		clearThumbIcon();
 
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
		buttonsPanel.add( Box.createRigidArea(new Dimension(6, 0)));
		buttonsPanel.add( addImage );
		buttonsPanel.add( addSequence );
		buttonsPanel.add( delete );
		buttonsPanel.add( Box.createRigidArea(new Dimension(10, 0)));
		buttonsPanel.add( Box.createHorizontalGlue() );

		FileSourceSelectPanel sPanel = currentSelectPanel();
		sPanel.requestFocusInWindow();

		//--- Create scrollpane and put selectPanel in it.
		scrollPane = new JScrollPane( 	sPanel,
			 			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		JScrollBar vsb = scrollPane.getVerticalScrollBar();
		vsb.setUI( new PHScrollUI() );
		
		Border b1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border b2 = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 4, 1, 7, 5 ), b1 );
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
		binPanel.setPreferredSize( new Dimension( BINPANEL_WIDTH, BINPANEL_HEIGHT ));
		binPanel.setMaximumSize( new Dimension( BINPANEL_WIDTH, BINPANEL_HEIGHT ));

		//add( holder );
		add( buttonsPanel );
		add( binPanel );

		//--- Validate and display.
		validate();
		repaint();
	}

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
	public Bin currentBin(){ return currentBin; }
	public void setCurrentBin( Bin bin ){ currentBin = bin; }
	public Vector <FileSourceSelectPanel> binPanels(){ return binPanels; }
	public Vector <Bin> bins(){ return bins; }

	public FileSourceSelectPanel currentSelectPanel()
	{
		int index = bins.indexOf( currentBin );
		return binPanels.elementAt( index );
	}

	//--- Returns new initialized instance of current type of FileSourceSelectedPanel.
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

	public void sendLastSelectionFileSourceToFlow()
	{
		FlowController.addToCenterFromFileSource( currentSelectPanel().getLastSelectionFileSource() );
	}
	//---------------------------------------------- BUTTON EVENTS
	public void actionPerformed( ActionEvent e )
	{

		if( e.getSource() == addImage )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addSingleFileSources( GUIComponents.binsPanel );
				}
			}.start();
		}

		if( e.getSource() == addSequence )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addFileSequenceSource( GUIComponents.binsPanel );
				}
			}.start();
		}

		if( e.getSource() == delete )
			UserActions.deleteFileSources( this );

	}

}//end class
