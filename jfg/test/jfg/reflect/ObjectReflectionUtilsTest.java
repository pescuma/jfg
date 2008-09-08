package jfg.reflect;

import static jfg.reflect.ObjectReflectionUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ObjectReflectionUtilsTest
{
	@Test
	public void testGetDefaultValue()
	{
		assertEquals(Byte.valueOf((byte) 0), getDefaultValue(byte.class));
		assertEquals(Byte.valueOf((byte) 0), getDefaultValue(Byte.class));
		assertEquals(Short.valueOf((short) 0), getDefaultValue(short.class));
		assertEquals(Short.valueOf((short) 0), getDefaultValue(Short.class));
		assertEquals(Integer.valueOf(0), getDefaultValue(int.class));
		assertEquals(Integer.valueOf(0), getDefaultValue(Integer.class));
		assertEquals(Long.valueOf(0), getDefaultValue(long.class));
		assertEquals(Long.valueOf(0), getDefaultValue(Long.class));
		assertEquals(Float.valueOf(0), getDefaultValue(float.class));
		assertEquals(Float.valueOf(0), getDefaultValue(Float.class));
		assertEquals(Double.valueOf(0), getDefaultValue(double.class));
		assertEquals(Double.valueOf(0), getDefaultValue(Double.class));
		assertEquals(Boolean.FALSE, getDefaultValue(boolean.class));
		assertEquals(Boolean.FALSE, getDefaultValue(Boolean.class));
		assertEquals(Character.valueOf('\0'), getDefaultValue(char.class));
		assertEquals(Character.valueOf('\0'), getDefaultValue(Character.class));
		assertEquals("", getDefaultValue(String.class));
		assertEquals(null, getDefaultValue(Object.class));
		assertEquals(null, getDefaultValue(ObjectReflectionUtils.class));
	}
	
	@Test
	public void testValue() throws Exception
	{
		assertEquals(null, valueOf(null, null));
		
		assertEquals(Byte.valueOf((byte) 10), valueOf(10, byte.class));
		assertEquals(Byte.valueOf((byte) 10), valueOf(10, Byte.class));
		assertEquals(Short.valueOf((short) 10), valueOf(10, short.class));
		assertEquals(Short.valueOf((short) 10), valueOf(10, Short.class));
		assertEquals(Integer.valueOf(10), valueOf(10, int.class));
		assertEquals(Integer.valueOf(10), valueOf(10, Integer.class));
		assertEquals(Long.valueOf(10), valueOf(10, long.class));
		assertEquals(Long.valueOf(10), valueOf(10, Long.class));
		assertEquals(Float.valueOf(10), valueOf(10, float.class));
		assertEquals(Float.valueOf(10), valueOf(10, Float.class));
		assertEquals(Double.valueOf(10), valueOf(10, double.class));
		assertEquals(Double.valueOf(10), valueOf(10, Double.class));
	}
}
