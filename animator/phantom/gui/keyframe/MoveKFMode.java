package animator.phantom.gui.keyframe;

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
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.UpdateController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimationKeyFrame;

//--- Edit mode for moving keyframes in keyframe editor.
public class MoveKFMode extends KFEditMode
{
	private KeyFrameEditorPanel kfPanel;
	private int startX;
	private int lastFrameDelta;
	private int startFrame;
	private ImageOperation iop;

	public MoveKFMode(){ System.out.println("MoveKFMode constructor"); }

	//--- Called when edit in this mode is started by mouse button press.
	public void mousePressed( MouseEvent e, KeyFrameEditorPanel kfPanel, AnimationKeyFrame kf, int iopBeginFrame, ImageOperation iop )
	{
		this.kfPanel = kfPanel;
		this.iop = iop;
		startFrame = kf.getFrame() + iopBeginFrame;
		startX = e.getX();
		lastFrameDelta = 0;//--- Last frame delta changes always when frame for kf changes.
		kfPanel.setFocusFrame( startFrame );
	}

	//--- Called when edit in this mode is ongoing and mouse is dragged.
	public void mouseDragged( MouseEvent e )
	{
		float val = kfPanel.getValueForY( e.getY() );
		int editDelta = e.getX() - startX;
		int frameDelta = Math.round( editDelta / kfPanel.getPixPerFrame() );
		if( startFrame + frameDelta < 0 
			|| startFrame + frameDelta >  ProjectController.getLength() - 1 ) frameDelta = lastFrameDelta;
		if(  startFrame + frameDelta < iop.getClipStartFrame() ) frameDelta = iop.getClipStartFrame() - startFrame;
		if(  startFrame + frameDelta > iop.getClipEndFrame() ) frameDelta = iop.getClipEndFrame() - startFrame;

		if( frameDelta == lastFrameDelta )
		{
			kfPanel.getEditValue().setValue( startFrame + lastFrameDelta, val );
		}
		else//move kf to new frame
		{
			//--- This block can be made >30? times faster, but for now, never mind
			AnimationKeyFrame lastKF = kfPanel.getEditValue().getKeyFrame( startFrame + lastFrameDelta );
			kfPanel.getEditValue().removeKeyFrame( startFrame + lastFrameDelta );
			lastFrameDelta = frameDelta;
			kfPanel.getEditValue().setValue( startFrame + lastFrameDelta, val );
			kfPanel.getEditValue().copyParams( startFrame + lastFrameDelta, lastKF );
		}
		kfPanel.setFocusFrame( startFrame + lastFrameDelta );
		EditorsController.setCurrentKeyFrame(  kfPanel.getEditValue().getKeyFrame( startFrame + frameDelta ) );
	}

	//--- Called when edit in this mode is ongoing and mouse is button is released.
	public void mouseReleased( MouseEvent e )
	{
		float val = kfPanel.getValueForY( e.getY() );

		int editDelta = e.getX() - startX;
		int frameDelta = Math.round( editDelta / kfPanel.getPixPerFrame() );
		if( startFrame + frameDelta < 0 
			|| startFrame + frameDelta >  ProjectController.getLength() - 1 ) frameDelta = lastFrameDelta;
		if(  startFrame + frameDelta < iop.getClipStartFrame() ) frameDelta = startFrame - iop.getClipStartFrame();
		if(  startFrame + frameDelta > iop.getClipEndFrame() ) frameDelta = iop.getClipEndFrame() - startFrame;

		if( frameDelta == lastFrameDelta )
		{
			kfPanel.getEditValue().setValue( startFrame + lastFrameDelta, val );
		}
		else//move kf to new frame
		{
			//--- This block can be made >30? times faster, but for now, never mind
			AnimationKeyFrame lastKF = kfPanel.getEditValue().getKeyFrame( startFrame + lastFrameDelta );
			kfPanel.getEditValue().removeKeyFrame( startFrame + lastFrameDelta );
			kfPanel.getEditValue().setValue( startFrame + frameDelta, val );
			kfPanel.getEditValue().copyParams( startFrame + frameDelta, lastKF );
			EditorsController.setCurrentKeyFrame(  kfPanel.getEditValue().getKeyFrame( startFrame + frameDelta ) );
		}
		UpdateController.valueChangeUpdate( UpdateController.KF_EDIT, false );
	}

}//end class