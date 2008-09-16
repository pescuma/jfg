package jfg.gui;

public interface GuiCopyManager
{
	/** Model value changed and without a gui update happening */
	void modelChanged(GuiWidget widget);
	
	/** Gui value changed and without a model update happening */
	void guiChanged(GuiWidget widget);
	
	/** Any gui set happened */
	void guiUpdated(GuiWidget widget);
}
