package org.pescuma.jfg.gui.swt;

public class JfgHelper
{
	public static String attribute(Class<?> cls, String fieldName)
	{
		return cls.getName() + "." + fieldName;
	}
	
	public static String attribute(Class<?> cls, String fieldName, String subitem)
	{
		return cls.getName() + "." + fieldName + "#" + subitem;
	}
}
