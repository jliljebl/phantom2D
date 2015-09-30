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

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import animator.phantom.controller.Application;


public class GUIResources
{
	//--- PATHS,
	public static String resourcePath = Application.getResourcePath();
	//public static String nativePath = "native/";
	private static Object o = new Object();

	//--- FONTS
	public static String FREE_SANS_PATH = resourcePath + "font/FreeSans.ttf";
	//public static String TC_FONT_PATH = resourcePath + "font/DejaVuSansMono.ttf";
	public static final Font BASIC_FONT_12 = new Font( "SansSerif", Font.PLAIN, 12 );
	public static final Font BASIC_FONT_11 = new Font( "SansSerif", Font.PLAIN, 11 );
	//public static final Font BASIC_FONT_10 = new Font( "SansSerif", Font.PLAIN, 10 );
	public static final Font BOLD_FONT_11 = new Font( "SansSerif", Font.BOLD, 11 );
	public static final Font BOLD_FONT_12 = new Font( "SansSerif", Font.BOLD, 12 );
	public static final Font BOLD_FONT_14 = new Font( "SansSerif", Font.BOLD, 14 );
	//public static final Font BOLD_FONT_13 = new Font( "SansSerif", Font.BOLD, 13 );
	//public static final Font BOLD_SERIF_FONT_14 = new Font( "Serif", Font.BOLD, 14 );
	public static final Font TC_FONT = new Font( "SansSerif", Font.BOLD, 15 );
	public static Font PARAM_EDIT_LABEL_FONT = BOLD_FONT_11;
	//public static Font TABBED_PANE_FONT = BASIC_FONT_12;
	public static Font BIG_BUTTONS_FONT = BASIC_FONT_12;
	public static Font TOP_LEVEL_COMBO_FONT = BASIC_FONT_12;
	public static Font EDITOR_COLUMN_ITEM_FONT = new Font( "SansSerif", Font.PLAIN, 11 );

	//--- BUTTON IMAGES
	//public static String far = resourcePath + "far.png";
	//public static String near = resourcePath + "near.png";

	//public static String binIcon = resourcePath + "binIcon.png";
	//public static String ioWriter = resourcePath + "ioWriter.png";
	//public static String ioReader = resourcePath + "ioReader.png";

	//--- Bin area icons
	public static String bmicon = resourcePath + "bmicon.png";
	//public static String svgicon = resourcePath + "svgicon.png";
	public static String bmseriesicon = resourcePath + "bmseriesicon.png";
	public static String movieicon = resourcePath + "movieicon.png";
	//public static String loading = resourcePath + "loading.png";

	//--- Cut and paste buttons
	//public static String cut = resourcePath + "cut.png";
	//public static String paste = resourcePath + "paste.png";

	//--- Project buttons.
	//public static String addFile = resourcePath + "addFile.png";
	public static String openMediaSmall = resourcePath + "openMediaSmall.png";
	public static String openFileSeqSmall = resourcePath + "openFileSeqSmall.png";
	public static String deleteFileSmall = resourcePath + "deleteFileSmall.png";
	//public static String deleteFile = resourcePath + "deleteFile.png";
	//public static String addFileSequence = resourcePath + "addFileSequence.png";
	//public static String addClip = resourcePath + "addClip30.png";
	//public static String addBin = resourcePath + "addBin.png";
	//public static String deleteBin = resourcePath + "deleteBin.png";
	//public static String renameBin = resourcePath + "reBin.png";
	//public static String deleteSelected = resourcePath + "deleteSelected30.png";

	//--- Select area buttons.
	//public static String toFlow = resourcePath + "toFlowWide.png";
	//public static String toFlowWide = resourcePath + "toFlowWide.png";
	//public static String toFlowSmall = resourcePath + "toFlowSmall.png";
	//public static String cancel = resourcePath + "cancel.png";

