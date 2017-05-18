package animator.phantom.controller;

/*
    Copyright Janne Liljeblad.

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

import animator.phantom.renderer.FileSource;

public class GraphicsAnimatorController
{

	public static void setAnimatedGraphic()
	{
		new Thread()
		{
			public void run()
			{
				UserActions.addSingleFileSources(FileSource.IMAGE_FILE, -1, -1);
				FileSource fs = ProjectController.getProject().getFileSources().elementAt(0);
				FlowActions.addIOPFromFileSource( fs );
			}
		}.start();
	}

}//end class
