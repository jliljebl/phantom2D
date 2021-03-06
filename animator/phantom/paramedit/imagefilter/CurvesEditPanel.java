package animator.phantom.paramedit.imagefilter;

/*
    Copyright Janne Liljeblad

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

import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.imagefilter.CurvesIOP;

public class CurvesEditPanel extends ParamEditPanel
{
	private static final long serialVersionUID = 1L;
	
	public CurvesEditPanel( CurvesIOP ciop )
	{
		initParamEditPanel();
		CurvesEditor cedit = new CurvesEditor( "text2", ciop );
	
		add( cedit );
	}

}//end class