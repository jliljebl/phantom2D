package animator.phantom.gui.view.editlayer;

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
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.SVec;
import animator.phantom.gui.view.ViewRenderUtils;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.renderer.plugin.PolyLinePlugin;

public class PolyLineEditLayer extends ViewEditorLayer
{
	private	PolyLineShape editShape;
	private PolyLinePlugin plm;
	private static final float MAX_ADD_DIST = 200.0f;
	private static final Color NOT_CLOSED_COLOR = new Color( 95, 105, 159 );
	private Point2D.Float mousePrevPoint = null;

	public PolyLineEditLayer( PolyLinePlugin plm )
	{
		super( plm.getIOP()  );
		this.plm = plm;

		editShape = new PolyLineShape( plm.px, plm.py, plm, this );
		registerShape( editShape );
		
		setMode( ViewEditorLayer.KF_EDIT_MODE );
		setName( plm.getName() );
	}

	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> btns = new Vector<Integer>();
		btns.add( new Integer( ViewControlButtons.MOVE_B ));
		btns.add( new Integer( ViewControlButtons.KF_EDIT_B ));
		btns.add( new Integer( ViewControlButtons.KF_ADD_B ));
		btns.add( new Integer( ViewControlButtons.KF_REMOVE_B ));

		buttons.setModeButtons( btns );
		buttons.setSelected( 1 );//index of existing buttons
	}

	//--- Udpate for frame change
	public void frameChanged()
	{
		editShape.movePoints( getCurrentFrame() );
	}
	//--- Udadate for mode change.
	public void modeChanged(){}

	public void mousePressed()
	{
		//--- Get possibly pressed edit point.
		lastPressedPoint = getEditPoint( mouseStartPoint );
		//--- edit, add mode
		if( getMode() == ViewEditorLayer.KF_ADD_MODE ||
			 getMode() == ViewEditorLayer.KF_EDIT_MODE  )
		{
			//--- Close line if open and first pressed
			if( 	lastPressedPoint != null && 
				lastPressedPoint == editShape.getEditPoints().elementAt( 0 ) &&
				plm.closed.get() == false )
			{
				plm.closed.set( true );
			}
			//--- add point if open and no point pressed.
			else if( lastPressedPoint == null && plm.closed.get() == false )
			{
				plm.addPoint( mouseStartPoint );
				lastPressedPoint = new EditPoint( mouseStartPoint.x, mouseStartPoint.y ) ;
				editShape.addEditPoint( lastPressedPoint );
			}
			//--- Try to add point if open and no point pressed.
			else if( lastPressedPoint == null && plm.closed.get() == true && 
					getMode() == ViewEditorLayer.KF_ADD_MODE )
			{
				int segIndex = -1;
				float closest = 10000000000.0f;
				for( int i = 0; i < editShape.getEditPoints().size(); i++ )
				{
					float dist = getPointDistFromSeg( mouseStartPoint, i );
					if( dist >= 0 && dist < MAX_ADD_DIST )
					{
						if( dist < closest )
						{
							closest = dist;
							segIndex = i;
						}
					}
				}
				if( segIndex != -1 )
				{
					plm.insertPoint( mouseStartPoint, segIndex + 1 );
					editShape.getEditPoints().insertElementAt( new EditPoint( mouseStartPoint.x, mouseStartPoint.y ), segIndex + 1 );
				}
			}
		}
		//--- Remove move
		else if( getMode() == ViewEditorLayer.KF_REMOVE_MODE )
		{
			if( lastPressedPoint != null )
			{
				int index = editShape.getIndexOfPoint( lastPressedPoint );
				plm.removePoint( index );
				editShape.getEditPoints().remove( index );
				if( editShape.getEditPoints().size() < 3 )
					plm.closed.set( false );
			}
		}
		else if( getMode() == ViewEditorLayer.MOVE_MODE )
		{
			mousePrevPoint = mouseStartPoint;
		}
		
	}
	public void mouseDragged()
	{
		int frame = getCurrentFrame();
		//--- move point, add mode
		if( getMode() == ViewEditorLayer.KF_EDIT_MODE )
		{
			if( lastPressedPoint != null )
			{
				lastPressedPoint.setPos( mouseCurrentPoint.x, mouseCurrentPoint.y );
			}
		}
		if( getMode() == ViewEditorLayer.MOVE_MODE )
		{
			float dx = mouseCurrentPoint.x - mousePrevPoint.x;
			float dy = mouseCurrentPoint.y - mousePrevPoint.y;
			editShape.translate( dx, dy );
			mousePrevPoint = mouseCurrentPoint;
		}
		editShape.updateValues( frame );
		EditorsController.displayCurrentInViewEditor( true );

	}
	public void mouseReleased()
	{
		int frame = getCurrentFrame();
		if( getMode() == ViewEditorLayer.KF_EDIT_MODE )
		{
			if( lastPressedPoint != null )
			{
				lastPressedPoint.setPos( mouseCurrentPoint.x, mouseCurrentPoint.y );
			}
		}
		if( getMode() == ViewEditorLayer.MOVE_MODE )
		{
			float dx = mouseCurrentPoint.x - mousePrevPoint.x;
			float dy = mouseCurrentPoint.y - mousePrevPoint.y;
			editShape.translate( dx, dy );
			mousePrevPoint = null;
		}
		editShape.updateValues( frame );
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );

		lastPressedPoint = null;
	}

	public float getHitAreaSize(){ return 0; }//this 

	private float getPointDistFromSeg( Point2D.Float p, int segIndex )
	{
		Point2D.Float start = editShape.getEditPoint( segIndex ).getPos();
		Point2D.Float end;
		if( segIndex <  editShape.getEditPoints().size() - 1 ) end = editShape.getEditPoint( segIndex + 1 ).getPos();
		else end = editShape.getEditPoint( 0 ).getPos();

 		SVec seg = new SVec( start, end );
		if( seg.pointInBetween( p ) )
		{
			SVec dist = seg.getDistanceVec( p );
			return (float) Math.abs( dist.getLength() );
		}
		return -1;
	}
	public void paintLayer( Graphics2D g )
	{
		Color linesC = Color.white;
		if( !plm.closed.get() )
			linesC = NOT_CLOSED_COLOR;
		if( !isActive ) 
			linesC = Color.darkGray;
		Vector<EditPoint> panelPoints = 
			getPanelCoordinatesEditPoints( editShape.getEditPoints() );
		ViewRenderUtils.drawPoints( g, panelPoints, linesC );
		ViewRenderUtils.drawPolygon( g, panelPoints, linesC, plm.closed.get() );
	}

}//emd class