	//--- RenderFlowViewButtons 
	public static String deleteBoxes = resourcePath + "deleteBoxes.png";
// 	public static String toTimeline = resourcePath + "toTimeline.png";
	public static String lineUpBoxes = resourcePath + "lineUpBoxes.png";
	public static String connectBoxes = resourcePath + "connectBoxes.png";
	public static String disConnectBoxes = resourcePath + "disConnectBoxes.png";
	//public static String colorQButton = resourcePath + "colorQButton.png";
	//public static String gradientQButton = resourcePath + "gradientQButton.png";
//	public static String gaussianQButton = resourcePath + "gaussianQButton.png";
// 	public static String mergeQButton = resourcePath + "mergeQButton.png";
	public static String viewTargetInFlow = resourcePath + "viewTargetInFlow.png";
	public static String editTargetInFlow = resourcePath + "editTargetInFlow.png";
	public static String editTargetInFlowDark = resourcePath + "editTargetInFlowDark.png";
	//public static String renameInFlow = resourcePath + "renameInFlow.png";
	public static String showGrid = resourcePath + "showGrid.png";

	//--- Buttons for IOP groups 
// 	public static String colorGroup = resourcePath + "colorGroup.png";
// 	public static String colorGroupPressed = resourcePath + "colorGroupPressed.png";
// 	public static String mergeGroup = resourcePath + "mergeGroup.png";
// 	public static String mergeGroupPressed = resourcePath + "mergeGroupPressed.png";
 	public static String transformGroup = resourcePath + "transformGroup.png";
// 	public static String transformGroupPressed = resourcePath + "transformGroupPressed.png";
// 	public static String filterGroup = resourcePath + "filterGroup.png";
// 	public static String filterGroupPressed = resourcePath + "filterGroupPressed.png";
// 	public static String filterArtGroup = resourcePath + "filterArtGroup.png";
// 	public static String filterArtGroupPressed = resourcePath + "filterArtGroupPressed.png";
// 	public static String IOGroup = resourcePath + "IOGroup.png";
// 	public static String IOGroupPressed = resourcePath + "IOGroupPressed.png";
// 	public static String sourceGroup = resourcePath + "sourceGroup.png";
// 	public static String sourceGroupPressed = resourcePath + "sourceGroupPressed.png";
// 	public static String distortGroup = resourcePath + "distortGroup.png";
// 	public static String distortGroupPressed = resourcePath + "distortGroupPressed.png";
// 	public static String toolGroup = resourcePath + "toolGroup.png";
// 	public static String toolGroupPressed = resourcePath + "toolGroupPressed.png";

	//--- Key frame editor.
	public static String lTriActive = resourcePath + "lTriActive.png";
	public static String lTriActiveDark = resourcePath + "lTriActiveDark.png";
	public static String lTriActiveTheme = lTriActive;
	public static String lTriNotActive = resourcePath + "lTriNotActive.png";
	public static String rTriActive = resourcePath + "rTriActive.png";
	public static String rTriActiveDark = resourcePath + "rTriActiveDark.png";
	public static String rTriActiveTheme = rTriActive;
	public static String rTriNotActive = resourcePath + "rTriNotActive.png";
	public static String kfOn = resourcePath + "kfOn.png";
	public static String kfOff = resourcePath + "kfOff.png";
	public static String kfOffDark = resourcePath + "kfOffDark.png";
	public static String kfOffTheme = kfOff;

	//--- Labels for selectable IOPs
// 	public static String colorFilter = resourcePath + "colorFilter.png";
// 	public static String filter = resourcePath + "filter.png";
// 	public static String sourceOperation = resourcePath + "sourceOperation.png";
// 	public static String mergeOperation = resourcePath + "mergeOperation.png";
// 	public static String keyIop = resourcePath + "keyIop.png";
// 	public static String transformer = resourcePath + "transformer.png";
// 	public static String coordTransformer = resourcePath + "coordTransformer.png";
// 	public static String multiply = resourcePath + "multiplyIOP.png";
// 	public static String maskOperation = resourcePath + "maskOperation.png";

