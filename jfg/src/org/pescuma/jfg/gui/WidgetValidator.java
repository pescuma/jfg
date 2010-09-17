package org.pescuma.jfg.gui;

import org.pescuma.jfg.Attribute;

public interface WidgetValidator
{
	boolean isValid(Attribute attrib, Object value);
}
