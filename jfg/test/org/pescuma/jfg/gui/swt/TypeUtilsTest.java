/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import static org.junit.Assert.*;
import static org.pescuma.jfg.gui.swt.TypeUtils.*;

import java.util.Locale;

import org.junit.Test;

public class TypeUtilsTest
{
	static
	{
		Locale.setDefault(new Locale("pt"));
	}
	
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
		assertEquals(Float.valueOf(0), valueOf(",", float.class, "0"));
		assertEquals(Float.valueOf(0.123123f), valueOf(",123123", float.class, "0"));
		assertEquals(Float.valueOf(123123), valueOf("123123,", float.class, "0"));
		assertEquals(Float.valueOf(0), valueOf(null, Float.class, "0"));
		assertEquals(Float.valueOf(10), valueOf("10", Float.class, "0"));
		assertEquals(Float.valueOf(-10.1f), valueOf("-10,1", Float.class, "0"));
		assertEquals(Float.valueOf(0), valueOf(",", Float.class, "0"));
		assertEquals(Float.valueOf(0.123123f), valueOf(",123123", Float.class, "0"));
		assertEquals(Float.valueOf(123123), valueOf("123123,", Float.class, "0"));
		
		assertEquals(Double.valueOf(0), valueOf(null, double.class, "0"));
		assertEquals(Double.valueOf(10), valueOf("10", double.class, "0"));
		assertEquals(Double.valueOf(-10.1), valueOf("-10,1", double.class, "0"));
		assertEquals(Double.valueOf(0), valueOf(",", double.class, "0"));
		assertEquals(Double.valueOf(0.123123), valueOf(",123123", double.class, "0"));
		assertEquals(Double.valueOf(123123), valueOf("123123,", double.class, "0"));
		assertEquals(Double.valueOf(0), valueOf(null, Double.class, "0"));
		assertEquals(Double.valueOf(10), valueOf("10", Double.class, "0"));
		assertEquals(Double.valueOf(-10.1), valueOf("-10,1", Double.class, "0"));
		assertEquals(Double.valueOf(0), valueOf(",", Double.class, "0"));
		assertEquals(Double.valueOf(0.123123), valueOf(",123123", Double.class, "0"));
		assertEquals(Double.valueOf(123123), valueOf("123123,", Double.class, "0"));
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
		assertEquals(0, asDouble(Byte.valueOf((byte) 0)), 0.001f);
		assertEquals(10, asDouble(Byte.valueOf((byte) 10)), 0.001f);
		assertEquals(-10, asDouble(Byte.valueOf((byte) -10)), 0.001f);
		
		assertEquals(0, asDouble(Short.valueOf((short) 0)), 0.001f);
		assertEquals(10, asDouble(Short.valueOf((short) 10)), 0.001f);
		assertEquals(-10, asDouble(Short.valueOf((short) -10)), 0.001f);
		
		assertEquals(0, asDouble(Integer.valueOf(0)), 0.001f);
		assertEquals(10, asDouble(Integer.valueOf(10)), 0.001f);
		assertEquals(-10, asDouble(Integer.valueOf(-10)), 0.001f);
		
		assertEquals(0, asDouble(Long.valueOf(0)), 0.001f);
		assertEquals(10, asDouble(Long.valueOf(10)), 0.001f);
		assertEquals(-10, asDouble(Long.valueOf(-10)), 0.001f);
		
		assertEquals(0, asDouble(Float.valueOf(0)), 0.001f);
		assertEquals(10, asDouble(Float.valueOf(10)), 0.001f);
		assertEquals(-10, asDouble(Float.valueOf(-10)), 0.001f);
		assertEquals(0.1f, asDouble(Float.valueOf(0.1f)), 0.001f);
		assertEquals(10.1f, asDouble(Float.valueOf(10.1f)), 0.001f);
		assertEquals(-10.1f, asDouble(Float.valueOf(-10.1f)), 0.001f);
		assertEquals(0.9f, asDouble(Float.valueOf(0.9f)), 0.001f);
		assertEquals(10.9f, asDouble(Float.valueOf(10.9f)), 0.001f);
		assertEquals(-10.9f, asDouble(Float.valueOf(-10.9f)), 0.001f);
		
		assertEquals(0, asDouble(Double.valueOf(0)), 0.001f);
		assertEquals(10, asDouble(Double.valueOf(10)), 0.001f);
		assertEquals(-10, asDouble(Double.valueOf(-10)), 0.001f);
		assertEquals(0.1, asDouble(Double.valueOf(0.1)), 0.001f);
		assertEquals(10.1, asDouble(Double.valueOf(10.1)), 0.001f);
		assertEquals(-10.1, asDouble(Double.valueOf(-10.1)), 0.001f);
		assertEquals(0.9, asDouble(Double.valueOf(0.9)), 0.001f);
		assertEquals(10.9, asDouble(Double.valueOf(10.9)), 0.001f);
		assertEquals(-10.9, asDouble(Double.valueOf(-10.9)), 0.001f);
	}
	
	@Test
	public void testValueOfNumber() throws Exception
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
	
	@Test
	public void testTypeIsNumber() throws Exception
	{
		assertTrue(typeIsNumber(byte.class));
		assertTrue(typeIsNumber(Byte.class));
		assertTrue(typeIsNumber(short.class));
		assertTrue(typeIsNumber(Short.class));
		assertTrue(typeIsNumber(int.class));
		assertTrue(typeIsNumber(Integer.class));
		assertTrue(typeIsNumber(long.class));
		assertTrue(typeIsNumber(Long.class));
		assertFalse(typeIsNumber(float.class));
		assertFalse(typeIsNumber(Float.class));
		assertFalse(typeIsNumber(double.class));
		assertFalse(typeIsNumber(Double.class));
		assertFalse(typeIsNumber(String.class));
		assertFalse(typeIsNumber(Object.class));
		assertFalse(typeIsNumber("aaa"));
	}
	
	@Test
	public void testTypeIsReal() throws Exception
	{
		assertFalse(typeIsReal(byte.class));
		assertFalse(typeIsReal(Byte.class));
		assertFalse(typeIsReal(short.class));
		assertFalse(typeIsReal(Short.class));
		assertFalse(typeIsReal(int.class));
		assertFalse(typeIsReal(Integer.class));
		assertFalse(typeIsReal(long.class));
		assertFalse(typeIsReal(Long.class));
		assertTrue(typeIsReal(float.class));
		assertTrue(typeIsReal(Float.class));
		assertTrue(typeIsReal(double.class));
		assertTrue(typeIsReal(Double.class));
		assertFalse(typeIsReal(String.class));
		assertFalse(typeIsReal(Object.class));
		assertFalse(typeIsReal("aaa"));
	}
}
