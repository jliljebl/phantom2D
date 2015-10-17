package animator.phantom.paramedit;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import animator.phantom.controller.FilterStackController;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.PHButtonFactory;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

public class FilterStackPanel extends JPanel implements ActionListener
{
	private ImageOperation iop;
	private JTable stackTable;
	private JTable pluginTable ;

	private JButton addButton;
	private JButton exitButton;
	private JButton addFilter = new JButton( GUIResources.getIcon(  GUIResources.addClip ) );
	private JButton deleteFilter = new JButton( GUIResources.getIcon(  GUIResources.deleteClip ) );
	private JButton filterDown = new JButton( GUIResources.getIcon(  GUIResources.clipDown ) );
	private JButton filterUp = new JButton( GUIResources.getIcon( GUIResources.clipUp ) );
	private JButton editTargetButton;

	private static final int ROW_HEIGHT = 20;
	private static final int MID_GAP = 30;
	private static final int BUTTON_TABLE_GAP = 4;
	private static final int TABLES_WIDTH = 266;
	private static final int PLUGIN_TABLE_HEIGHT = 200;
	private static final int STACK_TABLE_HEIGHT = 140;
	private static final int NAME_PANEL_PAD = 8;
	private static final int NAME_PANEL_GAP = 12;
	private static final int SUB_TITLE_GAP = 2;

	private static Vector<ImageOperation> filters;

	public FilterStackPanel( ImageOperation iop ) 
	{
		this.iop = iop;

		filters = IOPLibrary.getFilters();
		Collections.sort( filters );

		GUIResources.prepareMediumButton( addFilter, this, "Add Filter" );
		GUIResources.prepareMediumButton( deleteFilter, this, "Delete Selected Filter" );
		GUIResources.prepareMediumButton( filterDown, this, "Move Selected Filter Down" );
		GUIResources.prepareMediumButton( filterUp, this, "Move Selected Filter Up" );

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout( namePanel, BoxLayout.X_AXIS));
		namePanel.add( Box.createRigidArea( new Dimension( NAME_PANEL_PAD, 0 ) ) );
		namePanel.add( iop.getNamePanel() );
		
		//addButton =  PHButtonFactory.getButton("Add To Stack"  );
		//addButton.addActionListener( this );

		//JPanel filterButtons = new JPanel();
		//filterButtons.setLayout(new BoxLayout( filterButtons, BoxLayout.X_AXIS));
		//filterButtons.add( addButton );
		//filterButtons.add( Box.createHorizontalGlue() );

		pluginTable = new JTable( getFilterTableModel() );
		pluginTable.setPreferredScrollableViewportSize(new Dimension( TABLES_WIDTH, PLUGIN_TABLE_HEIGHT));
		pluginTable.setFillsViewportHeight( true );
		pluginTable.setColumnSelectionAllowed( false );
		pluginTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		pluginTable.setShowGrid( false );
		pluginTable.setRowHeight( ROW_HEIGHT );
		pluginTable.setFont( GUIResources.BOLD_FONT_12 );

		JScrollPane pluginScrollPane = new JScrollPane( pluginTable );

		editTargetButton = PHButtonFactory.getButton( "Edit" );
		editTargetButton.addActionListener( this );

		JPanel stackButtons = new JPanel();
		stackButtons.setLayout(new BoxLayout( stackButtons, BoxLayout.X_AXIS));
		stackButtons.add( addFilter );
		stackButtons.add( deleteFilter );
		stackButtons.add( filterDown  );
		stackButtons.add( filterUp );
		stackButtons.add( Box.createHorizontalGlue() );
		stackButtons.add( editTargetButton );

		stackTable = new JTable(  new CustomTableModel( new Vector<Vector<String>>(), "" ) );
		stackTable.setPreferredScrollableViewportSize(new Dimension( TABLES_WIDTH, STACK_TABLE_HEIGHT));
		stackTable.setFillsViewportHeight( true );
		stackTable.setColumnSelectionAllowed( false );
		stackTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		stackTable.setShowGrid( false );
		stackTable.setRowHeight(ROW_HEIGHT );
		stackTable.setFont( GUIResources.BOLD_FONT_12 );

		initFilterStack( 0 );

		JScrollPane stackScrollPane = new JScrollPane( stackTable );

		//exitButton = PHButtonFactory.getButton( "Exit" );
		//exitButton.addActionListener( this );

