package animator.phantom.paramedit.imagemerge;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.ButtonRow;
import animator.phantom.paramedit.ParamColorDisplay;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.SingleCurveEditor;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.imagemerge.ColorRangeKeyIOP;

public class ColorRangeKeyEditPanel extends ParamEditPanel implements ActionListener
{
	public ColorRangeKeyIOP crk;
	public ParamColorDisplay cDisp;
	private JButton delLast = new JButton("Delete last sample");
	private JButton delAll = new JButton("Delete all samples");

	public ParamColorDisplay fgDisp;
	public ParamColorDisplay bgDisp;

	private JButton delLastBG = new JButton("Delete last sample");
	private JButton delAllBG = new JButton("Delete all samples");

	private JButton delLastFG = new JButton("Delete last sample");
	private JButton delAllFG = new JButton("Delete all samples");

	public ColorRangeKeyEditPanel( ColorRangeKeyIOP crk )
	{
		this.crk = crk;
		initParamEditPanel();

		//--- Key
		cDisp = new ParamColorDisplay( crk.dispColor, "Samples: 0", "keycolor" );
		AnimValueSliderEditor spread = new AnimValueSliderEditor( "Spread", crk.spread );
		SingleCurveEditor aedit = new SingleCurveEditor( "Key value to Alpha", crk.alphaCurve, 156 );

		Vector<JButton> buttons = new Vector<JButton>();
		buttons.add( delAll );
		buttons.add( delLast );
		ButtonRow row = new ButtonRow( this, buttons );

		//--- FG/BG Clean
		fgDisp = new ParamColorDisplay( crk.fgColor, "Foreground; 0", "keycolor" );
		bgDisp = new ParamColorDisplay( crk.bgColor, "Background : 0", "bgkeycolor" );

		Vector<JButton> buttons3 = new Vector<JButton>();
		buttons3.add( delAllFG );
		buttons3.add( delLastFG );
		ButtonRow row3 = new ButtonRow( this, buttons3 );
		
		Vector<JButton> buttons2 = new Vector<JButton>();
		buttons2.add( delAllBG );
		buttons2.add( delLastBG );
		ButtonRow row2 = new ButtonRow( this, buttons2 );

		//--- Tabs
		Vector<String> tabs = new Vector<String>();
		tabs.add( "Key" );
		tabs.add( "FG/BG Clean" );
		setTabbedPanel( 350, tabs );

		addToTab( "Key", cDisp );
		addToTab( "Key", new RowSeparator() );
		addToTab( "Key", row );
		addToTab( "Key", new RowSeparator() );
		addToTab( "Key", spread );
		addToTab( "Key", new RowSeparator() );
		addToTab( "Key", aedit );

		addToTab( "FG/BG Clean", fgDisp );
		addToTab( "FG/BG Clean", new RowSeparator() );
		addToTab( "FG/BG Clean", row3 );
		addToTab( "FG/BG Clean", new RowSeparator() );
		addToTab( "FG/BG Clean", bgDisp );
		addToTab( "FG/BG Clean", new RowSeparator() );
		addToTab( "FG/BG Clean", row2 );
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == delLast ) crk.deleteLast();
		if( e.getSource() == delAll ) crk.deleteAll();

		if( e.getSource() == delLastBG ) crk.deleteLast( ColorRangeKeyIOP.BACKGROUND );
		if( e.getSource() == delAllBG ) crk.deleteAll( ColorRangeKeyIOP.BACKGROUND );

		if( e.getSource() == delLastFG ) crk.deleteLast( ColorRangeKeyIOP.FOREGROUND );
		if( e.getSource() == delAllFG ) crk.deleteAll( ColorRangeKeyIOP.FOREGROUND );
	}

}//end class
