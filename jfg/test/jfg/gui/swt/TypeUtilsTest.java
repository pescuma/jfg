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
	}
	
	@Test
	public void testGetMinMax()
	{
		assertArrayEquals(new long[] { Byte.MIN_VALUE, Byte.MAX_VALUE }, getMinMax(byte.class));
		assertArrayEquals(new long[] { Byte.MIN_VALUE, Byte.MAX_VALUE }, getMinMax(Byte.class));
		
		assertArrayEquals(new long[] { Short.MIN_VALUE, Short.MAX_VALUE }, getMinMax(short.class));
		assertArrayEquals(new long[] { Short.MIN_VALUE, Short.MAX_VALUE }, getMinMax(Short.class));
		
		assertArrayEquals(new long[] { Integer.MIN_VALUE, Integer.MAX_VALUE }, getMinMax(int.class));
		assertArrayEquals(new long[] { Integer.MIN_VALUE, Integer.MAX_VALUE }, getMinMax(Integer.class));
		
		assertArrayEquals(new long[] { Long.MIN_VALUE, Long.MAX_VALUE }, getMinMax(long.class));
		assertArrayEquals(new long[] { Long.MIN_VALUE, Long.MAX_VALUE }, getMinMax(Long.class));
	}
	
	@Test
	public void testCastNumber() throws Exception
	{
		assertEquals(0, castNumber(Byte.valueOf((byte) 0), Byte.class));
		assertEquals(10, castNumber(Byte.valueOf((byte) 10), Byte.class));
		assertEquals(-10, castNumber(Byte.valueOf((byte) -10), Byte.class));
		assertEquals(0, castNumber(Byte.valueOf((byte) 0), Byte.class));
		assertEquals(10, castNumber(Byte.valueOf((byte) 10), Byte.class));
		assertEquals(-10, castNumber(Byte.valueOf((byte) -10), Byte.class));
		
		assertEquals(0, castNumber(Short.valueOf((short) 0), Short.class));
		assertEquals(10, castNumber(Short.valueOf((short) 10), Short.class));
		assertEquals(-10, castNumber(Short.valueOf((short) -10), Short.class));
		assertEquals(0, castNumber(Short.valueOf((short) 0), Short.class));
		assertEquals(10, castNumber(Short.valueOf((short) 10), Short.class));
		assertEquals(-10, castNumber(Short.valueOf((short) -10), Short.class));
		
		assertEquals(0, castNumber(Integer.valueOf(0), Integer.class));
		assertEquals(10, castNumber(Integer.valueOf(10), Integer.class));
		assertEquals(-10, castNumber(Integer.valueOf(-10), Integer.class));
		assertEquals(0, castNumber(Integer.valueOf(0), Integer.class));
		assertEquals(10, castNumber(Integer.valueOf(10), Integer.class));
		assertEquals(-10, castNumber(Integer.valueOf(-10), Integer.class));
		
		assertEquals(0, castNumber(Long.valueOf(0), Long.class));
		assertEquals(10, castNumber(Long.valueOf(10), Long.class));
		assertEquals(-10, castNumber(Long.valueOf(-10), Long.class));
		assertEquals(0, castNumber(Long.valueOf(0), Long.class));
		assertEquals(10, castNumber(Long.valueOf(10), Long.class));
		assertEquals(-10, castNumber(Long.valueOf(-10), Long.class));
	}
}
