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
}
