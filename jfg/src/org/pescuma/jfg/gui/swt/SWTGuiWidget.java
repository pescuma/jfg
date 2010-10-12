package org.pescuma.jfg.gui.swt;

import org.eclipse.swt.events.DisposeListener;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiWidget;

public interface SWTGuiWidget extends GuiWidget
{
	void init(SWTLayoutBuilder layout, InnerBuilder innerBuilder, GuiCopyManager manager);
	
	void addDisposeListener(DisposeListener listener);
	
	int getDefaultLayoutHint();
}
