/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

package jfg.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

public class SWTSimpleComponentFactory implements SWTComponentFactory
{
	public Text createText(Composite parent, int style)
	{
		return new Text(parent, SWT.BORDER | style);
	}
	
	public Button createCheckbox(Composite parent, int style)
	{
		return new Button(parent, SWT.CHECK | style);
	}
	
	public Combo createCombo(Composite parent, int style)
	{
		return new Combo(parent, style);
	}
	
	public Group createGroup(Composite parent, int style)
	{
		return new Group(parent, style);
	}
	
	public Scale createScale(Composite parent, int style)
	{
		return new Scale(parent, style);
	}
	
}
