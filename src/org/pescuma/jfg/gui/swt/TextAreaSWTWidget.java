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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.gui.TextBasedGuiWidget;
import org.pescuma.jfg.gui.WidgetFormater;
import org.pescuma.jfg.gui.WidgetFormater.TextAndPos;

class TextAreaSWTWidget extends AbstractControlSWTWidget implements TextBasedGuiWidget
{
	private Text text;
	private Color background;
	private Listener formaterListener;
	private WidgetFormater formater;
	private boolean ignoreFormat = false;
	
	TextAreaSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		String description = data.textTranslator.fieldName(attrib.getName());
		
		Group group = null;
		if (!description.isEmpty() && showLabel())
		{
			group = data.componentFactory.createGroup(parent, SWT.NONE);
			group.setText(description);
			group.setLayout(new GridLayout(1, false));
		}
		
		text = data.componentFactory.createText(group == null ? parent : group, (attrib.canWrite() ? SWT.NONE
				: SWT.READ_ONLY) | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		text.addListener(SWT.Modify, getModifyListener());
		text.addListener(SWT.Dispose, getDisposeListener());
		
		if (group != null)
			text.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (attrib.canWrite())
			setTextLimit(attrib, text);
		
		background = text.getBackground();
		
		return group == null ? text : group;
	}
	
	@Override
	protected int getDefaultHeightHint()
	{
		return 70;
	}
	
	@Override
	public Object getValue()
	{
		return convertToObject(text.getText());
	}
	
	@Override
	public void setValue(Object value)
	{
		int caretPosition = text.getCaretPosition();
		text.setText(convertToString(value));
		text.setSelection(caretPosition);
	}
	
	@Override
	protected void updateColor()
	{
		text.setBackground(createColor(text, background));
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		text.setEnabled(enabled);
	}
	
	private String convertToString(Object value)
	{
		if (value == null)
			return "";
		else
			return value.toString();
	}
	
	private Object convertToObject(String value)
	{
		if (value == null && !canBeNull())
			return "";
		return value;
	}
	
	private void setTextLimit(Attribute attrib, Text text)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return;
		
		Object max = range.getMax();
		if (max != null && (max instanceof Number))
			text.setTextLimit(((Number) max).intValue());
	}
	
	@Override
	public void setShadowText(String shadowText)
	{
		this.text.setMessage(shadowText == null ? "" : shadowText);
	}
	
	@Override
	public void setFormater(final WidgetFormater formater)
	{
		if (!attrib.canWrite())
			return;
		
		if (formaterListener != null)
		{
			text.removeListener(SWT.Modify, formaterListener);
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
			text.addListener(SWT.Modify, formaterListener);
			
			formatText();
		}
	}
	
	private void formatText()
	{
		if (formater == null || ignoreFormat)
			return;
		
		TextAndPos actual = new TextAndPos();
		actual.text = text.getText();
		actual.selectionStart = text.getSelection().x;
		actual.selectionEnd = text.getSelection().y;
		
		TextAndPos formated = formater.format(attrib, actual);
		
		ignoreFormat = true;
		
		boolean changedText = !formated.text.equals(actual.text);
		if (changedText)
			text.setText(formated.text);
		
		if (changedText || formated.selectionStart != actual.selectionStart
				|| formated.selectionEnd != actual.selectionEnd)
			text.setSelection(new Point(formated.selectionStart, formated.selectionEnd));
		
		ignoreFormat = false;
	}
}