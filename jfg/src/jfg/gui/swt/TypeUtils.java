package jfg.gui.swt;

class TypeUtils
{
	public static Object valueOf(String value, Object type, String defVal)
	{
		if (value == null || value.isEmpty())
			value = defVal;
		
		if (type == byte.class || type == Byte.class)
		{
			return Byte.valueOf(value);
		}
		else if (type == short.class || type == Short.class)
		{
			return Short.valueOf(value);
		}
		else if (type == int.class || type == Integer.class)
		{
			return Integer.valueOf(value);
		}
		else if (type == long.class || type == Long.class)
		{
			return Long.valueOf(value);
		}
		else
			throw new IllegalArgumentException();
	}
	
	public static long[] getMinMax(Object type)
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
}
