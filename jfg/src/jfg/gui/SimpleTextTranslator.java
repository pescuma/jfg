package jfg.gui;

public class SimpleTextTranslator implements TextTranslator
{
	public String fieldName(String fieldName)
	{
		int index = fieldName.lastIndexOf('.');
		if (index >= 0)
			fieldName = fieldName.substring(index + 1);
		
		fieldName = fieldName.replaceAll("_", " - ");
		fieldName = fieldName.replaceAll("([A-Z]+)", " $1");
		fieldName = fieldName.replaceAll(" +", " ");
		return firstUpper(fieldName.trim());
	}
	
	private String firstUpper(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
}
