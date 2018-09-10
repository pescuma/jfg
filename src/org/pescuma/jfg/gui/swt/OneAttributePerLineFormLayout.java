/*
 * Copyright 2010 Ricardo Pescuma Domenecci
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

import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END;
import static org.pescuma.jfg.gui.swt.SWTUtils.createBorderlessGridLayout;
import static org.pescuma.jfg.gui.swt.SWTUtils.layoutHintsToGridDataStyle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class OneAttributePerLineFormLayout implements SWTLayoutBuilder, Cloneable
{
	private Composite parent;
	private JfgFormData data;
	
	@Override
	public void init(Composite parent, JfgFormData data)
	{
		if (this.parent != null)
			throw new IllegalStateException("Already initialized. Create a new one!");
		
		this.parent = parent;
		this.data = data;
		
		Layout layout = this.parent.getLayout();
		
		if (layout == null)
			this.parent.setLayout(createBorderlessGridLayout(2, false));
		
		else if (!(layout instanceof GridLayout))
			throw new IllegalArgumentException("SWTSimpleFormBuilder needs a GridLayout");
		
		else
			((GridLayout) layout).numColumns = 2;
	}
	
	@Override
	public Composite[] getParentsForLabelWidget(String attributeName)
	{
		return new Composite[] { parent, parent };
	}
	
	@Override
	public void addLabelWidget(String attributeName, Label label, Control widget, int layoutHints, int heightHint)
	{
		GridData gd = new GridData(HORIZONTAL_ALIGN_END);
		gd.heightHint = heightHint;
		label.setLayoutData(gd);
		
		gd = new GridData(layoutHintsToGridDataStyle(layoutHints));
		gd.heightHint = heightHint;
		widget.setLayoutData(gd);
	}
	
	@Override
	public Composite getParentForWidget(String attributeName)
	{
		return parent;
	}
	
	@Override
	public void addWidget(String attributeName, Control widget, int layoutHints, int heightHint)
	{
		GridData gd = new GridData(layoutHintsToGridDataStyle(layoutHints));
		gd.heightHint = heightHint;
		gd.horizontalSpan = 2;
		widget.setLayoutData(gd);
	}
	
	@Override
	public Group addGroup(String groupName, int layoutHints, int heightHint)
	{
		Group frame = data.componentFactory.createGroup(parent, SWT.NONE);
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.groupName(groupName));
		
		addWidget(groupName, frame, layoutHints, heightHint);
		
		return frame;
	}
	
	@Override
	public ListBuilder addList(String attributeName, int layoutHints, int heightHint)
	{
		Group frame = data.componentFactory.createGroup(parent, SWT.NONE);
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.fieldName(attributeName));
		
		addWidget(attributeName, frame, layoutHints, heightHint);
		
		return new SWTSimpleFormListBuilder(attributeName, frame, data);
	}
	
	@Override
	public OneAttributePerLineFormLayout clone()
	{
		try
		{
			return (OneAttributePerLineFormLayout) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
