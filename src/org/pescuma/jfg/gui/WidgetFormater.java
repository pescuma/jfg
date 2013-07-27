package org.pescuma.jfg.gui;

import org.pescuma.jfg.Attribute;

public interface WidgetFormater
{
	class TextAndPos
	{
		public String text;
		public int selectionStart;
		public int selectionEnd;
	}
	
	TextAndPos format(Attribute attrib, TextAndPos text);
	
}
