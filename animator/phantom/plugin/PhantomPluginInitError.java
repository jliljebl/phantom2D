package animator.phantom.plugin;

/**
* This error is thrown when plugin is initialized incorrectly.
*/
public class PhantomPluginInitError extends Error
{
	private static final long serialVersionUID = 1L;
	
	public PhantomPluginInitError( String msg )
	{
		super( msg );
	}

}//end class
