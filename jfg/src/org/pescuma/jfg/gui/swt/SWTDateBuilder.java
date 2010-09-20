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

import java.util.Calendar;
import java.util.Date;

import org.pescuma.jfg.Attribute;

public class SWTDateBuilder implements SWTWidgetBuilder
{
	private final boolean showDate;
	private final boolean showTime;
	
	public SWTDateBuilder(boolean showDate, boolean showTime)
	{
		this.showDate = showDate;
		this.showTime = showTime;
	}
	
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		
		if (showDate && !showTime && "date".equals(type))
			return true;
		if (!showDate && showTime && "time".equals(type))
			return true;
		if (showDate && showTime && "datetime".equals(type))
			return true;
		
		return type == Date.class || type == Calendar.class;
	}
	
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
		return new DateSWTWidget(attrib, data, showDate, showTime);
	}
}