	//--- Button labels for timeline controls
 	//public static String inMarker = resourcePath + "inMarker.png";
// 	public static String inMarkerPressed = resourcePath + "inMarkerPressed.png";
// 	public static String outMarker = resourcePath + "outMarker.png";
// 	public static String outMarkerPressed = resourcePath + "outMarkerPressed.png";
// 	public static String clearInOut = resourcePath + "clearInOut.png";
// 	public static String clearInOutPressed = resourcePath + "clearInOutPressed.png";
// 	public static String setMarker = resourcePath + "setMarker.png";
// 	public static String setMarkerPressed = resourcePath + "setMarkerPressed.png";
// 	public static String clearMarker = resourcePath + "clearMarker.png";
// 	public static String clearMarkerPressed = resourcePath + "clearMarkerPressed.png";

	//--- Timeline marks and markers
// 	public static String timeLineInMarker = resourcePath + "timeLineInMark.png";
// 	public static String timeLineOutMarker = resourcePath + "timeLineOutMark.png";
 	//public static String timeLineMark = resourcePath + "mark.png";
	//public static String timeLineEye = resourcePath + "iris.png";

	//--- Timeline action buttons
// 	public static String groupClips = resourcePath + "groupClips.png";
// 	public static String ungroupClips = resourcePath + "ungroupClips.png";
 	//public static String lockGroup = resourcePath + "lockGroup.png";
// 	public static String lockGroupPressed = resourcePath + "lockGroupPressed.png";
 	//public static String clipToMark = resourcePath + "clipToMark.png";
	//public static String clipToPreviousMark = resourcePath + "clipToPreviousMark.png";
	public static String deleteClip = resourcePath + "deleteClip.png";
	public static String clipDown = resourcePath + "clipDown.png";
	public static String clipUp = resourcePath + "clipUp.png";

	//--- Timeline edit buttons.
	public static String clipOutToCurrent = resourcePath + "clipOutToCurrent.png";
	public static String clipInToCurrent = resourcePath + "clipInToCurrent.png";
	public static String clipTailToCurrent = resourcePath + "clipTailToCurrent.png";
	public static String clipHeadToCurrent = resourcePath + "clipHeadToCurrent.png";

	public static String zoomIn = resourcePath + "zoomOut.png";
	public static String zoomOut = resourcePath + "zoomIn.png";
	
	//--- TimeLine navi and preview buttons 
	public static String toPreviousFrameNavi = resourcePath + "toPreviousFrameNavi.png";
	public static String toNextFrameNavi = resourcePath + "toNextFrameNavi.png";
	//public static String toEndNavi = resourcePath + "toEndNavi.png";
	//public static String toStartNavi = resourcePath + "toStartNavi.png";
	//public static String toPreviousStopNavi = resourcePath + "toPreviousStopNavi.png";
	//public static String toNextStopNavi = resourcePath + "toNextStopNavi.png";
	//public static String rewind = resourcePath + "rewind.png";
	//public static String fastForward = resourcePath + "fastForward.png";
	public static String play = resourcePath + "play.png";
	//public static String playGlass = resourcePath + "playGlass.png";
	public static String pause = resourcePath + "pause.png";
	//public static String previewFrame = resourcePath + "previewFrame.png";
// 	public static String previewMovie = resourcePath + "previewMovie.png";
// 	public static String previewRange = resourcePath + "previewRange.png";
	public static String renderPreview = resourcePath + "renderPreview.png";
	public static String renderLaunch = resourcePath + "renderLaunch.png";
	public static String loop = resourcePath + "loop.png";
	public static String loopPressed = resourcePath + "loopPressed.png";
	//public static String tLock = resourcePath + "tLock.png";
	//public static String tLockPressed = resourcePath + "tLockPressed.png";
	public static String stopPreviewRender = resourcePath + "stopPreviewRender.png";
	public static String noPreview = resourcePath +	"noPreview.png";
	public static String lockIcon = resourcePath + "lockIcon.png";

