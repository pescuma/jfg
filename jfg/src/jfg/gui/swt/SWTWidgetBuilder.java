package jfg.gui.swt;

import jfg.Attribute;

import org.eclipse.swt.widgets.Composite;

public interface SWTWidgetBuilder
{
	boolean acceptType(Object type);
	
	boolean wantNameLabel();
	
	SWTAttribute build(Composite parent, Attribute attrib, JfgFormData data);
}
