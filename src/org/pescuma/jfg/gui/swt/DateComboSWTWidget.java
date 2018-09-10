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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.CalendarListenerAdapter;
import org.eclipse.nebula.widgets.calendarcombo.DefaultSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;

class DateComboSWTWidget extends AbstractLabelControlSWTWidget
{
	private CalendarCombo date;
	private Color background;
	
	public DateComboSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		date = new CalendarCombo(parent, (attrib.canWrite() ? 0 : SWT.READ_ONLY), new DefaultSettings() {
			@Override
			public boolean keyboardNavigatesCalendar()
			{
				return false;
			}
		});
		date.addListener(SWT.Dispose, getDisposeListener());
		
		date.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				System.out.println("date.addListener(SWT.Selection");
			}
		});
		date.addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				System.out.println("date.addListener(Dispose");
			}
		});
		
		date.addCalendarListener(new CalendarListenerAdapter() {
			@Override
			public void dateChanged(Calendar date)
			{
				System.out.println("dateChanged(Calendar date)");
				onWidgetModify();
			}
		});
		
		background = date.getBackground();
		
		return date;
	}
	
	@Override
	public Object getValue()
	{
		if (attrib.canWrite())
		{
			Calendar calendar = date.getDate();
			
			if (calendar == null)
				return null;
			else if (attrib.getType() == Calendar.class)
				return calendar;
			else
				return calendar.getTime();
		}
		else
			return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		date.setDate(toCalendar(value));
	}
	
	private Calendar toCalendar(Object value)
	{
		if (value == null)
		{
			return null;
		}
		else if (value instanceof Calendar)
		{
			return (Calendar) value;
		}
		else if (value instanceof Date)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date) value);
			return calendar;
		}
		else
			throw new IllegalArgumentException();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		date.setEnabled(enabled);
	}
	
	@Override
	protected void updateColor()
	{
		super.updateColor();
		
		date.setBackground(createColor(date, background));
	}
	
	@Override
	public int getDefaultLayoutHint()
	{
		return JfgFormData.HORIZONTAL_SHRINK;
	}
	
}
