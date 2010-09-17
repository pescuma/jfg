package org.pescuma.jfg.gui;

import org.pescuma.jfg.Attribute;

public interface WidgetFormater
{
	class TextAndPos
	{
		String text;
		int caretPos;
	}
	
	TextAndPos format(Attribute attrib, TextAndPos text);
	
}
