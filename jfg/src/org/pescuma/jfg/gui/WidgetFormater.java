package org.pescuma.jfg.gui;

import org.pescuma.jfg.Attribute;

public interface WidgetFormater
{
	class TextAndPos
	{
		public String text;
		public int caretPos;
	}
	
	TextAndPos format(Attribute attrib, TextAndPos text);
	
}
