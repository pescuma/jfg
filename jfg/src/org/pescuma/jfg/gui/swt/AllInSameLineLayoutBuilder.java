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
import static org.pescuma.jfg.gui.swt.SWTUtils.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class AllInSameLineLayoutBuilder implements SWTLayoutBuilder, Cloneable
{
	private Composite parent;
	private Runnable layoutListener;
	private JfgFormData data;
	private int desidedColumns = 0;
	
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
			parent.setLayout(createBorderlessGridLayout(1, false));
		
		else if (!(layout instanceof GridLayout))
			throw new IllegalArgumentException("SWTAllInSameLineBuilder needs a GridLayout");
		
		else
			layout.numColumns = 1;
	}
	
	private void updateColumns(int columnsToAdd)
	{
		desidedColumns += columnsToAdd;
		((GridLayout) parent.getLayout()).numColumns = desidedColumns;
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
	
	public void addLabelWidget(String attributeName, Label label, Control widget, int layoutHints)
	{
		label.setLayoutData(new GridData(HORIZONTAL_ALIGN_END));
		
		widget.setLayoutData(new GridData(layoutHintsToGridDataStyle(layoutHints)));
		
		updateColumns(2);
	}
	
	public Composite getParentForWidget(String attributeName)
	{
		return parent;
	}
	
	public void addWidget(String attributeName, Control widget, int layoutHints)
	{
		widget.setLayoutData(new GridData(layoutHintsToGridDataStyle(layoutHints)));
		
		updateColumns(1);
	}
	
	public Group addGroup(String groupName)
	{
		Group frame = data.componentFactory.createGroup(parent, SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.groupName(groupName));
		
		updateColumns(1);
		
		return frame;
	}
	
	@Override
	public ListBuilder addList(String attributeName)
	{
		Group frame = data.componentFactory.createGroup(parent, SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.fieldName(attributeName));
		
		updateColumns(1);
		
		return new SWTSimpleFormListBuilder(attributeName, frame, layoutListener, data);
	}
	
	@Override
	public AllInSameLineLayoutBuilder clone()
	{
		try
		{
			return (AllInSameLineLayoutBuilder) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
