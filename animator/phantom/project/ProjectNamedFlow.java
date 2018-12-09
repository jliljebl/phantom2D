package animator.phantom.project;

import java.awt.Dimension;
import java.util.Vector;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;

public class ProjectNamedFlow 
{

	private int id;
	private String name;
	private Dimension screenSize;
	private RenderFlow renderFlow;
	private Vector<ImageOperation> timelineClips;
	private int length;
	
	public ProjectNamedFlow(){}
	
	public ProjectNamedFlow(int id, String name, int length, Dimension screenSize )
	{
		this.id = id;
		this.name = name;
		this.length = length;
		this.screenSize = new Dimension(screenSize.width, screenSize.height );
		this.renderFlow = new RenderFlow();
		this.timelineClips = new Vector<ImageOperation>();
	}

	public void setID( int newId ){ id = newId; }
	public int getID() { return id; }
	
	public void setFlow( RenderFlow flow ){ this.renderFlow = flow; }
	public RenderFlow getFlow() { return renderFlow; }
	
	public void setTimelineClips(Vector<ImageOperation> newClips ) { timelineClips = newClips; }
	public Vector<ImageOperation> getTimelineClips(){ return timelineClips; }
	
	public void setName( String newName ) { name = newName; }
	public String getName() { return name; }
	
	public int getLength() { return length; }
	public void setLength( int newLength ) {length = newLength; }
	
	public void setScreenDimensions( Dimension newScreenSize) { screenSize = newScreenSize; }
	public Dimension getScreenDimensions() { return screenSize; }

}//end class
