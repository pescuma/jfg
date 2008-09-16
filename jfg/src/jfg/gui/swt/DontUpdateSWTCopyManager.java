package jfg.gui.swt;

import jfg.gui.GuiWidget;

public class DontUpdateSWTCopyManager extends AbstractSWTCopyManager
{
	public DontUpdateSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
	}
	
	public void guiChanged(GuiWidget attrib)
	{
	}
}
