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
		fieldName = fieldName.replaceAll("([A-Z]+)([A-Z][^ A-Z])", "$1 $2");
		fieldName = fieldName.replaceAll("^( -)+", "");
		fieldName = fieldName.replaceAll("(- )+$", "");
		fieldName = fieldName.replaceAll(" +", " ");
		return firstUpper(fieldName.trim());
	}
	
	private String firstUpper(String str)
	{
		int len = str.length();
		if (len <= 0)
			return "";
		else if (len == 1)
			return str.toUpperCase();
		else
			return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
}
