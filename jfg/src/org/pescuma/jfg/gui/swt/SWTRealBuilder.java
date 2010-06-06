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

import static org.pescuma.jfg.gui.swt.TypeUtils.*;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;

public class SWTRealBuilder extends SWTTextBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return typeIsReal(type) || "real".equals(type);
	}
	
	@Override
	protected void addValidation(final Text text, final Object type)
	{
		text.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e)
			{
				if (e.character != 0 && Character.isISOControl(e.character))
					return;
				
				String append = e.text;
				String txt = text.getText();
				txt = txt.substring(0, e.start) + append + txt.substring(e.end);
				
				if (!isValidNumber(txt))
				{
					e.doit = false;
					return;
				}
				
				if ("-".equals(txt))
					return;
				
				try
				{
					parseDouble(txt, type);
				}
				catch (NumberFormatException ex)
				{
					e.doit = false;
					return;
				}
			}
		});
	}
	
	@Override
	protected String convertToString(Text text, Object value, Object type)
	{
		if (value == null)
			return "";
		
		String oldText = text.getText();
		double o = parseDouble(oldText, type);
		double v = asDouble(value);
		
		if (o == v)
			return oldText;
		else
		{
			NumberFormat formater = NumberFormat.getNumberInstance();
			formater.setGroupingUsed(false);
			return formater.format(v);
		}
	}
	
	@Override
	protected Object convertToObject(String value, Object type, boolean canBeNull)
	{
		try
		{
			return valueOf(value, type, canBeNull ? null : "0");
		}
		catch (NumberFormatException e)
		{
			return valueOf(null, type, canBeNull ? null : "0");
		}
	}
	
	static boolean isValidNumber(String append)
	{
		char sep = new DecimalFormatSymbols().getDecimalSeparator();
		boolean hasSeparator = false;
		for (int i = 0; i < append.length(); i++)
		{
			char c = append.charAt(i);
			
			if (i == 0 && c == '-')
				continue;
			
			if (c == sep && !hasSeparator)
			{
				hasSeparator = true;
				continue;
			}
			
			if ('0' <= c && c <= '9')
				continue;
			
			return false;
		}
		return true;
	}
	
	@Override
	protected Object getType(Object type)
	{
		if ("real".equals(type))
			return float.class;
		return type;
	}
	
	@Override
	protected void setTextLimit(Attribute attrib, Text text)
	{
	}
}
