package org.pescuma.jfg.gui;

public interface GuiWidgetListener
{
	void onWidgetCreated(GuiWidget widget);
	
	void onWidgetUpdated(GuiWidget widget);
	
	void onWidgetDisposed(GuiWidget widget);
}
