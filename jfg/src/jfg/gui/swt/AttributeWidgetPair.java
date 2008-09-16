package jfg.gui.swt;

import jfg.Attribute;
import jfg.gui.GuiWidget;

class AttributeWidgetPair
{
	final Attribute attrib;
	final GuiWidget widget;
	
	public AttributeWidgetPair(Attribute attrib, GuiWidget widget)
	{
		this.attrib = attrib;
		this.widget = widget;
	}
}
