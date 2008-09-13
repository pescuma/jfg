package jfg.map;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jfg.Attribute;
import jfg.AttributeListener;

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
		assertGroup(group);
		
		Collection<Object> attributes = group.getAttributes();
		assertNotNull(attributes);
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		Attribute attrib = (Attribute) it.next();
		assertAttribute(attrib, "abc", String.class);
		assertGetSet(attrib, map, "abc", "123", "aa", "xy");
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "d", Integer.class);
		assertGetSet(attrib, map, "d", Integer.valueOf(1), Integer.valueOf(4), Integer.valueOf(10));
		
		attrib = (Attribute) it.next();
		assertAttribute(attrib, "c", String.class);
		assertGetSet(attrib, map, "c", null, "aa", "xy");
	}
	
	private void assertGroup(MapGroup group)
	{
		assertEquals("Map", group.getName());
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
	
	private void assertAttribute(Attribute attrib, String name, Class<?> type)
	{
		assertEquals("map." + name, attrib.getName());
		assertEquals(type, attrib.getType());
		assertEquals(null, attrib.getValueRange());
		
		assertTrue(attrib.canWrite());
		assertEquals(null, attrib.asGroup());
		
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
