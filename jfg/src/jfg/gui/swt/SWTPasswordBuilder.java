package jfg.gui.swt;

import jfg.Attribute;

import org.eclipse.swt.SWT;

public class SWTPasswordBuilder extends SWTTextBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return (type == String.class) || "password".equals(type);
	}
	
	@Override
	protected int getAdditionalTextStyle()
	{
		return SWT.PASSWORD;
	}
}
