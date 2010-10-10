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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.ReferenceListGuiWidget;
import org.pescuma.jfg.gui.swt.JfgFormData.FieldConfig;

class ObjectComboSWTWidget extends AbstractLabelControlSWTWidget implements ReferenceListGuiWidget
{
	private Combo combo;
	private Text text;
	private Color background;
	private final List<Object> options = new ArrayList<Object>();
	private DescriptionGetter toDescription;
	private List<Object> comboObjects;
	
	ObjectComboSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
		
		FieldConfig config = data.fieldsConfig.get(attrib.getName());
		if (config != null && config.widgetData != null)
		{
			ReferenceListGuiWidget.Data widgetData = (ReferenceListGuiWidget.Data) config.widgetData;
			setObjects(widgetData.objects, widgetData.toDescription);
		}
	}
	
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
		combo.removeAll();
		
		comboObjects = new ArrayList<Object>();
		
		if (canBeNull())
		{
			comboObjects.add(null);
			combo.add(data.textTranslator.translate("ComboWidget:None"));
		}
		
		for (Object obj : options)
		{
			comboObjects.add(obj);
			combo.add(toDescription.getDescription(obj));
		}
	}
	
	@Override
	public Object getValue()
	{
		if (attrib.canWrite())
		{
			int index = combo.getSelectionIndex();
			if (index < 0)
				return fixNull(null);
			
			return comboObjects.get(index);
		}
		else
			return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		if (attrib.canWrite())
		{
			value = fixNull(value);
			combo.select(getIndex(value));
		}
		else
		{
			text.setText(convertToString(value, attrib.getType()));
		}
	}
	
	private Object fixNull(Object value)
	{
		if (!canBeNull() && comboObjects.size() > 0)
			return comboObjects.get(0);
		else
			return null;
	}
	
	private int getIndex(Object value)
	{
		int i = 0;
		for (Object obj : comboObjects)
		{
			if (obj == value)
				return i;
			++i;
		}
		
		if (value == null)
			return -1;
		
		throw new IllegalArgumentException();
	}
	
	private String convertToString(Object value, Object type)
	{
		if (value == null)
			return data.textTranslator.translate("ComboWidget:None");
		
		if (type instanceof Class<?>)
		{
			Class<?> cls = (Class<?>) type;
			if (cls.isEnum())
				return data.textTranslator.enumElement(cls.getName() + "." + ((Enum<?>) value).name());
		}
		
		return value.toString();
	}
	
	@Override
	protected void markFieldAsUncommited()
	{
		super.markFieldAsUncommited();
		
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
	protected void unmarkFieldAsUncommited()
	{
		super.unmarkFieldAsUncommited();
		
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
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setObjects(List objects, DescriptionGetter toDescription)
	{
		this.options.clear();
		this.options.addAll(objects);
		this.toDescription = toDescription;
		
		if (this.toDescription == null)
			this.toDescription = new DescriptionGetter() {
				@Override
				public String getDescription(Object obj)
				{
					return obj.toString();
				}
			};
		
		if (combo != null)
			fill();
	}
}