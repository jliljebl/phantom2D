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

public class Phantom2D
{

	public static void main( String args[] )
	{
		String profile = null;
		String diskCacheDirPath = null;

		for (int i=0; i < args.length; i++)
		{
			String arg = args[i];

			if (arg.equals("-p"))
				profile =  args[i + 1];

			if (arg.equals("-c"))
				diskCacheDirPath = args[i + 1];
		}
		
		LayerCompositorApplication app = new LayerCompositorApplication();
		app.startUp(profile, diskCacheDirPath);
	}

}//end class
