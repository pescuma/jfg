package jfg.gui.swt;

public class FastSWTCopyManager extends AbstractSWTCopyManager
{
	public FastSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
	}
	
	public void guiChanged(SWTAttribute attrib)
	{
		attrib.copyToModel();
	}
}
