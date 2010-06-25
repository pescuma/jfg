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

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;

public class SWTComboBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return false;
		
		Collection<?> values = range.getPossibleValues();
		return values != null && values.size() > 0;
	}
	
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
		return new AbstractLabelWidgetSWTWidget(attrib, data) {
			
			private Combo combo;
			private Text text;
			private Color background;
			
			@Override
			protected Control createWidget(Composite parent)
			{
				Control ret;
				if (attrib.canWrite())
				{
					combo = data.componentFactory.createCombo(parent, SWT.READ_ONLY);
					fill();
					combo.addListener(SWT.Modify, getModifyListener());
					combo.addListener(SWT.Dispose, getDisposeListener());
					
					ret = combo;
				}
				else
				{
					text = data.componentFactory.createText(parent, SWT.READ_ONLY);
					text.addListener(SWT.Dispose, getDisposeListener());
					
					ret = text;
				}
				
				background = ret.getBackground();
				
				return ret;
			}
			
			private void fill()
			{
				if (canBeNull())
					combo.add(data.textTranslator.translate("ComboWidget:None"));
				
				for (Object object : getPossibleValues())
					combo.add(convertToString(object, attrib.getType()));
			}
			
			public Object getValue()
			{
				if (attrib.canWrite())
					return getObject(combo.getSelectionIndex());
				else
					return attrib.getValue();
			}
			
			private Object getObject(int index)
			{
				int i = 0;
				
				if (canBeNull())
				{
					if (i == index)
						return null;
					i++;
				}
				
				for (Object object : getPossibleValues())
				{
					if (i == index)
						return object;
					i++;
				}
				
				throw new IllegalArgumentException();
			}
			
			public void setValue(Object value)
			{
				if (attrib.canWrite())
					combo.select(getIndex(value));
				else
					text.setText(convertToString(value, attrib.getType()));
			}
			
			private int getIndex(Object value)
			{
				int i = 0;
				
				if (canBeNull())
				{
					if (value == null)
						return i;
					i++;
				}
				
				for (Object object : getPossibleValues())
				{
					if (object == value)
						return i;
					i++;
				}
				
				throw new IllegalArgumentException();
			}
			
			private String convertToString(Object value, Object type)
			{
				if (value == null)
					return data.textTranslator.translate("ComboWidget:null");
				
				if (type instanceof Class<?>)
				{
					Class<?> cls = (Class<?>) type;
					if (cls.isEnum())
						return data.textTranslator.enumElement(cls.getName() + "." + ((Enum<?>) value).name());
				}
				
				return value.toString();
			}
			
			private Collection<?> getPossibleValues()
			{
				return attrib.getValueRange().getPossibleValues();
			}
			
			private boolean canBeNull()
			{
				return attrib.getValueRange().canBeNull();
			}
			
			@Override
			protected void markField()
			{
				super.markField();
				
				if (attrib.canWrite())
				{
					combo.setBackground(data.createBackgroundColor(combo, background));
				}
				else
				{
					text.setBackground(data.createBackgroundColor(text, background));
				}
			}
			
			@Override
			protected void unmarkField()
			{
				super.unmarkField();
				
				if (attrib.canWrite())
				{
					combo.setBackground(background);
				}
				else
				{
					text.setBackground(background);
				}
			}
			
			@Override
			public void setEnabled(boolean enabled)
			{
				super.setEnabled(enabled);
				
				if (attrib.canWrite())
				{
					combo.setEnabled(enabled);
				}
				else
				{
					text.setEnabled(enabled);
				}
			}
		};
	}
}
