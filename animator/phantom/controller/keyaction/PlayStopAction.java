package animator.phantom.controller.keyaction;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import animator.phantom.controller.PreviewController;

public class PlayStopAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed( ActionEvent e )
	{
		PreviewController.playPressed();
	}

}//end class
