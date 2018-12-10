package animator.phantom.gui;

import javax.swing.JMenuItem;


public class CompositionMenuItem extends JMenuItem 
{
	private static final long serialVersionUID = 1L;
	
	private int compositionID;

	public CompositionMenuItem( String text, int compID )
	{
		super( text );
		this.compositionID = compID;
		setFont(GUIResources.BOLD_FONT_12);
	}

	public int getCompsitionID(){ return compositionID; }
	
}//end class
