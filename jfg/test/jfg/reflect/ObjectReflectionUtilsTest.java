package jfg.reflect;

import static jfg.reflect.ObjectReflectionUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ObjectReflectionUtilsTest
{
	@Test
	public void testGetDefaultValue()
	{
		assertEquals(new Byte((byte) 0), getDefaultValue(byte.class));
		assertEquals(new Byte((byte) 0), getDefaultValue(Byte.class));
		assertEquals(new Short((short) 0), getDefaultValue(short.class));
		assertEquals(new Short((short) 0), getDefaultValue(Short.class));
		assertEquals(new Integer(0), getDefaultValue(int.class));
		assertEquals(new Integer(0), getDefaultValue(Integer.class));
		assertEquals(new Long(0), getDefaultValue(long.class));
		assertEquals(new Long(0), getDefaultValue(Long.class));
		assertEquals(new Float(0), getDefaultValue(float.class));
		assertEquals(new Float(0), getDefaultValue(Float.class));
		assertEquals(new Double(0), getDefaultValue(double.class));
		assertEquals(new Double(0), getDefaultValue(Double.class));
		assertEquals(Boolean.FALSE, getDefaultValue(boolean.class));
		assertEquals(Boolean.FALSE, getDefaultValue(Boolean.class));
		assertEquals(new Character('\0'), getDefaultValue(char.class));
		assertEquals(new Character('\0'), getDefaultValue(Character.class));
		assertEquals("", getDefaultValue(String.class));
		assertEquals(null, getDefaultValue(Object.class));
		assertEquals(null, getDefaultValue(ObjectReflectionUtils.class));
	}
}