	//--- Switches + source buttons
	public static String motionBlurLabel = resourcePath + "motionBlur.png";
	public static String alfaLineLabel = resourcePath + "alphaLine.png";
	public static String iris = resourcePath + "iris.png";
	public static String bilinear = resourcePath + "bilinear.png";
	public static String bicubic = resourcePath + "bicubic.png";
	public static String nearest = resourcePath + "nearest.png";
	//public static String scaleLock = resourcePath + "scaleLock.png";
	//public static String scaleLockPressed = resourcePath + "scaleLockPressed.png";
	//public static String parentDialog = resourcePath + "parentDialog.png";
	public static String centerAnchor = resourcePath + "centerAnchor.png";
	public static String leafTrans  = resourcePath + "leafTrans.png";
	public static String leafTransPressed  = resourcePath + "leafTransPressed.png";
	public static String filterStack = resourcePath + "filterStack.png";
	public static String parentLabel = resourcePath + "parentLabel.png";
	public static String filterStackLabel = resourcePath + "filterStackLabel.png";

	//--- View editor
	public static String showViewEditUpdates = resourcePath + "showViewEditUpdates.png";
	public static String showViewEditUpdatesOff = resourcePath + "showViewEditUpdatesOff.png";
	//public static String viewImage = resourcePath + "viewImage.png";
	public static String viewImagePressed = resourcePath + "viewImagePressed.png";
	//public static String viewAlpha = resourcePath + "viewAlpha.png";
	public static String viewAlphaPressed = resourcePath + "viewAlphaPressed.png";
	public static String viewTarget = resourcePath + "viewTarget.png";
	public static String viewTargetPressed = resourcePath + "viewTargetPressed.png";
	public static String viewFlow = resourcePath + "viewFlow.png";
	public static String viewFlowPressed = resourcePath + "viewFlowPressed.png";
	public static String viewSelected = resourcePath + "viewSelected.png";
	public static String viewSelectedPressed = resourcePath + "viewSelectedPressed.png";
	public static String viewLayer = resourcePath + "viewLayer.png";
	public static String viewLayerPressed = resourcePath + "viewLayerPressed.png";
	//public static String lastPreview = resourcePath + "lastPreview.png";
	//public static String lastPreviewPressed = resourcePath + "lastPreviewPressed.png";
	public static String kfEdit = resourcePath + "kfEdit.png";
	public static String kfEditPressed = resourcePath + "kfEditPressed.png";
	public static String kfAdd = resourcePath + "kfAdd.png";
	public static String kfAddPressed = resourcePath + "kfAddPressed.png";
	public static String kfRemove = resourcePath + "kfRemove.png";
	public static String kfRemovePressed = resourcePath + "kfRemovePressed.png";
	public static String viewEditorLabel = resourcePath + "viewEditorLabel.png";
	public static String noViewEditorLabel = resourcePath + "noViewEditorLabel.png";
	public static String allBoxes = resourcePath + "allBoxes.png";
	public static String allBoxesPressed = resourcePath + "allBoxesPressed.png";	
	public static String pickColor = resourcePath + "pickColor.png";
	public static String pickColorPressed = resourcePath + "pickColorPressed.png";
	public static String pickFGColor = resourcePath + "pickFGColor.png";
	public static String pickFGColorPressed = resourcePath + "pickFGColorPressed.png";
	public static String pickBGColor = resourcePath + "pickBGColor.png";
	public static String pickBGColorPressed = resourcePath + "pickBGColorPressed.png";
	public static String renderClock = resourcePath + "renderClock.png";
	public static String renderClockDark = resourcePath + "renderClockDark.png";
	public static String renderClockTheme = renderClock;
	//public static String flowBroke = resourcePath + "flowBroke.png";
	public static String customButton = resourcePath + "custombutton.png";
	public static String customPressed = resourcePath + "custombuttonpressed.png";
	//public static String move3D = resourcePath + "move3D.png";
	//public static String move3DPressed = resourcePath + "move3Dpressed.png";
	public static String panelSizes = resourcePath + "panelSizes.png";
	
	//--- View editor action buttons
	public static String move = resourcePath + "move.png";
	public static String movePressed = resourcePath + "movePressed.png";
	public static String rotate = resourcePath + "rotate.png";
	public static String rotatePressed = resourcePath + "rotatePressed.png";
	//public static String anchor = resourcePath + "anchor.png";
	//public static String anchorPressed = resourcePath + "anchorPressed.png";
	public static String layerUp = resourcePath + "layerUp.png";
	//public static String layerDown = resourcePath + "layerDown.png";

