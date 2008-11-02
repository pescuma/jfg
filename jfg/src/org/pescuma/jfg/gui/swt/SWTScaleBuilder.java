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

import static java.lang.Math.*;
import static org.pescuma.jfg.gui.swt.TypeUtils.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.gui.GuiWidget;

public class SWTScaleBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null || range.getMin() == null || range.getMax() == null)
			return false;
		
		Object type = attrib.getType();
		return typeIsNumber(type) || typeIsReal(type) || "scale".equals(type);
	}
	
	public GuiWidget build(Composite aParent, Attribute attrib, JfgFormData data)
	{
		return new AbstractLabeledSWTWidget(aParent, attrib, data) {
			
			private Scale scale;
			private Color background;
			
			private Object getType()
			{
				Object type = attrib.getType();
				if ("scale".equals(type))
					type = long.class;
				return type;
			}
			
			@Override
			protected void createWidget(Composite parent)
			{
				scale = data.componentFactory.createScale(parent, SWT.NONE);
				scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				scale.addListener(SWT.Selection, getModifyListener());
				scale.addListener(SWT.Dispose, getDisposeListener());
				setLimits();
				
				addAttributeListener();
				
				background = scale.getBackground();
			}
			
			private void setLimits()
			{
				Object type = getType();
				
				Number minObj = (Number) attrib.getValueRange().getMin();
				Number maxObj = (Number) attrib.getValueRange().getMax();
				
				if (typeIsNumber(type))
				{
					long min = minObj.longValue();
					long max = maxObj.longValue();
					
					// It does not make sense beeing > 100
					int diff = (int) min(100, max - min);
					if (diff <= 0)
						throw new IllegalArgumentException();
					
					scale.setMaximum(diff);
					scale.setPageIncrement(max(1, diff / 5));
				}
				else
				{
					// Is real
					double min = minObj.doubleValue();
					double max = maxObj.doubleValue();
					
					double diff = max - min;
					if (diff <= 0)
						throw new IllegalArgumentException();
					
					scale.setMaximum(100);
					scale.setPageIncrement(20);
				}
			}
			
			public Object getValue()
			{
				return convertToObject(scale.getSelection());
			}
			
			private Object convertToObject(int selection)
			{
				Object type = getType();
				
				Number minObj = (Number) attrib.getValueRange().getMin();
				Number maxObj = (Number) attrib.getValueRange().getMax();
				
				if (typeIsNumber(type))
				{
					long min = minObj.longValue();
					long max = maxObj.longValue();
					
					long diff = max - min;
					if (diff <= 100)
						return TypeUtils.valueOf(min + selection, type);
					
					double factor = min(100, diff) / 100.0;
					return TypeUtils.valueOf(min + (long) (selection * factor), type);
				}
				else
				{
					// Is real
					double min = minObj.doubleValue();
					double max = maxObj.doubleValue();
					
					double diff = max - min;
					double factor = diff / 100.0;
					
					return TypeUtils.valueOf(min + selection * factor, type);
				}
			}
			
			public void setValue(Object value)
			{
				scale.setSelection(convertToInt(value));
			}
			
			private int convertToInt(Object value)
			{
				Object type = getType();
				
				Number minObj = (Number) attrib.getValueRange().getMin();
				Number maxObj = (Number) attrib.getValueRange().getMax();
				
				if (typeIsNumber(type))
				{
					long min = minObj.longValue();
					long max = maxObj.longValue();
					long val = (value == null ? min : ((Number) value).longValue());
					
					long diff = max - min;
					if (diff <= 100)
						return (int) (val - min);
					
					double factor = min(100, diff) / 100.0;
					return (int) round((val - min) / factor);
				}
				else
				{
					// Is real
					double min = minObj.doubleValue();
					double max = maxObj.doubleValue();
					double val = (value == null ? min : ((Number) value).doubleValue());
					
					double diff = max - min;
					double factor = diff / 100.0;
					
					return (int) round((val - min) / factor);
				}
			}
			
			@Override
			protected void markField()
			{
				scale.setBackground(data.createBackgroundColor(scale, background));
			}
			
			@Override
			protected void unmarkField()
			{
				scale.setBackground(background);
			}
			
			@Override
			public void setEnabled(boolean enabled)
			{
				super.setEnabled(enabled);
				
				scale.setEnabled(enabled);
			}
		};
	}
	
}
