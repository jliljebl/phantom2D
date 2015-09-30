package animator.phantom.gui.timeline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import animator.phantom.gui.GUIResources;

public class TimeLineControls implements ChangeListener,  ActionListener
{
	private TimeLinePositionSlider positionSlider;
	
	private JButton zoomIn = new JButton( GUIResources.getIcon( GUIResources.zoomIn ) );
	private JButton zoomOut = new JButton( GUIResources.getIcon( GUIResources.zoomOut ) );
	
	public TimeLineControls()
	{
		GUIResources.prepareMediumButton( zoomIn, this, "Zoom in" );
		GUIResources.prepareMediumButton( zoomOut, this, "Zoom out" );

		positionSlider = new TimeLinePositionSlider();
	}
	
	public void addComponentsToPanel( JPanel panel )
	{
		panel.add( positionSlider );
		panel.add( zoomIn );
		panel.add( zoomOut );
	}
	public void stateChanged(ChangeEvent e) 
	{
		
	}

	public void update()
	{
		positionSlider.repaint();
	}
	public void actionPerformed(ActionEvent arg0) 
	{
		
	}

}//end class