	//--- KeyFrame editor
	public static String scaleZoomIn = resourcePath + "scaleZoomIn.png";
	public static String scaleZoomOut = resourcePath + "scaleZoomOut.png";
	//public static String scaleZoomAuto = resourcePath + "scaleZoomAuto.png";
	public static String addKF = resourcePath + "addKF.png";
	public static String deleteKF = resourcePath + "deleteKF.png";
	//public static String verticalPos = resourcePath + "verticalPos.png";

	//public static String areaIn  = resourcePath + "areaIn.png";
	//public static String areaOut  = resourcePath + "areaOut.png";

	public static String colorWheel = resourcePath + "colorWheel.png";

	//--- Flow edit
	public static String flowBoxBG = resourcePath + "flowboxbg.png";
	public static String flowBoxBGSelected = resourcePath + "flowboxbgselected.png";
	//public static String flowBoxBGMoving = resourcePath + "flowboxbgmoving.png";
	public static String flowBoxBGFilter = resourcePath + "flowboxbgfilter.png";
	public static String flowBoxBGMerge = resourcePath + "flowboxbgmerge.png";
	
	//--- logo
	//public static String phantomLogo = resourcePath + "phantom_logo_color_small.png";
	public static String phantomLogoSmall = resourcePath + "phantom_logo_color_tiny.png";

	//--- icon
	public static String phantomIcon = resourcePath + "phantom_icon.png";
	public static String folderIcon = resourcePath + "folder.png";
	public static String fileIcon = resourcePath + "file.png";

	//--- Progress bar
// 	public static String progressPiece = resourcePath + "progressPiece.png";

	//--- Value editor label
	public static final String keyFrameSmall = resourcePath + "keyFrameSmall.png";

	
	public static final String keyframeProperties = resourcePath + "keyframeProperties.png";
	
	public static final String keyframePropertiesDisabled  = resourcePath + "keyframePropertiesDisabled.png";

	//--- buttonFactory
	//public static final String buttonLeft = resourcePath + "button_left_edge.png";
	//public static final String buttonRight = resourcePath + "button_right_edge.png";
	//public static final String buttonMiddle = resourcePath + "button_middle.png";

	//public static final String gtkStop  = resourcePath + "gtkStop.png";
// 	public static final String gtkOk = resourcePath + "gtkOk.png";
// 	public static final String gtkSave  = resourcePath + "gtkSave.png";
// 	public static final String gtkClose  = resourcePath + "gtkClose.png";
// 	public static final String gtkTrash  = resourcePath + "gtkTrash.png";
// 	public static final String gtkExit  = resourcePath + "gtkExit.png";

	//--- Dialogs
	public static final String gtk_dialog_warning = resourcePath + "gtk-dialog-warning.png";

	//--- texts
	//public static String faq = resourcePath + "texts/faq.txt";
	//public static String help = resourcePath + "texts/help.txt";
	//public static String keyShortcutInfo = resourcePath + "texts/key_sc_info.txt";
	//public static String codecInfo = resourcePath + "texts/codec_info.txt";

	//--- BUTTON DIMENSIONS, BUTTON DIMENSIONS, BUTTON DIMENSIONS, BUTTON DIMENSIONS, BUTTON DIMENSIONS
	//public static final Dimension bigButton = new Dimension( 34, 34 );
	//public static final Dimension bigWideButton = new Dimension( 64, 34 );
 	public static final Dimension mediumButton = new Dimension( 27, 27 );
	//public static final Dimension smallButton = new Dimension( 24, 24 );

	public static String emptyIcon = resourcePath + "emptyIcon.png";
	public static String draggedNode = resourcePath + "draggedNode.png";
 
	public static ImageIcon getIcon( String path )
	{ 
		if( Application.inJar() )
		{
			URL u = o.getClass().getResource( path );
			return new ImageIcon( u );
		}
		return new ImageIcon( path );
	}

