package org.pescuma.jfg.validators;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.WidgetValidator;

public class CEPValidator implements WidgetValidator
{
	@Override
	public boolean isValid(Attribute attrib, Object value)
	{
		if (!(value instanceof String))
			return false;
		
		String text = (String) value;
		
		// Others worry about empty text
		if (text.length() < 1)
			return true;
		
		text = text.replace("-", "");
		
		int textLength = text.length();
		if (textLength != 5 && textLength != 8)
			return false;
		
		for (int i = 0; i < textLength; i++)
		{
			char c = text.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		
		return true;
	}
	
}
