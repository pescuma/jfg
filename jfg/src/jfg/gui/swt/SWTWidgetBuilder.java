package jfg.gui.swt;

import jfg.Attribute;
import jfg.gui.GuiWidget;

import org.eclipse.swt.widgets.Composite;

public interface SWTWidgetBuilder
{
	boolean accept(Attribute attrib);
	
	GuiWidget build(Composite parent, Attribute attrib, JfgFormData data);
}
