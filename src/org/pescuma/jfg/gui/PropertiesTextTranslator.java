package org.pescuma.jfg.gui;

import java.util.Properties;

public class PropertiesTextTranslator implements TextTranslator
{
	private final Properties props;
	private final boolean addMissingEntries;
	
	public PropertiesTextTranslator(Properties props)
	{
		this(props, false);
	}
	
	public PropertiesTextTranslator(Properties props, boolean addMissingEntries)
	{
		this.props = props;
		this.addMissingEntries = addMissingEntries;
	}
	
	@Override
	public String fieldName(String fieldName)
	{
		return getProp(fieldName);
	}
	
	@Override
	public String enumElement(String enumElement)
	{
		return getProp(enumElement);
	}
	
	@Override
	public String groupName(String groupName)
	{
		return getProp(groupName);
	}
	
	@Override
	public String translate(String text)
	{
		return getProp(text);
	}
	
	private String getProp(String text, String... arguments)
	{
		String ret = props.getProperty(text);
		if (ret == null)
		{
			ret = text;
			
			if (addMissingEntries)
				props.setProperty(text, text);
		}
		return ret;
	}
}
