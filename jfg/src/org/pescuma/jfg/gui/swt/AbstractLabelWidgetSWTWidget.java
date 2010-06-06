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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pescuma.jfg.Attribute;

abstract class AbstractLabelWidgetSWTWidget extends AbstractSWTWidget
{
	private Label name;
	
	public AbstractLabelWidgetSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout)
	{
		Composite[] parents = layout.getParentsForLabelWidget(attrib.getName());
		if (parents[0] != null)
		{
			name = data.componentFactory.createLabel(parents[0], SWT.NONE);
			name.setText(data.textTranslator.fieldName(attrib.getName()) + ":");
		}
		
		if (parents[1] == null)
			throw new IllegalStateException();
		
		Control widget = createWidget(parents[1]);
		
		addAttributeListener();
		
		layout.addLabelWidget(attrib.getName(), name, widget, wantToFillVertical());
	}
	
	protected abstract Control createWidget(Composite parent);
	
	protected boolean wantToFillVertical()
	{
		return false;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		name.setEnabled(enabled);
	}
	
}
