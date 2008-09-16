package jfg.gui.swt;

import jfg.gui.GuiCopyManager;
import jfg.gui.GuiWidget;

import org.eclipse.swt.widgets.Display;

public abstract class AbstractSWTCopyManager implements GuiCopyManager
{
	protected final JfgFormComposite composite;
	protected final JfgFormData data;
	
	public AbstractSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		this.composite = composite;
		this.data = data;
	}
	
	protected Display getDisplay()
	{
		return composite.getDisplay();
	}
	
	public void modelChanged(GuiWidget widget)
	{
		if (data.updateGuiWhenModelChanges)
			widget.copyToGUI();
	}
	
	public void guiUpdated(GuiWidget widget)
	{
		composite.onGuiUpdated(widget);
	}
}
