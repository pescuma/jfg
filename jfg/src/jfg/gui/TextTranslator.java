package jfg.gui;

public interface TextTranslator
{
	String fieldName(String fieldName);
	String enumElement(String enumElement);
	String groupName(String groupName);
	
	String translate(String text);
}
