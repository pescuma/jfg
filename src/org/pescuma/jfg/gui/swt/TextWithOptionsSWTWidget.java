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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.ReferenceGuiWidget;
import org.pescuma.jfg.gui.TextBasedGuiWidget;
import org.pescuma.jfg.gui.WidgetFormater;
import org.pescuma.jfg.gui.WidgetFormater.TextAndPos;
import org.pescuma.jfg.gui.swt.JfgFormData.FieldConfig;

class TextWithOptionsSWTWidget extends AbstractLabelControlSWTWidget implements ReferenceGuiWidget, TextBasedGuiWidget
{
	private Combo combo;
	private Text text;
	private Color background;
	private final List<Object> options = new ArrayList<Object>();
	private DescriptionGetter toDescription;
	private Listener formaterListener;
	private WidgetFormater formater;
	private boolean ignoreFormat = false;
	
	TextWithOptionsSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
		
		FieldConfig config = data.fieldsConfig.get(attrib.getName());
		if (config != null && config.widgetData != null)
		{
			ReferenceGuiWidget.Data widgetData = (ReferenceGuiWidget.Data) config.widgetData;
			setObjects(widgetData.objects, widgetData.toDescription);
		}
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		Control ret;
		if (attrib.canWrite())
		{
			combo = data.componentFactory.createCombo(parent, SWT.DROP_DOWN);
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
		for (Object obj : options)
			combo.add(toDescription.getDescription(obj));
	}
	
	@Override
	public Object getValue()
	{
		if (attrib.canWrite())
			return combo.getText();
		else
			return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		if (attrib.canWrite())
			combo.setText(convertToString(value, attrib.getType()));
		else
			text.setText(convertToString(value, attrib.getType()));
	}
	
	private String convertToString(Object value, Object type)
	{
		if (value == null)
			return "";
		
		if (type instanceof Class<?>)
		{
			Class<?> cls = (Class<?>) type;
			if (cls.isEnum())
				return data.textTranslator.enumElement(cls.getName() + "." + ((Enum<?>) value).name());
		}
		
		return value.toString();
	}
	
	@Override
	protected void updateColor()
	{
		if (attrib.canWrite())
			combo.setBackground(createColor(combo, background));
		else
			text.setBackground(createColor(text, background));
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		if (attrib.canWrite())
			combo.setEnabled(enabled);
		else
			text.setEnabled(enabled);
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
	
	@Override
	public void setShadowText(String shadowText)
	{
		if (attrib.canWrite())
		{
			// Combo does not support setMessage
		}
		else
		{
			text.setMessage(shadowText == null ? "" : shadowText);
		}
	}
	
	@Override
	public void setFormater(final WidgetFormater formater)
	{
		if (!attrib.canWrite())
			return;
		
		if (formaterListener != null)
		{
			combo.removeListener(SWT.Modify, formaterListener);
			formaterListener = null;
		}
		
		this.formater = formater;
		
		if (formater != null)
		{
			formaterListener = new Listener() {
				@Override
				public void handleEvent(Event event)
				{
					formatText();
				}
			};
			combo.addListener(SWT.Modify, formaterListener);
			
			formatText();
		}
	}
	
	private void formatText()
	{
		if (formater == null || ignoreFormat)
			return;
		
		TextAndPos actual = new TextAndPos();
		actual.text = combo.getText();
		actual.selectionStart = combo.getSelection().x;
		actual.selectionEnd = combo.getSelection().y;
		
		TextAndPos formated = formater.format(attrib, actual);
		
		ignoreFormat = true;
		
		boolean changedText = !formated.text.equals(actual.text);
		if (changedText)
			combo.setText(formated.text);
		
		if (changedText || formated.selectionStart != actual.selectionStart
				|| formated.selectionEnd != actual.selectionEnd)
			combo.setSelection(new Point(formated.selectionStart, formated.selectionEnd));
		
		ignoreFormat = false;
	}
}