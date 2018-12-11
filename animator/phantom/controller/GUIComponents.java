package animator.phantom.controller;

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

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import animator.phantom.gui.LayerCompositorFrame;
import animator.phantom.gui.MenuBarCallbackInterface;
import animator.phantom.gui.NodesPanel;
import animator.phantom.gui.keyframe.KFColumnPanel;
import animator.phantom.gui.keyframe.KFToolButtons;
import animator.phantom.gui.keyframe.KeyFrameEditorPanel;
import animator.phantom.gui.modals.FilterStackEdit;
import animator.phantom.gui.modals.render.RenderWindow;
import animator.phantom.gui.preview.PreViewControlPanel;
import animator.phantom.gui.preview.PreViewUpdater;
import animator.phantom.gui.timeline.TCDisplay;
import animator.phantom.gui.timeline.TimeLineControls;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.gui.timeline.TimeLineEditorPanel;
import animator.phantom.gui.timeline.TimeLineIOPColumnPanel2;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.gui.view.component.ViewEditor;
import animator.phantom.gui.view.component.ViewSizeSelector;
import animator.phantom.paramedit.FilterStackPanel;

//--- Class to hold references to GUI components.
public class GUIComponents
{

	public static LayerCompositorFrame animatorFrame;
	public static MenuBarCallbackInterface animatorMenu;
	public static ViewEditor viewEditor;
	public static TimeLineIOPColumnPanel2 timeLineIOPColumnPanel;
	public static TimeLineEditorPanel timeLineEditorPanel;
	public static Vector<TimeLineDisplayPanel> timeLineScaleDisplays = new Vector<TimeLineDisplayPanel>();
	public static TCDisplay tcDisplay;
	public static KFColumnPanel kfColumnPanel;
	public static KeyFrameEditorPanel keyFrameEditPanel;
	public static JPanel kfNamePanel;
	public static KFToolButtons kfControl;
	public static PreViewUpdater previewUpdater;
	public static PreViewControlPanel previewControls;
	public static ViewControlButtons viewControlButtons;
	public static ViewSizeSelector viewSizeSelector;
	public static RenderWindow renderWindow;
	public static FilterStackEdit filterStackEdit;
	public static JPanel keyEditorContainerPanel;
	public static JScrollPane viewScrollPane;
	public static FilterStackPanel filterStackPanel;
	public static TimeLineControls tlineControls;
	public static NodesPanel nodesPanel;
	public static JLabel projectInfoLabel;
	public static JScrollPane filterStackTablePane;
	public static JLabel compNameLabel;
	public static JLabel layerNameLabel;

	public static void reset()
	{
		animatorMenu = null;
		viewEditor = null;
		ParamEditController.paramEditFrame = null;
		timeLineIOPColumnPanel = null;
		timeLineEditorPanel = null;
		timeLineScaleDisplays = new Vector<TimeLineDisplayPanel>();
		tcDisplay = null;
		filterStackEdit = null;
	}
	

	public static void LCReset()
	{
		animatorMenu = null;
		viewEditor = null;
		ParamEditController.paramEditFrame = null;
		timeLineIOPColumnPanel = null;
		timeLineEditorPanel = null;
		timeLineScaleDisplays = new Vector<TimeLineDisplayPanel>();
		tcDisplay = null;
		filterStackEdit = null;
	}

	public static LayerCompositorFrame getAnimatorFrame(){ return animatorFrame; }

	public static ViewEditor getViewEditor(){ return viewEditor; }

 }//end class
