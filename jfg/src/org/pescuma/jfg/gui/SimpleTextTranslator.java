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

package org.pescuma.jfg.gui;

import static org.pescuma.jfg.StringUtils.*;

public class SimpleTextTranslator implements TextTranslator
{
	public String fieldName(String fieldName)
	{
		return formatText(fieldName);
	}
	
	public String enumElement(String enumElement)
	{
		return formatText(enumElement);
	}
	
	public String groupName(String groupName)
	{
		return formatText(groupName);
	}
	
	private String formatText(String text)
	{
		int index = text.lastIndexOf('.');
		if (index >= 0)
			text = text.substring(index + 1);
		
		text = text.replace("_", " - ");
		text = text.replaceAll("([A-Z]+)", " $1");
		text = text.replaceAll("([A-Z]+)([A-Z][^ A-Z])", "$1 $2");
		text = text.replaceAll("^( -)+", "");
		text = text.replaceAll("(- )+$", "");
		text = text.replaceAll(" +", " ");
		return firstUpper(text.trim());
	}
	
	public String translate(String... text)
	{
		if (text.length == 2)
		{
			if (text[0].equals("Add"))
				return "Add another";
			else if (text[0].equals("Remove"))
				return "";
		}
		return text[0];
	}
	
}
