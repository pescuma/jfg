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

package org.pescuma.jfg.gui.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.gui.GuiWidget;

public class SWTTextBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == String.class || "text".equals(type);
	}
	
	public GuiWidget build(Composite aParent, Attribute attrib, JfgFormData data)
	{
		return new AbstractLabeledSWTWidget(aParent, attrib, data) {
			
			private Text text;
			private Color background;
			
			@Override
			protected void createWidget(Composite parent)
			{
				text = data.componentFactory.createText(parent, (attrib.canWrite() ? SWT.NONE : SWT.READ_ONLY) | getAdditionalTextStyle());
				text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				text.addListener(SWT.Modify, getModifyListener());
				text.addListener(SWT.Dispose, getDisposeListener());
				addValidation(text, getType(attrib.getType()));
				setTextLimit(attrib, text);
				
				addAttributeListener();
				
				background = text.getBackground();
			}
			
			public Object getValue()
			{
				return convertToObject(text.getText(), getType(attrib.getType()), canBeNull());
			}
			
			private boolean canBeNull()
			{
				AttributeValueRange range = attrib.getValueRange();
				if (range == null)
					return true;
				
				return range.canBeNull();
			}
			
			public void setValue(Object value)
			{
				int caretPosition = text.getCaretPosition();
				text.setText(convertToString(text, value, getType(attrib.getType())));
				text.setSelection(caretPosition);
			}
			
			@Override
			protected void markField()
			{
				text.setBackground(data.createBackgroundColor(text, background));
			}
			
			@Override
			protected void unmarkField()
			{
				text.setBackground(background);
			}
			
			@Override
			public void setEnabled(boolean enabled)
			{
				super.setEnabled(enabled);
				
				text.setEnabled(enabled);
			}
		};
	}
	
	protected int getAdditionalTextStyle()
	{
		return 0;
	}
	
	protected void addValidation(Text text, Object type)
	{
	}
	
	protected String convertToString(Text text, Object value, Object type)
	{
		if (value == null)
			return "";
		else
			return value.toString();
	}
	
	protected Object convertToObject(String value, Object type, boolean canBeNull)
	{
		if (value == null && !canBeNull)
			return "";
		return value;
	}
	
	protected Object getType(Object type)
	{
		return String.class;
	}
	
	protected void setTextLimit(Attribute attrib, Text text)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return;
		
		Object max = range.getMax();
		if (max != null && (max instanceof Number))
			text.setTextLimit(((Number) max).intValue());
	}
	
}
