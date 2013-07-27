package org.pescuma.jfg.validators;

import java.util.regex.Pattern;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.WidgetValidator;

public class CEPValidator implements WidgetValidator
{
	private static final Pattern re = Pattern.compile("\\d{5}(-?\\d{3})?");
	
	@Override
	public boolean isValid(Attribute attrib, Object value)
	{
		if (!(value instanceof String))
			return false;
		
		String text = (String) value;
		
		// Others worry about empty text
		if (text.length() < 1)
			return true;
		
		return re.matcher(text).matches();
	}
	
}
