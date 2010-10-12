package org.pescuma.jfg.gui.swt;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.pescuma.jfg.gui.GuiWidget;

class ChildManipulationLogic
{
	private final GuiWidget widget;
	
	public ChildManipulationLogic(GuiWidget widget)
	{
		this.widget = widget;
	}
	
	private Collection<GuiWidget> getChildren()
	{
		return widget.getChildren();
	}
	
	public GuiWidget getChild(String attributeName)
	{
		Collection<GuiWidget> ret = getChildren(attributeName);
		
		if (ret == null)
			return null;
		
		if (ret.size() != 1)
			throw new IllegalStateException("More than one attribute with same name found. You must call getChildren");
		
		return ret.iterator().next();
	}
	
	public Collection<GuiWidget> getChildren(String attributeName)
	{
		List<GuiWidget> ret = new LinkedList<GuiWidget>();
		
		for (GuiWidget widget : getChildren())
		{
			if (attributeName.equals(widget.getAttribute().getName()))
				ret.add(widget);
		}
		
		if (ret.size() < 1)
			return null;
		else
			return Collections.unmodifiableCollection(ret);
	}
	
	public GuiWidget findChild(String attributeName)
	{
		Collection<GuiWidget> ret = findChildren(attributeName);
		
		if (ret == null)
			return null;
		
		if (ret.size() != 1)
			throw new IllegalStateException("More than one attribute with same name found. You must call findChildren");
		
		return ret.iterator().next();
	}
	
	public Collection<GuiWidget> findChildren(String attributeName)
	{
		List<GuiWidget> ret = new LinkedList<GuiWidget>();
		
		Collection<GuiWidget> children = getChildren(attributeName);
		if (children != null)
			ret.addAll(children);
		
		for (GuiWidget widget : getChildren())
		{
			children = widget.findChildren(attributeName);
			if (children != null)
				ret.addAll(children);
		}
		
		if (ret.size() < 1)
			return null;
		else
			return Collections.unmodifiableCollection(ret);
	}
	
	public Collection<GuiWidget> findAllLeafWidgets()
	{
		List<GuiWidget> ret = new LinkedList<GuiWidget>();
		addAllLeafWidgets(ret, getChildren());
		return Collections.unmodifiableCollection(ret);
	}
	
	private void addAllLeafWidgets(List<GuiWidget> ret, Collection<GuiWidget> widgets)
	{
		for (GuiWidget widget : widgets)
		{
			Collection<GuiWidget> cs = widget.getChildren();
			if (cs.size() < 1)
				ret.add(widget);
			else
				addAllLeafWidgets(ret, cs);
		}
	}
	
	public Collection<GuiWidget> findAllWidgets()
	{
		List<GuiWidget> ret = new LinkedList<GuiWidget>();
		addAllWidgets(ret, widget);
		return Collections.unmodifiableCollection(ret);
	}
	
	private void addAllWidgets(List<GuiWidget> ret, GuiWidget widget)
	{
		ret.add(widget);
		
		for (GuiWidget w : widget.getChildren())
			addAllWidgets(ret, w);
	}
	
}