		//JPanel exitButtonPanel = new JPanel();
		//exitButtonPanel.setLayout(new BoxLayout( exitButtonPanel, BoxLayout.X_AXIS));
		//exitButtonPanel.add( Box.createHorizontalGlue() );
		//exitButtonPanel.add( exitButton );

		//JPanel top = new JPanel();
		//top.setLayout(new BoxLayout( top, BoxLayout.Y_AXIS));
		//top.add( Box.createRigidArea( new Dimension( 0, SUB_TITLE_GAP ) ) );
		//top.add( filterButtons );
		//top.add( Box.createRigidArea( new Dimension( 0, BUTTON_TABLE_GAP ) ) );
		//top.add( pluginScrollPane );

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout( bottom, BoxLayout.Y_AXIS));
		bottom.add( Box.createRigidArea( new Dimension( 0, SUB_TITLE_GAP ) ) );
		bottom.add( stackButtons );
		bottom.add( Box.createRigidArea( new Dimension( 0, BUTTON_TABLE_GAP ) ) );
		bottom.add( stackScrollPane );

		//p.add( namePanel );
		//p.add( Box.createRigidArea( new Dimension( 0, NAME_PANEL_GAP ) ) );
		//p.add( top );
		//p.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
		p.add( bottom );
		p.add( Box.createRigidArea( new Dimension( 0, MID_GAP ) ) );
		//p.add( exitButtonPanel ); 


		add( p );

		EmptyBorder b1 = new EmptyBorder( new Insets( 0,0,0,0 )); 
		TitledBorder b2 = (TitledBorder) BorderFactory.createTitledBorder( 	b1,
								"Filter Stack",
								TitledBorder.CENTER,
								TitledBorder.TOP );
		b2.setTitleColor( GUIColors.grayTitle );
		Border b3 = BorderFactory.createCompoundBorder( b2, BorderFactory.createEmptyBorder( 0, 0, 0, 0));
		setBorder( b3 );
	}

	public ImageOperation getIop(){ return iop; }

	private CustomTableModel getFilterTableModel()
	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		for( ImageOperation filter : filters )
			data.add( getRowVec( filter.getName() ) );

		return new CustomTableModel( data, "Plugin name" );
	}

	public void initFilterStack()
	{
		initFilterStack( stackTable.getSelectedRow() );
	}

	public void initFilterStack( int selIndex )
	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		for( int i = 0; i < iop.getFilterStack().size(); i++ )
		{

			ImageOperation filter = iop.getFilterStack().elementAt( i );
			String target = "";
			if( filter == FilterStackController.getEditTarget() )
				target =  " < E >";
;
			data.add( getRowVec( filter.getName() + target ) );
		}
		Vector<String> colNames = new Vector<String>();
		colNames.add( "Filter Stack" );
	
		CustomTableModel stackModel = (CustomTableModel) stackTable.getModel();
		stackModel.setDataVector( data, colNames );

		if( selIndex < stackTable.getRowCount() && selIndex > -1 )
			stackTable.setRowSelectionInterval( selIndex, selIndex );

		repaint();
	}

	private Vector<String> getRowVec( String str )
	{
		Vector<String> vec = new Vector<String>();
		vec.add( str );
		//vec.add( new ImageIcon(PHButtonFactory.EXIT_IMG ) );
		return vec;
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == addButton )
			FilterStackController.addFilter( iop, filters.elementAt( pluginTable.getSelectedRow() ) );

		if( e.getSource() == deleteFilter )
			FilterStackController.removeFilter( iop, stackTable.getSelectedRow() );

		if( e.getSource() == filterUp )
			FilterStackController.moveFilterUp( iop, stackTable.getSelectedRow()  );

		if( e.getSource() == filterDown )
			FilterStackController.moveFilterDown( iop, stackTable.getSelectedRow()  );

		if( e.getSource() == editTargetButton )
			FilterStackController.setStackIOPEditTarget( iop, stackTable.getSelectedRow() );

		if( e.getSource() == exitButton )
			FilterStackController.closeEditor();
	}

	class CustomTableModel extends DefaultTableModel
	{
		public CustomTableModel( Vector<Vector<String>> data, String columnName )
		{
			super( data.size(), 1);
			Vector<String> columnIdentifiers = new Vector<String>();
			columnIdentifiers.add( columnName );
			setDataVector( data, columnIdentifiers);
		}

		public boolean isCellEditable(int row, int column){ return false; } 
	}
}