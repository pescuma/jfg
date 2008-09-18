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

import jfg.Attribute;
import jfg.gui.GuiWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SWTCheckboxBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == Boolean.class || type == boolean.class || "checkbox".equals(type);
	}
	
	public GuiWidget build(Composite aParent, Attribute attrib, JfgFormData data)
	{
		return new AbstractSWTWidget(aParent, attrib, data) {
			
			private Button chk;
			private Color background;
			
			@Override
			protected void createWidget(Composite parent)
			{
				chk = data.componentFactory.createCheckbox(parent, SWT.NONE);
				chk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				chk.setText(data.textTranslator.fieldName(attrib.getName()));
				chk.addListener(SWT.Selection, getModifyListener());
				chk.addListener(SWT.Dispose, getDisposeListener());
				
				// SWT does not support a read-only checkbox
				if (!attrib.canWrite())
				{
					chk.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event)
						{
							copyToGUI();
						}
					});
				}
				
				addAttributeListener();
				
				background = chk.getBackground();
			}
			
			public Object getValue()
			{
				return chk.getSelection() ? Boolean.TRUE : Boolean.FALSE;
			}
			
			public void setValue(Object value)
			{
				chk.setSelection((Boolean) value);
			}
			
			@Override
			protected void markField()
			{
				chk.setBackground(data.createBackgroundColor(chk, background));
			}
			
			@Override
			protected void unmarkField()
			{
				chk.setBackground(background);
			}
			
			@Override
			public void setEnabled(boolean enabled)
			{
				super.setEnabled(enabled);
				
				chk.setEnabled(enabled);
			}
		};
	}
}
