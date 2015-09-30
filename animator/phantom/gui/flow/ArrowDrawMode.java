package animator.phantom.gui.flow;

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

import java.awt.event.MouseEvent;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.FlowController;
import animator.phantom.renderer.RenderNode;
import animator.phantom.undo.ArrowAddUndoEdit;
import animator.phantom.undo.PhantomUndoManager;

public class ArrowDrawMode extends EditMode
{
	//--- Structure used to speed up graphics access.
	private LookUpGrid lookUpGrid;
	//--- Current arrow being hadled
	private FlowConnectionArrow drawArrow = null;
	//--- The connection point that arrow editing started from 
	private FlowBoxConnectionPoint startCP = null;
	//--- The connection point that arrow connected to last
	private FlowBoxConnectionPoint lastTouchedCP = null;

	public ArrowDrawMode( FlowEditPanel editPanel, LookUpGrid lookUpGrid )
	{
		this.editPanel = editPanel;
		this.lookUpGrid = lookUpGrid;
	}

	//--------------------------------------- MOUSE EVENTS
	//--- This is called after mouse press when arrow draw begins:
	//--- - parentBox != null
	//--- - startCp != null
	//--- - startCP.getArrow() == null 
	public void  mousePressed( 	MouseEvent e,
					FlowBox parentBox,
					FlowBoxConnectionPoint startCP  )
	{
		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();
	
		//--- Capture 
		this.startCP = startCP;

		//--- Set cp active.
		startCP.setActive( true );

		//--- Draw into box, now as active connection point.
		startCP.redrawIntoParentBox();

		//--- Create new arrow
		//--- The tail of arrow is connected to box
		if( startCP.getType() == FlowBoxConnectionPoint.OUTPUT )
		{
			drawArrow = new FlowConnectionArrow( parentBox, null );
			int startX = parentBox.getX() + startCP.getX() + 4;
			int startY = parentBox.getY() + startCP.getY() + 4;
			drawArrow.setStartPos( startX, startY );
			drawArrow.setEndPos( mouseX, mouseY );
		}
		//--- The head of arrow is connected to box
		else
		{
			drawArrow = new FlowConnectionArrow( null, parentBox );
			int endX = parentBox.getX() + startCP.getX() + 4;
			int endY = parentBox.getY() + startCP.getY() + 4;
			drawArrow.setStartPos( mouseX, mouseY );
			drawArrow.setEndPos( endX, endY );
		}
		//--- Put reference of created arrow into pressed connection point.
		startCP.setArrow( drawArrow );
		//--- Clear last touched variable used to make connections
		lastTouchedCP = null;
		//---- Add arrow into moving graphics vector.
		//--- FlowGraphics in movingGraphics are erased and drawn 
		//--- after each mouse dragged.
		editPanel.movingGraphics.add( drawArrow );
	}

