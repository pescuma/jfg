package jfg.gui.swt;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

class TypeUtils
{
	public static long[] getMinMaxAsLong(Object type)
	{
		final long[] mm = new long[2];
		if (type == byte.class || type == Byte.class)
		{
			mm[0] = Byte.MIN_VALUE;
			mm[1] = Byte.MAX_VALUE;
		}
		else if (type == short.class || type == Short.class)
		{
			mm[0] = Short.MIN_VALUE;
			mm[1] = Short.MAX_VALUE;
		}
		else if (type == int.class || type == Integer.class)
		{
			mm[0] = Integer.MIN_VALUE;
			mm[1] = Integer.MAX_VALUE;
		}
		else if (type == long.class || type == Long.class)
		{
			mm[0] = Long.MIN_VALUE;
			mm[1] = Long.MAX_VALUE;
		}
		else
			throw new IllegalArgumentException();
		
		return mm;
	}
	
	public static long asLong(Object value)
	{
		Class<?> type = (value == null ? null : value.getClass());
		
		long v;
		if (value == null)
			v = 0;
		else if (type == byte.class || type == Byte.class)
			v = ((Byte) value).longValue();
		else if (type == short.class || type == Short.class)
			v = ((Short) value).longValue();
		else if (type == int.class || type == Integer.class)
			v = ((Integer) value).longValue();
		else if (type == long.class || type == Long.class)
			v = ((Long) value).longValue();
		else if (type == float.class || type == Float.class)
			v = ((Float) value).longValue();
		else if (type == double.class || type == Double.class)
			v = ((Double) value).longValue();
		else
			throw new IllegalArgumentException();
		
		return v;
	}
	
	public static double asDouble(Object value)
	{
		Class<?> type = (value == null ? null : value.getClass());
		
		double v;
		if (value == null)
			v = 0;
		else if (type == byte.class || type == Byte.class)
			v = ((Byte) value).doubleValue();
		else if (type == short.class || type == Short.class)
			v = ((Short) value).doubleValue();
		else if (type == int.class || type == Integer.class)
			v = ((Integer) value).doubleValue();
		else if (type == long.class || type == Long.class)
			v = ((Long) value).doubleValue();
		else if (type == float.class || type == Float.class)
			v = ((Float) value).doubleValue();
		else if (type == double.class || type == Double.class)
			v = ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException();
		return v;
	}
	
	public static Object valueOf(String value, Object type, String defVal)
	{
		if (value == null || value.isEmpty() || value.equals(String.valueOf(new DecimalFormatSymbols().getDecimalSeparator())))
			value = defVal;
		if (value == null)
			return null;
		
		if (type == byte.class || type == Byte.class)
			return Byte.valueOf(value);
		else if (type == short.class || type == Short.class)
			return Short.valueOf(value);
		else if (type == int.class || type == Integer.class)
			return Integer.valueOf(value);
		else if (type == long.class || type == Long.class)
			return Long.valueOf(value);
		else if (type == float.class || type == Float.class)
		{
			try
			{
				return Float.valueOf(NumberFormat.getNumberInstance().parse(value).floatValue());
			}
			catch (ParseException e)
			{
				throw new NumberFormatException(e.getMessage());
			}
		}
		else if (type == double.class || type == Double.class)
			try
			{
				return Double.valueOf(NumberFormat.getNumberInstance().parse(value).doubleValue());
			}
			catch (ParseException e)
			{
				throw new NumberFormatException(e.getMessage());
			}
		else
			throw new IllegalArgumentException();
	}
	
	public static long parseLong(String value, Object type)
	{
		return ((Number) valueOf(value, type, "0")).longValue();
	}
	
	public static double parseDouble(String value, Object type)
	{
		return ((Number) valueOf(value, type, "0")).doubleValue();
	}
	
	public static Object valueOf(Number value, Object type)
	{
		if (value == null)
			return null;
		
		if (type == byte.class || type == Byte.class)
			return Byte.valueOf(value.byteValue());
		else if (type == short.class || type == Short.class)
			return Short.valueOf(value.shortValue());
		else if (type == int.class || type == Integer.class)
			return Integer.valueOf(value.intValue());
		else if (type == long.class || type == Long.class)
			return Long.valueOf(value.longValue());
		else if (type == float.class || type == Float.class)
			return Float.valueOf(value.floatValue());
		else if (type == double.class || type == Double.class)
			return Double.valueOf(value.doubleValue());
		else
			throw new IllegalArgumentException();
	}
	
	public static boolean typeIsNumber(Object type)
	{
		return type == byte.class || type == Byte.class || type == short.class || type == Short.class || type == int.class
				|| type == Integer.class || type == long.class || type == Long.class;
	}
	
	public static boolean typeIsReal(Object type)
	{
		return type == float.class || type == Float.class || type == double.class || type == Double.class;
	}
}
