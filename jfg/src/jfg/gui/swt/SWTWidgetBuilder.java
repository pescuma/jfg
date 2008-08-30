package jfg.gui.swt;

import jfg.Attribute;

import org.eclipse.swt.widgets.Composite;

public interface SWTWidgetBuilder
{
	boolean accept(Attribute attrib);
	
	boolean wantNameLabel();
	
	SWTAttribute build(Composite parent, Attribute attrib, JfgFormData data);
}
