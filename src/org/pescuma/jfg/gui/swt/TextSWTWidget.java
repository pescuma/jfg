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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.TextBasedGuiWidget;
import org.pescuma.jfg.gui.WidgetFormater;
import org.pescuma.jfg.gui.WidgetFormater.TextAndPos;

abstract class TextSWTWidget extends AbstractLabelControlSWTWidget implements TextBasedGuiWidget
{
	private Text text;
	private Color background;
	private Listener formaterListener;
	private WidgetFormater formater;
	private boolean ignoreFormat = false;
	
	TextSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		text = data.componentFactory.createText(parent, (attrib.canWrite() ? SWT.NONE : SWT.READ_ONLY)
				| getAdditionalTextStyle());
		text.addListener(SWT.Modify, getModifyListener());
		text.addListener(SWT.Dispose, getDisposeListener());
		
		if (attrib.canWrite())
		{
			addValidation(text, getType(attrib.getType()));
			setTextLimit(attrib, text);
		}
		
		background = text.getBackground();
		
		return text;
	}
	
	@Override
	public Object getValue()
	{
		return convertToObject(text.getText(), getType(attrib.getType()), canBeNull());
	}
	
	@Override
	public void setValue(Object value)
	{
		int caretPosition = text.getCaretPosition();
		text.setText(convertToString(text, value, getType(attrib.getType())));
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
	
	protected abstract int getAdditionalTextStyle();
	
	protected abstract void addValidation(Text text, Object type);
	
	protected abstract String convertToString(Text text, Object value, Object type);
	
	protected abstract Object convertToObject(String value, Object type, boolean canBeNull);
	
	protected abstract Object getType(Object type);
	
	protected abstract void setTextLimit(Attribute attrib, Text text);
	
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