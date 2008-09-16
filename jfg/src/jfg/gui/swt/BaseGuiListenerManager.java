package jfg.gui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jfg.gui.GuiUpdateListener;
import jfg.gui.GuiWidget;
import jfg.gui.GuiWidgetList;

class BaseGuiListenerManager
{
	private Map<String, List<GuiUpdateListener>> listeners = new HashMap<String, List<GuiUpdateListener>>();
	
	public void addListener(String id, GuiUpdateListener listener)
	{
		List<GuiUpdateListener> list = listeners.get(id);
		if (list == null)
		{
			list = new ArrayList<GuiUpdateListener>();
			listeners.put(id, list);
		}
		
		list.add(listener);
	}
	
	public void removeListener(String id, GuiUpdateListener listener)
	{
		List<GuiUpdateListener> list = listeners.get(id);
		if (list == null)
			return;
		
		list.remove(listener);
	}
	
	public void notifyChange(String id, GuiWidget widget, GuiWidgetList widgets)
	{
		List<GuiUpdateListener> list = listeners.get(id);
		if (list != null)
			for (GuiUpdateListener listener : list)
				listener.onGuiUpdated(widget, widgets);
		
		list = listeners.get(null);
		if (list != null)
			for (GuiUpdateListener listener : list)
				listener.onGuiUpdated(widget, widgets);
	}
	
}
