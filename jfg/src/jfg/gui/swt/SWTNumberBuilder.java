package jfg.gui.swt;

import static jfg.gui.swt.TypeUtils.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SWTNumberBuilder extends SWTTextBuilder
{
	@Override
	public boolean acceptType(Object type)
	{
		return type == byte.class || type == Byte.class || type == short.class || type == Short.class || type == int.class
				|| type == Integer.class || type == long.class || type == Long.class;
	}
	
	@Override
	protected void addValidation(final Text text, final Object type)
	{
		final long[] mm = getMinMax(type);
		
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
				
				long val;
				try
				{
					val = parseLong(txt);
				}
				catch (NumberFormatException ex)
				{
					e.doit = false;
					return;
				}
				
				if (val < mm[0] || val > mm[1])
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
		String oldText = text.getText();
		long o = parseLong(oldText);
		long v = castNumber(value, type);
		
		if (o == v)
			return oldText;
		else if (value == null)
			return "";
		else
			return value.toString();
	}
	
	@Override
	protected Object convertToObject(String value, Object type)
	{
		return valueOf(value, type, "0");
	}
	
	private long parseLong(String oldText)
	{
		long o;
		if (oldText.isEmpty())
			o = 0;
		else
			o = Long.parseLong(oldText);
		return o;
	}
	
	private long castNumber(Object value, Object type)
	{
		return TypeUtils.castNumber(value, type);
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
