package org.pescuma.jfg.gui.swt;

import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiWidget;

public interface SWTGuiWidget extends GuiWidget
{
	void init(SWTLayoutBuilder layout, InnerBuilder innerBuilder, GuiCopyManager manager);
	
	int getDefaultLayoutHint();
	
	/** Called by JfgFormComposite when the time for the notification comes */
	void notifyCreation();
	
	/** Called by JfgFormComposite when the time for the notification comes */
	void notifyUpdate();
}
