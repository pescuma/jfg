package jfg.gui.swt;

public class DontUpdateSWTCopyManager extends AbstractSWTCopyManager
{
	public DontUpdateSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
	}
	
	public void guiChanged(SWTAttribute attrib)
	{
	}
}
