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

package jfg.map;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.AttributeListener;
import jfg.AttributeValueRange;

import org.junit.Test;

public class MapGroupTest
{
	private AttributeListener dummyListener = new AttributeListener() {
		public void onChange()
		{
		}
	};
	
	@Test
	public void testSimpleMap()
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("abc", "123");
		map.put("d", Integer.valueOf(1));
		map.put("c", null);
		
		MapGroup group = new MapGroup(map);
		assertGroup(group, "Map");
		
		Collection<Object> attributes = group.getAttributes();
		assertNotNull(attributes);
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		Attribute attrib = (Attribute) it.next();
		assertAttribute(attrib, "abc", String.class, true, false);
		assertGetSet(attrib, map, "abc", "123", "aa", "xy");
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "d", Integer.class, true, false);
		assertGetSet(attrib, map, "d", Integer.valueOf(1), Integer.valueOf(4), Integer.valueOf(10));
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "c", String.class, true, true);
		assertGetSet(attrib, map, "c", null, "aa", "xy");
	}
	
	@Test
	public void testWithType()
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("abc", "123");
		map.put("d", Integer.valueOf(1));
		map.put("c", null);
		
		MapGroup group = new MapGroup("Xz", map, String.class);
		assertGroup(group, "Xz");
		
		Collection<Object> attributes = group.getAttributes();
		assertNotNull(attributes);
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		Attribute attrib = (Attribute) it.next();
		assertAttribute(attrib, "abc", String.class, true, false);
		assertGetSet(attrib, map, "abc", "123", "aa", "xy");
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "d", String.class, true, false);
		assertGetSet(attrib, map, "d", Integer.valueOf(1), "aa", "xy");
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "c", String.class, true, true);
		assertGetSet(attrib, map, "c", null, "aa", "xy");
	}
	
	@Test
	public void testWithSubMap()
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("abc", "123");
		map.put("d", Integer.valueOf(1));
		
		Map<String, Object> map2 = new LinkedHashMap<String, Object>();
		map2.put("u", null);
		map2.put("v", "Zzzzz");
		map2.put("t", Float.valueOf(4));
		map.put("x", map2);
		
		map.put("c", null);
		
		MapGroup group = new MapGroup(map);
		assertGroup(group, "Map");
		
		Collection<Object> attributes = group.getAttributes();
		assertNotNull(attributes);
		assertEquals(4, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		Attribute attrib = (Attribute) it.next();
		assertAttribute(attrib, "abc", String.class, true, false);
		assertGetSet(attrib, map, "abc", "123", "aa", "xy");
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "d", Integer.class, true, false);
		assertGetSet(attrib, map, "d", Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "x", Map.class, false, false);
		{
			AttributeGroup group2 = attrib.asGroup();
			assertGroup(group2, "map.x");
			
			Collection<Object> attributes2 = group2.getAttributes();
			assertNotNull(attributes2);
			assertEquals(3, attributes2.size());
			
			Iterator<Object> it2 = attributes2.iterator();
			Attribute attrib2 = (Attribute) it2.next();
			assertAttribute(attrib2, "u", String.class, true, true);
			assertGetSet(attrib2, map2, "u", null, "aa", "xy");
			
			attrib2 = (Attribute) it2.next();
			assertAttribute(attrib2, "v", String.class, true, false);
			assertGetSet(attrib2, map2, "v", "Zzzzz", "aa", "xy");
			
			attrib2 = (Attribute) it2.next();
			assertAttribute(attrib2, "t", Float.class, true, false);
			assertGetSet(attrib2, map2, "t", Float.valueOf(4), Float.valueOf(5), Float.valueOf(49999999999f));
		}
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "c", String.class, true, true);
		assertGetSet(attrib, map, "c", null, "aa", "xy");
	}
	
	private void assertGroup(AttributeGroup group, String name)
	{
		assertEquals(name, group.getName());
		assertEquals(false, group.canListen());
		
		try
		{
			group.addListener(dummyListener);
			fail();
		}
		catch (MapAttributeException e)
		{
		}
		
		try
		{
			group.removeListener(dummyListener);
			fail();
		}
		catch (MapAttributeException e)
		{
		}
	}
	
	private void assertAttribute(Attribute attrib, String name, Class<?> type, boolean canWrite, boolean canBeNull)
	{
		assertEquals("map." + name, attrib.getName());
		assertEquals(type, attrib.getType());
		
		AttributeValueRange valueRange = attrib.getValueRange();
		if (canBeNull)
			assertNull(null, valueRange);
		else
		{
			assertNotNull(valueRange);
			assertFalse(valueRange.canBeNull());
			assertNull(valueRange.getComparator());
			assertNull(valueRange.getMax());
			assertNull(valueRange.getMin());
			assertNull(valueRange.getPossibleValues());
		}
		
		assertEquals(canWrite, attrib.canWrite());
		if (canWrite)
			assertNull(attrib.asGroup());
		else
			assertNotNull(attrib.asGroup());
		
		assertFalse(attrib.canListen());
		
		try
		{
			attrib.addListener(dummyListener);
			fail();
		}
		catch (MapAttributeException e)
		{
		}
		
		try
		{
			attrib.removeListener(dummyListener);
			fail();
		}
		catch (MapAttributeException e)
		{
		}
	}
	
	@SuppressWarnings("unchecked")
	private void assertGetSet(Attribute attrib, Map map, Object key, Object value0, Object value1, Object value2)
	{
		assertEquals(value0, attrib.getValue());
		attrib.setValue(value1);
		assertEquals(value1, attrib.getValue());
		assertEquals(value1, map.get(key));
		map.put(key, value2);
		assertEquals(value2, attrib.getValue());
		try
		{
			attrib.setValue(Long.valueOf(1));
			fail();
		}
		catch (ClassCastException e)
		{
		}
		assertEquals(value2, attrib.getValue());
		attrib.setValue(null);
		assertEquals(null, attrib.getValue());
	}
}
