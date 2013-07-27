package org.pescuma.jfg.validators;

import java.util.Date;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.WidgetValidator;

public class NotEmptyValidator implements WidgetValidator
{
	@Override
	public boolean isValid(Attribute attrib, Object value)
	{
		if (value == null)
			return false;
		
		if (value instanceof String)
			return !((String) value).trim().isEmpty();
		
		if (value instanceof Date)
			return ((Date) value).getTime() > 0;
		
		return true;
	}
}
