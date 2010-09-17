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

import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;

public class SWTTextBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == String.class || "text".equals(type);
	}
	
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
		return new TextSWTWidget(attrib, data) {
			protected int getAdditionalTextStyle()
			{
				return SWTTextBuilder.this.getAdditionalTextStyle();
			}
			
			protected void addValidation(Text text, Object type)
			{
				SWTTextBuilder.this.addValidation(text, type);
			}
			
			protected String convertToString(Text text, Object value, Object type)
			{
				return SWTTextBuilder.this.convertToString(text, value, type);
			}
			
			protected Object convertToObject(String value, Object type, boolean canBeNull)
			{
				return SWTTextBuilder.this.convertToObject(value, type, canBeNull);
			}
			
			protected Object getType(Object type)
			{
				return SWTTextBuilder.this.getType(type);
			}
			
			protected void setTextLimit(Attribute attrib, Text text)
			{
				SWTTextBuilder.this.setTextLimit(attrib, text);
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
