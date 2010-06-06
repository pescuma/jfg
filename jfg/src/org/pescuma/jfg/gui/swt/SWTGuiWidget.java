package org.pescuma.jfg.gui.swt;

import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiWidget;

public interface SWTGuiWidget extends GuiWidget
{
	void init(SWTLayoutBuilder layout, GuiCopyManager manager);
}
