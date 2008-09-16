package jfg.gui;

import jfg.Attribute;

public interface GuiWidgetList
{
	GuiWidget getWidget(Attribute attribute);
	
	GuiWidget getWidget(String attributeName);
}
