package jfg.gui.swt;

import jfg.gui.GuiWidget;

public class FastSWTCopyManager extends AbstractSWTCopyManager
{
	public FastSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
	}
	
	public void guiChanged(GuiWidget widget)
	{
		widget.copyToModel();
	}
}
