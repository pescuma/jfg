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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;

class DateSWTWidget extends AbstractLabelControlSWTWidget
{
	private final boolean showDate;
	private final boolean showTime;
	private CalendarCombo date;
	private DateTime time;
	private Color dateBackground;
	private Color timeBackground;
	
	DateSWTWidget(Attribute attrib, JfgFormData data, boolean showDate, boolean showTime)
	{
		super(attrib, data);
		
		this.showDate = showDate;
		this.showTime = showTime;
		
		if (!showDate && !showTime)
			throw new IllegalArgumentException();
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		if (showTime && showDate)
		{
			Composite tmp = data.componentFactory.createComposite(parent, SWT.NONE);
			tmp.setLayout(SWTUtils.createBorderlessGridLayout(2, false));
			
			createDate(tmp);
			date.addListener(SWT.Dispose, getDisposeListener());
			date.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			createTime(tmp);
			time.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			return tmp;
		}
		else if (showDate)
		{
			createDate(parent);
			date.addListener(SWT.Dispose, getDisposeListener());
			
			return date;
		}
		else
		{
			createTime(parent);
			time.addListener(SWT.Dispose, getDisposeListener());
			
			return time;
		}
	}
	
	private void createDate(Composite parent)
	{
		final boolean canBeNull = canBeNull();
		
		date = data.componentFactory.createCalendarCombo(parent, (attrib.canWrite() ? 0 : SWT.READ_ONLY),
				new DefaultSettings() {
					@Override
					public boolean keyboardNavigatesCalendar()
					{
						return false;
					}
					
					@Override
					public boolean allowEmptyDate()
					{
						return canBeNull;
					}
				});
		date.addCalendarListener(new CalendarListenerAdapter() {
			@Override
			public void dateChanged(Calendar date)
			{
				onWidgetModify();
			}
		});
		
		AttributeValueRange range = attrib.getValueRange();
		if (range != null)
		{
			date.setDisallowBeforeDate(toCalendar(range.getMin(), true));
			date.setDisallowAfterDate(toCalendar(range.getMax(), true));
		}
		
		dateBackground = date.getBackground();
	}
	
	private void createTime(Composite parent)
	{
		time = data.componentFactory.createDateTime(parent, SWT.TIME | (attrib.canWrite() ? 0 : SWT.READ_ONLY));
		time.addListener(SWT.Selection, getModifyListener());
		
		timeBackground = time.getBackground();
	}
	
	@Override
	public Object getValue()
	{
		if (attrib.canWrite())
		{
			if (canBeNull())
			{
				if (date != null && date.getDate() == null)
					return null;
			}
			
			Calendar calendar;
			if (date != null)
			{
				calendar = date.getDate();
			}
			else
			{
				calendar = Calendar.getInstance();
				calendar.set(1970, 0, 1, 0, 0, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
			
			if (time != null)
			{
				calendar.set(Calendar.HOUR, time.getHours());
				calendar.set(Calendar.MINUTE, time.getMinutes());
				calendar.set(Calendar.SECOND, time.getSeconds());
			}
			
			if (attrib.getType() == Calendar.class)
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
		Calendar calendar = toCalendar(value, canBeNull());
		
		if (date != null)
		{
			date.setDate(calendar);
		}
		
		if (time != null)
		{
			if (calendar != null)
			{
				time.setHours(calendar.get(Calendar.HOUR));
				time.setMinutes(calendar.get(Calendar.MINUTE));
				time.setSeconds(calendar.get(Calendar.SECOND));
			}
			else
			{
				time.setHours(0);
				time.setMinutes(0);
				time.setSeconds(0);
			}
		}
	}
	
	private Calendar toCalendar(Object value, boolean canBeNull)
	{
		if (value == null)
		{
			if (!canBeNull)
				return Calendar.getInstance();
			else
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
	protected void updateColor()
	{
		if (date != null)
			date.setBackground(createColor(date, dateBackground));
		if (time != null)
			time.setBackground(createColor(date, timeBackground));
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		if (date != null)
			date.setEnabled(enabled);
		if (time != null)
			time.setEnabled(enabled);
	}
	
	@Override
	public int getDefaultLayoutHint()
	{
		return JfgFormData.HORIZONTAL_SHRINK;
	}
}