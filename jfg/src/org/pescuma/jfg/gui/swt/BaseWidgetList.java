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
import java.util.Collection;
import java.util.Iterator;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.GuiWidgetList;


class BaseWidgetList implements GuiWidgetList, Iterable<AttributeWidgetPair>
{
	private final Collection<AttributeWidgetPair> widgets = new ArrayList<AttributeWidgetPair>();
	
	public boolean add(Attribute attrib, GuiWidget widget)
	{
		return widgets.add(new AttributeWidgetPair(attrib, widget));
	}
	
	public void clear()
	{
		widgets.clear();
	}
	
	public boolean isEmpty()
	{
		return widgets.isEmpty();
	}
	
	public boolean remove(GuiWidget o)
	{
		return widgets.remove(o);
	}
	
	public int size()
	{
		return widgets.size();
	}
	
	public Iterator<AttributeWidgetPair> iterator()
	{
		return widgets.iterator();
	}
	
	public GuiWidget getWidget(Attribute attribute)
	{
		for (AttributeWidgetPair aw : widgets)
		{
			if (aw.attrib == attribute)
				return aw.widget;
		}
		return null;
	}
	
	public GuiWidget getWidget(String attributeName)
	{
		for (AttributeWidgetPair aw : widgets)
		{
			if (aw.attrib.getName().equals(attributeName))
				return aw.widget;
		}
		return null;
	}
	
	public Attribute getAttribute(GuiWidget widget)
	{
		for (AttributeWidgetPair aw : widgets)
		{
			if (aw.widget == widget)
				return aw.attrib;
		}
		return null;
	}
}
