package jfg.gui.swt;

import static jfg.gui.swt.TypeUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TypeUtilsTest
{
	@Test
	public void testValueOf()
	{
		assertEquals(Byte.valueOf((byte) 0), valueOf(null, byte.class, "0"));
		assertEquals(Byte.valueOf((byte) 10), valueOf("10", byte.class, "0"));
		assertEquals(Byte.valueOf((byte) -10), valueOf("-10", byte.class, "0"));
		assertEquals(Byte.valueOf((byte) 0), valueOf(null, Byte.class, "0"));
		assertEquals(Byte.valueOf((byte) 10), valueOf("10", Byte.class, "0"));
		assertEquals(Byte.valueOf((byte) -10), valueOf("-10", Byte.class, "0"));
		
		assertEquals(Short.valueOf((short) 0), valueOf(null, short.class, "0"));
		assertEquals(Short.valueOf((short) 10), valueOf("10", short.class, "0"));
		assertEquals(Short.valueOf((short) -10), valueOf("-10", short.class, "0"));
		assertEquals(Short.valueOf((short) 0), valueOf(null, Short.class, "0"));
		assertEquals(Short.valueOf((short) 10), valueOf("10", Short.class, "0"));
		assertEquals(Short.valueOf((short) -10), valueOf("-10", Short.class, "0"));
		
		assertEquals(Integer.valueOf(0), valueOf(null, int.class, "0"));
		assertEquals(Integer.valueOf(10), valueOf("10", int.class, "0"));
		assertEquals(Integer.valueOf(-10), valueOf("-10", int.class, "0"));
		assertEquals(Integer.valueOf(0), valueOf(null, Integer.class, "0"));
		assertEquals(Integer.valueOf(10), valueOf("10", Integer.class, "0"));
		assertEquals(Integer.valueOf(-10), valueOf("-10", Integer.class, "0"));
		
		assertEquals(Long.valueOf(0), valueOf(null, long.class, "0"));
		assertEquals(Long.valueOf(10), valueOf("10", long.class, "0"));
		assertEquals(Long.valueOf(-10), valueOf("-10", long.class, "0"));
		assertEquals(Long.valueOf(0), valueOf(null, Long.class, "0"));
		assertEquals(Long.valueOf(10), valueOf("10", Long.class, "0"));
		assertEquals(Long.valueOf(-10), valueOf("-10", Long.class, "0"));
		
		assertEquals(Float.valueOf(0), valueOf(null, float.class, "0"));
		assertEquals(Float.valueOf(10), valueOf("10", float.class, "0"));
		assertEquals(Float.valueOf(-10), valueOf("-10", float.class, "0"));
		assertEquals(Float.valueOf(0), valueOf(null, Float.class, "0"));
		assertEquals(Float.valueOf(10), valueOf("10", Float.class, "0"));
		assertEquals(Float.valueOf(-10), valueOf("-10", Float.class, "0"));
		
		assertEquals(Double.valueOf(0), valueOf(null, double.class, "0"));
		assertEquals(Double.valueOf(10), valueOf("10", double.class, "0"));
		assertEquals(Double.valueOf(-10), valueOf("-10", double.class, "0"));
		assertEquals(Double.valueOf(0), valueOf(null, Double.class, "0"));
		assertEquals(Double.valueOf(10), valueOf("10", Double.class, "0"));
		assertEquals(Double.valueOf(-10), valueOf("-10", Double.class, "0"));
	}
	
	@Test
	public void testGetMinMax()
	{
		assertArrayEquals(new long[] { Byte.MIN_VALUE, Byte.MAX_VALUE }, getMinMaxAsLong(byte.class));
		assertArrayEquals(new long[] { Byte.MIN_VALUE, Byte.MAX_VALUE }, getMinMaxAsLong(Byte.class));
		
		assertArrayEquals(new long[] { Short.MIN_VALUE, Short.MAX_VALUE }, getMinMaxAsLong(short.class));
		assertArrayEquals(new long[] { Short.MIN_VALUE, Short.MAX_VALUE }, getMinMaxAsLong(Short.class));
		
		assertArrayEquals(new long[] { Integer.MIN_VALUE, Integer.MAX_VALUE }, getMinMaxAsLong(int.class));
		assertArrayEquals(new long[] { Integer.MIN_VALUE, Integer.MAX_VALUE }, getMinMaxAsLong(Integer.class));
		
		assertArrayEquals(new long[] { Long.MIN_VALUE, Long.MAX_VALUE }, getMinMaxAsLong(long.class));
		assertArrayEquals(new long[] { Long.MIN_VALUE, Long.MAX_VALUE }, getMinMaxAsLong(Long.class));
	}
	
	@Test
	public void testAsLong() throws Exception
	{
		assertEquals(0, asLong(Byte.valueOf((byte) 0)));
		assertEquals(10, asLong(Byte.valueOf((byte) 10)));
		assertEquals(-10, asLong(Byte.valueOf((byte) -10)));
		
		assertEquals(0, asLong(Short.valueOf((short) 0)));
		assertEquals(10, asLong(Short.valueOf((short) 10)));
		assertEquals(-10, asLong(Short.valueOf((short) -10)));
		
		assertEquals(0, asLong(Integer.valueOf(0)));
		assertEquals(10, asLong(Integer.valueOf(10)));
		assertEquals(-10, asLong(Integer.valueOf(-10)));
		
		assertEquals(0, asLong(Long.valueOf(0)));
		assertEquals(10, asLong(Long.valueOf(10)));
		assertEquals(-10, asLong(Long.valueOf(-10)));
		
		assertEquals(0, asLong(Float.valueOf(0)));
		assertEquals(10, asLong(Float.valueOf(10)));
		assertEquals(-10, asLong(Float.valueOf(-10)));
		assertEquals(0, asLong(Float.valueOf(0.1f)));
		assertEquals(10, asLong(Float.valueOf(10.1f)));
		assertEquals(-10, asLong(Float.valueOf(-10.1f)));
		assertEquals(0, asLong(Float.valueOf(0.9f)));
		assertEquals(10, asLong(Float.valueOf(10.9f)));
		assertEquals(-10, asLong(Float.valueOf(-10.9f)));
		
		assertEquals(0, asLong(Double.valueOf(0)));
		assertEquals(10, asLong(Double.valueOf(10)));
		assertEquals(-10, asLong(Double.valueOf(-10)));
		assertEquals(0, asLong(Double.valueOf(0.1)));
		assertEquals(10, asLong(Double.valueOf(10.1)));
		assertEquals(-10, asLong(Double.valueOf(-10.1)));
		assertEquals(0, asLong(Double.valueOf(0.9)));
		assertEquals(10, asLong(Double.valueOf(10.9)));
		assertEquals(-10, asLong(Double.valueOf(-10.9)));
	}
	
	@Test
	public void testAsDouble() throws Exception
	{
		assertEquals(0, asDouble(Byte.valueOf((byte) 0)));
		assertEquals(10, asDouble(Byte.valueOf((byte) 10)));
		assertEquals(-10, asDouble(Byte.valueOf((byte) -10)));
		
		assertEquals(0, asDouble(Short.valueOf((short) 0)));
		assertEquals(10, asDouble(Short.valueOf((short) 10)));
		assertEquals(-10, asDouble(Short.valueOf((short) -10)));
		
		assertEquals(0, asDouble(Integer.valueOf(0)));
		assertEquals(10, asDouble(Integer.valueOf(10)));
		assertEquals(-10, asDouble(Integer.valueOf(-10)));
		
		assertEquals(0, asDouble(Long.valueOf(0)));
		assertEquals(10, asDouble(Long.valueOf(10)));
		assertEquals(-10, asDouble(Long.valueOf(-10)));
		
		assertEquals(0, asDouble(Float.valueOf(0)));
		assertEquals(10, asDouble(Float.valueOf(10)));
		assertEquals(-10, asDouble(Float.valueOf(-10)));
		assertEquals(0.1f, asDouble(Float.valueOf(0.1f)));
		assertEquals(10.1f, asDouble(Float.valueOf(10.1f)));
		assertEquals(-10.1f, asDouble(Float.valueOf(-10.1f)));
		assertEquals(0.9f, asDouble(Float.valueOf(0.9f)));
		assertEquals(10.9f, asDouble(Float.valueOf(10.9f)));
		assertEquals(-10.9f, asDouble(Float.valueOf(-10.9f)));
		
		assertEquals(0, asDouble(Double.valueOf(0)));
		assertEquals(10, asDouble(Double.valueOf(10)));
		assertEquals(-10, asDouble(Double.valueOf(-10)));
		assertEquals(0.1, asDouble(Double.valueOf(0.1)));
		assertEquals(10.1, asDouble(Double.valueOf(10.1)));
		assertEquals(-10.1, asDouble(Double.valueOf(-10.1)));
		assertEquals(0.9, asDouble(Double.valueOf(0.9)));
		assertEquals(10.9, asDouble(Double.valueOf(10.9)));
		assertEquals(-10.9, asDouble(Double.valueOf(-10.9)));
	}
}
