package animator.phantom.paramedit;

import java.util.Vector;

import javax.swing.JPanel;

import animator.phantom.controller.FlowController;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.gui.modals.MultiInputDialogPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.parent.AbstractParentMover;

public class AnimationParentPanel extends JPanel
{
	private ImageOperation iop;

	public AnimationParentPanel( ImageOperation iop )
	{
		this.iop = iop;

		Vector <ImageOperation> parentIops = FlowController.getAnimatebleIops();
		parentIops.remove( iop );
		int pselindex = 0;
		int typeselindex = 0;

		if( iop.parentNodeID != -1 ) 
		{
			ImageOperation piop = ( FlowController.getNode( iop.parentNodeID )).getImageOperation();
			pselindex = parentIops.indexOf( piop ) + 1;
			typeselindex = iop.parentMoverType;
		}

		String[] options = new String[parentIops.size() + 1];
		options[ 0 ] = "none";
		for( int i = 1; i < parentIops.size() + 1; i++ )
			options[ i ] = parentIops.elementAt( i - 1 ).getName();

		MComboBox parents = new MComboBox( "Animation parent", 75, options );
		MComboBox actions = new MComboBox( "Follow", 75, AbstractParentMover.types );
		parents.setSelectedIndex( pselindex );
		actions.setSelectedIndex( typeselindex );
	
		String[] loopOptions = { "no looping","loop","ping-pong" };
		MComboBox looping = new MComboBox( "Looping mode", 75, loopOptions );
		looping.setSelectedIndex( iop.getLooping() );
		
		MInputArea area = new MInputArea( "Animation Settings" );
		area.add( parents );
		area.add( actions );
		area.add( looping );
 
		MInputPanel panel = new MInputPanel( "Animation Properties" );
		panel.add( area );

		MultiInputDialogPanel multi = new MultiInputDialogPanel( panel );
		
		add( multi );
	}
		/*
		DialogUtils.showMultiInput( panel );

		int p = parents.getSelectedIndex();
		int ac = actions.getSelectedIndex();
		if( p == 0 ) iop.setParentMover( -1, -1, null );
		else 
		{
			ImageOperation parentIOP = parentIops.elementAt( p - 1 );
			if( isCyclicParenting( iop, parentIOP ) )
			{
				String[] tLines = {"Node/parent relationships can't form cycles.", "Parent set edit cancelled." };
				DialogUtils.showTwoStyleInfo( "Cyclic parenting detected", tLines, DialogUtils.WARNING_MESSAGE );
			}
			else
			{
				RenderNode node = FlowController.getNode( parentIOP );
				iop.setParentMover( ac, node.getID(), parentIOP  );
			}
		}

		iop.setLooping( looping.getSelectedIndex() );
		ParamEditController.reBuildEditFrame();
		UpdateController.valueChangeUpdate();
		*/
		
}//end class
