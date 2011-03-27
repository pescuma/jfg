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
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class SWTSimpleComponentFactory implements SWTComponentFactory
{
	@Override
	public Text createText(Composite parent, int style)
	{
		return new Text(parent, SWT.BORDER | style);
	}
	
	@Override
	public Button createCheckbox(Composite parent, int style)
	{
		return new Button(parent, SWT.CHECK | style);
	}
	
	@Override
	public Combo createCombo(Composite parent, int style)
	{
		return new Combo(parent, style);
	}
	
	@Override
	public Group createGroup(Composite parent, int style)
	{
		return new Group(parent, style);
	}
	
	@Override
	public Scale createScale(Composite parent, int style)
	{
		return new Scale(parent, style);
	}
	
	@Override
	public Label createLabel(Composite parent, int style)
	{
		return new Label(parent, style);
	}
	
	@Override
	public Composite createComposite(Composite parent, int style)
	{
		return new Composite(parent, style);
	}
	
	@Override
	public Button createButton(Composite parent, int style)
	{
		return new Button(parent, style);
	}
	
	@Override
	public Control createFlatButton(Composite parent, String text, String image, Listener selectionListener)
	{
		ToolBar addMore = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.NO_BACKGROUND);
		ToolItem item = new ToolItem(addMore, SWT.PUSH);
		item.setText(text);
		item.setImage(new Image(item.getDisplay(), image));
		item.addListener(SWT.Selection, selectionListener);
		item.addListener(SWT.DefaultSelection, selectionListener);
		
		return addMore;
	}
	
}
