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

import static org.pescuma.jfg.gui.swt.JfgFormData.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;

class TextAreaSWTWidget extends AbstractControlSWTWidget
{
	private Text text;
	private Color background;
	
	TextAreaSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		Group group = null;
		if (attrib.getName() != null)
		{
			group = data.componentFactory.createGroup(parent, SWT.NONE);
			group.setText(data.textTranslator.fieldName(attrib.getName()));
			group.setLayout(new GridLayout(1, false));
		}
		
		text = data.componentFactory.createText(group == null ? parent : group, (attrib.canWrite() ? SWT.NONE
				: SWT.READ_ONLY)
				| SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		text.addListener(SWT.Modify, getModifyListener());
		text.addListener(SWT.Dispose, getDisposeListener());
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.minimumHeight = 70;
		text.setLayoutData(data);
		
		if (attrib.canWrite())
			setTextLimit(attrib, text);
		
		background = text.getBackground();
		
		return group == null ? text : group;
	}
	
	@Override
	public int getDefaultLayoutHint()
	{
		return VERTICAL_FILL;
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
	protected void markFieldAsUncommited()
	{
		super.markFieldAsUncommited();
		
		text.setBackground(data.createBackgroundColor(text, background));
	}
	
	@Override
	protected void unmarkFieldAsUncommited()
	{
		super.unmarkFieldAsUncommited();
		
		text.setBackground(background);
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
}