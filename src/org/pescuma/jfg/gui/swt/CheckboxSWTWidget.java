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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;

class CheckboxSWTWidget extends AbstractControlSWTWidget
{
	private Button checkbox;
	private Color background;
	
	CheckboxSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		checkbox = data.componentFactory.createCheckbox(parent, SWT.NONE);
		
		if (attrib.getName() != null && showLabel())
			checkbox.setText(data.textTranslator.fieldName(attrib.getName()));
		
		checkbox.addListener(SWT.Selection, getModifyListener());
		checkbox.addListener(SWT.Dispose, getDisposeListener());
		
		// SWT does not support a read-only checkbox
		if (!attrib.canWrite())
		{
			checkbox.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event)
				{
					copyToGUI();
				}
			});
		}
		
		background = checkbox.getBackground();
		
		return checkbox;
	}
	
	@Override
	public Object getValue()
	{
		return checkbox.getSelection() ? Boolean.TRUE : Boolean.FALSE;
	}
	
	@Override
	public void setValue(Object value)
	{
		checkbox.setSelection((Boolean) value);
	}
	
	@Override
	protected void updateColor()
	{
		checkbox.setBackground(createColor(checkbox, background));
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		checkbox.setEnabled(enabled);
	}
}