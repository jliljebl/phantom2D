package animator.phantom.gui.timeline;

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;

//--- This class displays static sized timeline scale without frame values.
public class NormalizedTimelineDisplay extends JPanel implements MouseListener, MouseMotionListener
{
	public static final int BAR_WIDTH = 300;
	public static final int BAR_HEIGHT = 19; 
	private static final int LINE_HEIGHT = 5;
	private static final Color LINE_COLOR = new Color( (int)(0.3 * 255.0), (int)(0.3 * 255.0), (int)(0.3 * 255.0));
	private static final int LINE_COUNT = 11;
	private static final Color POINTER_COLOR = new Color( 181, 63, 63 );
	private static final Color PREVIEW_AREA_COLOR = new Color( 65,160,60 );
	private static final int END_PAD = 6;

	private int pixPos = END_PAD;
	
	public NormalizedTimelineDisplay()
	{
		setPreferredSize( new Dimension( BAR_WIDTH,  BAR_HEIGHT ) );
		setMaximumSize(  new Dimension( BAR_WIDTH,  BAR_HEIGHT ) );
		//--- MouseListener, MouseMotionListener
		addMouseListener( this );
		addMouseMotionListener( this );
	}

	//--- Handle mouse press on panel area
	public void mousePressed( MouseEvent e )
	{
			int frame = getFrame( e.getX() );
			TimeLineController.setCurrentFrame( frame );
			UpdateController.updateMovementDisplayers();
	}

	//--- Handle mouse dragging
	public void mouseDragged( MouseEvent e )
	{
			int frame = getFrame( e.getX() );
			TimeLineController.setCurrentFrame( frame );
			UpdateController.updateMovementDisplayers();

	}
	//--- Notify clip and set clipBeingEdited to null
	public void mouseReleased( MouseEvent e )
	{
			int frame = getFrame( e.getX() );
			TimeLineController.setCurrentFrame( frame );
			UpdateController.updateCurrentFrameDisplayers( false );
	}

	//--- Noop Mouse events.
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}

	public static int getFrame( int x )
	{
		x = legalizeX( x );
		x = x - END_PAD;
		int active_width = BAR_WIDTH - 2 * END_PAD;
		float normalizedPos = (float)x / (float)active_width;
		
		return (int) ((float) ProjectController.getLength() * normalizedPos );
	}

	public static int legalizeX( int x )
	{
		if( x < END_PAD )
			return END_PAD;
		else if (x > BAR_WIDTH - END_PAD)
			return BAR_WIDTH - END_PAD;
		else
			return x;
	}

	public static int getX( int frame )
	{
		float active_width = BAR_WIDTH - 2 * END_PAD;
		float normalizedFrame = (float) frame / (float) ProjectController.getLength();
		return END_PAD + ((int) ( normalizedFrame * active_width ) );
	}

	public void paintComponent( Graphics g )
	{
		g.setColor( Color.white );
		g.fillRect(0, 0, BAR_WIDTH + 1, BAR_HEIGHT + 1 );
                
		// Get area between end pads
		int active_width = BAR_WIDTH - 2 * END_PAD;

		// Draw lines
		g.setColor( LINE_COLOR );
		int x_step = active_width / LINE_COUNT;   
		for( int i = 0; i < LINE_COUNT + 1; i++ )
			g.drawLine( i * x_step + END_PAD, 0,  i * x_step + END_PAD, LINE_HEIGHT );

		for( int i = 0; i < LINE_COUNT + 1; i++ )
			g.drawLine( i * x_step + END_PAD, BAR_HEIGHT,  i * x_step + END_PAD, BAR_HEIGHT - LINE_HEIGHT );

		//--- Draw preview area
		if( PreviewController.getStartFrame() != -1 )
		{
			int inX = Math.round( getX( PreviewController.getStartFrame() ));
			int outX = Math.round( getX( PreviewController.getEndFrame() ));
			g.setColor( PREVIEW_AREA_COLOR );
			g.fillRect( inX, 5, outX - inX, 5 );
		}
		
		// frame pointer
		pixPos = getX( TimeLineController.getCurrentFrame() );
		g.setColor( POINTER_COLOR );
		int framePointerX = pixPos;
		g.fillRect(  framePointerX - 1, 0, 5, BAR_HEIGHT - 1);
		g.setColor( Color.white );
		g.drawLine( framePointerX + 1 , 0, framePointerX + 1 , BAR_HEIGHT - 1);
	}

}//end class