package jfg.gui.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jfg.Attribute;
import jfg.gui.GuiWidget;
import jfg.gui.GuiWidgetList;

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