	public void mouseDragged(MouseEvent e)
	{
		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();

		//--- Determine which end of arrow to move and set its position.
		FlowBox targetBox = drawArrow.getTargetBox();
		if( targetBox == null ) drawArrow.setEndPos( mouseX, mouseY );
		else drawArrow.setStartPos( mouseX, mouseY );
	
		//--- Check if arrow connects. Can only do so if direction is legal.
		FlowBox selectedBox = lookUpGrid.getBox( mouseX, mouseY );

		if( selectedBox != null && drawArrow.isLegal() )
		{
			FlowBoxConnectionPoint cp = selectedBox.getConnectionPoint( mouseX, mouseY );
				
			//--- If connection point is touched and it is of different type,
			//--- activate it.
			if( cp != null && !( cp.getType() == startCP.getType() ) )
			{	
				//--- Set cp active, get parentBox and paint into BG
				cp.setActive( true );
				cp.redrawIntoParentBox();
				editPanel.movingGraphics.add( cp.getParentBox() );
				lastTouchedCP = cp;
			}
		}
			
		//--- Check if touched connection point exited.
		if( lastTouchedCP != null )
		{
			//--- if box exited, so is connection point.
			if( selectedBox == null ) lastTouchedExited();
			else 
			{
				//--- Check if connection point exited inside box.
				FlowBoxConnectionPoint cp = 
					selectedBox.getConnectionPoint( mouseX, mouseY );
				if( cp == null ) lastTouchedExited();
			}
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		//--- Arrow connects two boxes if lastTouchedCP exists.
		if( lastTouchedCP != null )
		{
			//--- Get boxes into whitch arrow is connected to.
			FlowBox startB = startCP.getParentBox();
			FlowBox endB = lastTouchedCP.getParentBox();

			//--- Set arrow into middle of connection point
			//--- change renderflow and put correct flowbox reference into arrow.
			//--- First determine arrow direction.
			if( lastTouchedCP.getType() == FlowBoxConnectionPoint.OUTPUT )
			{
				//--- Last touched is output of source.
				//--- Start (first touched) is input of target
				//--- Set arrow position pixel perfect 
				drawArrow.setStartPos( endB.getX() + lastTouchedCP.getX() + FlowBox.ARROW_CP_OFF_X,
							  endB.getY() + lastTouchedCP.getY() + FlowBox.ARROW_CP_OFF_Y );

				//--- CONNECT NODES IN RENDER FLOW!!!
				RenderNode source = endB.getRenderNode();
				RenderNode target = startB.getRenderNode();
				
				int sourceCPIndex = lastTouchedCP.getIndex();
				int targetCPIndex = startCP.getIndex();

				FlowController.connectNodes( source, target,
								sourceCPIndex, targetCPIndex );
					
				//--- Put missing box reference into arrow.
				drawArrow.setSourceBox( endB );
				//--- Put connection points references into arrow
				drawArrow.setConnectionPoints( lastTouchedCP, startCP );

			}
			else 
			{		
				//--- Last touched input of target.
				//--- Start (first touched) is output of source
				//--- Set arrow position pixel perfect 
				drawArrow.setEndPos( endB.getX() + lastTouchedCP.getX() + FlowBox.ARROW_CP_OFF_X,
							endB.getY() + lastTouchedCP.getY() + FlowBox.ARROW_CP_OFF_Y );
				
				
				//--- CONNECT NODES IN RENDER FLOW!!! 
				RenderNode source = startB.getRenderNode();
				RenderNode target = endB.getRenderNode();

				int sourceCPIndex = startCP.getIndex();
				int targetCPIndex = lastTouchedCP.getIndex();

				FlowController.connectNodes( source, target,
								sourceCPIndex, targetCPIndex );

				//--- Put missing box reference into arrow.
				drawArrow.setTargetBox( endB );
				//--- Put connection points references into arrow
				drawArrow.setConnectionPoints( startCP, lastTouchedCP );
			}

			//--- Put reference of drawn arrow into connection point
			lastTouchedCP.setArrow( drawArrow );

			//--- Erase and deactivate lastTouchedCPs
			lastTouchedCP.setActive( false );
			lastTouchedCP.redrawIntoParentBox();
				
			//--- Save arrow
			editPanel.arrows.add( drawArrow );
			//--- Put arrow into grid
			lookUpGrid.addFlowGraphicToGrid( drawArrow );

			ArrowAddUndoEdit undoEdit = new ArrowAddUndoEdit( drawArrow );
			PhantomUndoManager.addUndoEdit( undoEdit );

			//--- If edit just done created cyclic graph, we need to roll it back
			FlowController.rollBackCyclic();

			EditorsController.displayCurrentInViewEditor( false );//bg img needs re-render
		}
		//--- Arrow didn't connect, delete it and set points arrow references null.
		else			
		{
			if( startCP == null )System.out.println( "starp cp == null" );
			editPanel.movingGraphics.remove( startCP.getArrow() );
			startCP.setArrow( null );
		}

		//--- Erase and deactivate start connection point, paint it into parentbox
		//--- and set parent box to be repainted
		startCP.setActive( false );
		startCP.redrawIntoParentBox();
	}
	
	//--- Set non-active, draw connection point and set parent box to be drawn.
	private void lastTouchedExited()
	{
		lastTouchedCP.setActive( false );
		lastTouchedCP.redrawIntoParentBox();
		lastTouchedCP = null;
	}

}//end class