	public static BufferedImage getResourceBufferedImage( String path )
	{
		if( !Application.inJar() ) 
			return GUIUtils.getBufferedImageFromFile( new File( path ) );
		else
		{
			try
			{
				InputStream is = o.getClass().getResourceAsStream( path );
				BufferedImage loadImg = ImageIO.read( is );
	
				//--- Force alpha if missing. Keep if exists.
				BufferedImage img = new BufferedImage( 	loadImg.getWidth(),
										loadImg.getHeight(),
										BufferedImage.TYPE_INT_ARGB );
				Graphics2D g2 = img.createGraphics();
				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC, 1.0f ) );
				g2.drawRenderedImage( loadImg, null );
				g2.dispose();
				
				return img;
			}
			catch( Exception e )
			{
				System.out.println("Resource IMAGE LOAD FAILED FOR "+ path );
				return null;
			}
		}

	}

	public static Font getFont( String path ) 
	{
		Font font = null;

		try {
			InputStream is = null;
			if( Application.inJar() )
			{
				is = o.getClass().getResourceAsStream( path );
			}
			else
			{
				is = new FileInputStream( path );
			}
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			System.err.println( path + " font was not loaded.");
		}
		return font;
	}
/*
	public static void prepareBigButton( 	AbstractButton button,
						ActionListener listener,
						String toolTipText )
	{
		button.addActionListener( listener );
		button.setToolTipText( toolTipText );
		setButtonSizeBigButton( button );
	}
*/
	/*
	public static void setButtonSizeBigButton( AbstractButton button )
	{
		button.setPreferredSize( bigButton );
		button.setMaximumSize( bigButton );
	}
	*/
	public static void prepareMediumButton( AbstractButton button,
						ActionListener listener,
						String toolTipText )
	{
		button.addActionListener( listener );
		button.setToolTipText( toolTipText );
		setButtonSizeMediumButton( button );
	}
/*
	public static void prepareMediumWideButton( AbstractButton button,
						ActionListener listener,
						String toolTipText )
	{
		button.addActionListener( listener );
		button.setToolTipText( toolTipText );
		setButtonSizeMediumButton( button );
		Dimension medWide = new Dimension( 64, 27 );
		button.setPreferredSize( medWide );
		button.setMaximumSize( medWide );
	}
*/
	public static void prepareMediumMediumButton( AbstractButton button,
						ActionListener listener,
						String toolTipText )
	{
		button.addActionListener( listener );
		button.setToolTipText( toolTipText );
		setButtonSizeMediumButton( button );
		Dimension medWide = new Dimension( 47, 27 );
		button.setPreferredSize( medWide );
		button.setMaximumSize( medWide );
	}

	public static void setButtonSizeMediumButton( AbstractButton button )
	{
		button.setPreferredSize( mediumButton );
		button.setMaximumSize( mediumButton );
	}



/*
	//--- LOOK AND FEEL, LOOK AND FEEL, LOOK AND FEEL, LOOK AND FEEL, LOOK AND FEEL, LOOK AND FEEL
	public static void natifyIcons()
	{
		addFile = natifyString( addFile );
		addBin = natifyString( addBin );
		addFileSequence = natifyString( addFileSequence );
		cancel = natifyString( cancel );
		cut = natifyString( cut );
		clipUp = natifyString( clipUp );
		clipDown = natifyString( clipDown );
		deleteBoxes = natifyString(  deleteBoxes );
		deleteBin = natifyString( deleteBin );
		deleteClip = natifyString( deleteClip );
		groupClips = natifyString( groupClips );
		ungroupClips = natifyString( ungroupClips );

		paste = natifyString( paste );
		renameBin = natifyString( renameBin );
		toFlow = natifyString( toFlow );
		toFlowWide = natifyString( toFlowWide );
		toTimeline = natifyString( toTimeline );
	}

	//not used currently
	public static void natifySmallButtons()
	{
		inMarker = natifyString(  inMarker );
		anchor = natifyString( anchor );
		anchorPressed = anchor;
	}

	private static String natifyString( String original )
	{
		if( original.contains( nativePath ) ) return original;//lets not do this twice even if asked.
		StringBuffer buf = new StringBuffer( original );
		buf.delete( 0, resourcePath.length() );
		buf.insert( 0, nativePath );
		buf.insert( 0, resourcePath );
		return buf.toString();
	}
	*/
}//end class
