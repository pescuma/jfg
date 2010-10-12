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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pescuma.jfg.gui.GuiUpdateListener;
import org.pescuma.jfg.gui.GuiWidget;

class BaseGuiListenerManager
{
	private Map<String, List<GuiUpdateListener>> listeners = new HashMap<String, List<GuiUpdateListener>>();
	
	public void addListener(String attributeName, GuiUpdateListener listener)
	{
		List<GuiUpdateListener> list = listeners.get(attributeName);
		if (list == null)
		{
			list = new ArrayList<GuiUpdateListener>();
			listeners.put(attributeName, list);
		}
		
		list.add(listener);
	}
	
	public void removeListener(String attributeName, GuiUpdateListener listener)
	{
		List<GuiUpdateListener> list = listeners.get(attributeName);
		if (list == null)
			return;
		
		list.remove(listener);
	}
	
	public void notifyChange(String attributeName, GuiWidget widget)
	{
		List<GuiUpdateListener> list = listeners.get(attributeName);
		if (list != null)
			for (GuiUpdateListener listener : list)
				listener.onGuiUpdated(widget);
		
		list = listeners.get(null);
		if (list != null)
			for (GuiUpdateListener listener : list)
				listener.onGuiUpdated(widget);
	}
	
}
