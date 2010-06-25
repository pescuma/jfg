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

import static org.eclipse.swt.layout.GridData.*;
import static org.pescuma.jfg.gui.swt.SWTHelper.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SWTSimpleFormBuilder implements SWTLayoutBuilder, Cloneable
{
	private Composite parent;
	private Runnable layoutListener;
	private JfgFormData data;
	
	@Override
	public void init(Composite root, Runnable layoutListener, JfgFormData data)
	{
		if (parent != null)
			throw new IllegalStateException("Already initialized. Create a new one!");
		
		this.parent = root;
		this.layoutListener = layoutListener;
		this.data = data;
		
		GridLayout layout = (GridLayout) parent.getLayout();
		
		if (layout == null)
			parent.setLayout(createBorderlessGridLayout(2, false));
		
		else if (!(layout instanceof GridLayout))
			throw new IllegalArgumentException("SWTSimpleFormBuilder needs a GridLayout");
		
		else
			layout.numColumns = 2;
	}
	
	@Override
	public Runnable getLayoutListener()
	{
		return layoutListener;
	}
	
	public Composite[] getParentsForLabelWidget(String attributeName)
	{
		return new Composite[] { parent, parent };
	}
	
	public void addLabelWidget(String attributeName, Label label, Control widget, boolean wantToFillVertical)
	{
		label.setLayoutData(new GridData(HORIZONTAL_ALIGN_END));
		widget.setLayoutData(new GridData(wantToFillVertical ? FILL_BOTH : FILL_HORIZONTAL));
	}
	
	public Composite getParentForWidget(String attributeName)
	{
		return createFullRowComposite();
	}
	
	public void addWidget(String attributeName, Control widget, boolean wantToFillVertical)
	{
		widget.setLayoutData(new GridData(wantToFillVertical ? FILL_BOTH : FILL_HORIZONTAL));
	}
	
	public Group addGroup(String groupName)
	{
		Group frame = data.componentFactory.createGroup(createFullRowComposite(), SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.groupName(groupName));
		
		return frame;
	}
	
	private Composite createFullRowComposite()
	{
		return createFullRowComposite(parent);
	}
	
	protected Composite createFullRowComposite(Composite compositeParent)
	{
		Composite composite = data.componentFactory.createComposite(compositeParent, SWT.NONE);
		setupHorizontalComposite(composite, 2);
		return composite;
	}
	
	@Override
	public ListBuilder addList(String attributeName)
	{
		Group frame = data.componentFactory.createGroup(createFullRowComposite(), SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.fieldName(attributeName));
		
		return new SWTSimpleFormListBuilder(attributeName, frame, layoutListener, data);
	}
	
	@Override
	public SWTSimpleFormBuilder clone()
	{
		try
		{
			return (SWTSimpleFormBuilder) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
