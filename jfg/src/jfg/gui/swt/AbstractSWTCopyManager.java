package jfg.gui.swt;

import org.eclipse.swt.widgets.Display;

public abstract class AbstractSWTCopyManager implements SWTCopyManager
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
	
	public void modelChanged(SWTAttribute attrib)
	{
		if (data.updateGuiWhenModelChanges)
			attrib.copyToGUI();
	}
}
