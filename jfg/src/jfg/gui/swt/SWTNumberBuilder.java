package jfg.gui.swt;

import static jfg.gui.swt.TypeUtils.*;
import jfg.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SWTNumberBuilder extends SWTTextBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == byte.class || type == Byte.class || type == short.class || type == Short.class || type == int.class
				|| type == Integer.class || type == long.class || type == Long.class || type == float.class || type == Float.class
				|| type == double.class || type == Double.class;
	}
	
	@Override
	protected void addValidation(final Text text, final Object type)
	{
		text.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e)
			{
				if (e.character != 0 && Character.isISOControl(e.character))
					return;
				
				String append = e.text;
				if (!isValidNumber(append, e.start))
				{
					e.doit = false;
					return;
				}
				
				String txt = text.getText();
				txt = txt.substring(0, e.start) + append + txt.substring(e.end);
				
				try
				{
					parseLong(txt, type);
				}
				catch (NumberFormatException ex)
				{
					e.doit = false;
					return;
				}
			}
		});
	}
	
	@Override
	protected String convertToString(Text text, Object value, Object type)
	{
		if (value == null)
			return "";
		
		String oldText = text.getText();
		long o = parseLong(oldText, type);
		long v = asLong(value);
		
		if (o == v)
			return oldText;
		else
			return value.toString();
	}
	
	@Override
	protected Object convertToObject(String value, Object type, boolean canBeNull)
	{
		return valueOf(value, type, canBeNull ? null : "0");
	}
	
	static boolean isValidNumber(String append, int start)
	{
		for (int i = 0; i < append.length(); i++)
		{
			char c = append.charAt(i);
			
			if (i == 0 && start == 0 && c == '-')
				continue;
			
			if ('0' <= c && c <= '9')
				continue;
			
			return false;
		}
		return true;
	}
}
