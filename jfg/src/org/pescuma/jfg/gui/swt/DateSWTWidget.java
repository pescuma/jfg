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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.pescuma.jfg.Attribute;

class DateSWTWidget extends AbstractLabelControlSWTWidget
{
	private final boolean showDate;
	private final boolean showTime;
	private DateTime date;
	private DateTime time;
	private Color background;
	
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
			Composite tmp = new Composite(parent, 0);
			tmp.setLayout(SWTUtils.createBorderlessGridLayout(2, false));
			
			date = new DateTime(tmp, SWT.DATE | (attrib.canWrite() ? 0 : SWT.READ_ONLY));
			date.addListener(SWT.Selection, getModifyListener());
			date.addListener(SWT.Dispose, getDisposeListener());
			date.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			time = new DateTime(tmp, SWT.TIME | (attrib.canWrite() ? 0 : SWT.READ_ONLY));
			time.addListener(SWT.Selection, getModifyListener());
			time.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			background = date.getBackground();
			
			return tmp;
		}
		else if (showDate)
		{
			date = new DateTime(parent, SWT.DATE | (attrib.canWrite() ? 0 : SWT.READ_ONLY));
			date.addListener(SWT.Selection, getModifyListener());
			date.addListener(SWT.Dispose, getDisposeListener());
			
			background = date.getBackground();
			
			return date;
		}
		else
		{
			time = new DateTime(parent, SWT.TIME | (attrib.canWrite() ? 0 : SWT.READ_ONLY));
			time.addListener(SWT.Selection, getModifyListener());
			time.addListener(SWT.Dispose, getDisposeListener());
			
			background = date.getBackground();
			
			return time;
		}
	}
	
	public Object getValue()
	{
		if (attrib.canWrite())
		{
			Calendar calendar = Calendar.getInstance();
			
			if (date != null && time != null)
				calendar.set(date.getYear(), date.getMonth(), date.getDay(), time.getHours(), time.getMinutes(),
						time.getSeconds());
			else if (date != null)
				calendar.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
			else
				calendar.set(1970, 0, 1, time.getHours(), time.getMinutes(), time.getSeconds());
			
			if (attrib.getType() == Calendar.class)
				return calendar;
			else
				return calendar.getTime();
		}
		else
			return attrib.getValue();
	}
	
	public void setValue(Object value)
	{
		Calendar calendar = toCalendar(value, false);
		
		if (date != null)
		{
			date.setYear(calendar.get(Calendar.YEAR));
			date.setMonth(calendar.get(Calendar.MONTH));
			date.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		}
		
		if (time != null)
		{
			time.setYear(calendar.get(Calendar.HOUR));
			time.setMonth(calendar.get(Calendar.MINUTE));
			time.setDay(calendar.get(Calendar.SECOND));
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
	protected void markFieldAsUncommited()
	{
		super.markFieldAsUncommited();
		
		if (date != null)
			date.setBackground(data.createBackgroundColor(date, background));
		if (time != null)
			time.setBackground(data.createBackgroundColor(date, background));
	}
	
	@Override
	protected void unmarkFieldAsUncommited()
	{
		super.unmarkFieldAsUncommited();
		
		if (date != null)
			date.setBackground(background);
		if (time != null)
			time.setBackground(background);
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