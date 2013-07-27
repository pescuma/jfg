package org.pescuma.jfg.validators;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.WidgetValidator;

public class EmailValidator implements WidgetValidator
{
	@Override
	public boolean isValid(Attribute attrib, Object value)
	{
		if (!(value instanceof String))
			return false;
		
		String text = (String) value;
		int length = text.length();
		
		// Others worry about empty text
		if (length < 1)
			return true;
		
		if (length < 5)
			return false;
		
		if (text.indexOf(' ') >= 0)
			return false;
		
		int a = text.indexOf('@');
		if (a < 1)
			return false;
		
		if (text.indexOf('@', a + 1) >= 0)
			return false;
		
		int p = text.indexOf('.', a + 1);
		return p > a + 1 && p < length - 1;
	}
}
