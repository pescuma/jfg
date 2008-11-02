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

package org.pescuma.jfg.reflect;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.AttributeListenerConverter;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.model.ann.CompareWith;
import org.pescuma.jfg.model.ann.NotNull;
import org.pescuma.jfg.model.ann.Range;

public class ClassReflectionGroupTest
{
	private static class TestClassNoData
	{
	}
	
	@Test
	public void testNoData()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassNoData.class);
		assertEquals("TestClassNoData", group.getName());
		assertEquals(0, group.getAttributes().size());
	}
	
	private static class TestClassWithPublicFields
	{
		public static int aa;
		public static long bb;
		public static String cc;
		protected static int dd;
	}
	
	@Test
	public void testWithPublicFields()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithPublicFields.class);
		assertEquals("TestClassWithPublicFields", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithPublicFields.aa", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		TestClassWithPublicFields.aa = 1;
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithPublicFields.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		TestClassWithPublicFields.bb = 1;
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithPublicFields.cc", attr.getName());
		assertEquals(null, attr.getValue());
		TestClassWithPublicFields.cc = "x";
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
		
	}
	
	private static void assertSimpleFieldAttribute(Object obj, Class<?> cls)
	{
		assertFieldAttribute(obj, true, false, false, cls.isPrimitive());
		Attribute attr = (Attribute) obj;
		assertEquals(cls, attr.getType());
		
		if (cls.isPrimitive())
		{
			AttributeValueRange range = attr.getValueRange();
			assertFalse(range.canBeNull());
			assertNull(range.getComparator());
			assertNull(range.getMax());
			assertNull(range.getMin());
			assertNull(range.getPossibleValues());
		}
	}
	
	private static void assertFieldAttribute(Object obj, boolean canWrite, boolean canListen, boolean asGroup, boolean hasRange)
	{
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals(canWrite, attr.canWrite());
		assertEquals(canListen, attr.canListen());
		
		if (asGroup)
			assertNotNull(attr.asGroup());
		else
			assertNull(attr.asGroup());
		
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
			}
		};
		
		try
		{
			attr.addListener(listener);
			if (!canListen)
				fail();
		}
		catch (RuntimeException e)
		{
			if (canListen)
				fail();
		}
		
		try
		{
			attr.removeListener(listener);
			if (!canListen)
				fail();
		}
		catch (RuntimeException e)
		{
			if (canListen)
				fail();
		}
		
		if (hasRange)
			assertNotNull(attr.getValueRange());
		else
			assertNull(attr.getValueRange());
	}
	
	private static class TestClassWithPublicFieldsAndGettersSetters
	{
		public float xx;
		
		public static int aa;
		protected static long bb;
		public static String cc;
		protected static int dd;
		
		public static int getAa()
		{
			return 100 + aa;
		}
		
		public static void setAa(int aa)
		{
			TestClassWithPublicFieldsAndGettersSetters.aa = 10 + aa;
		}
		
		public static long getBb()
		{
			return 100 + bb;
		}
		
		public static void setCc(String cc)
		{
			TestClassWithPublicFieldsAndGettersSetters.cc = "-" + cc;
		}
	}
	
	@Test
	public void testWithPublicFieldsAndGettersSetters()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithPublicFieldsAndGettersSetters.class);
		assertEquals("TestClassWithPublicFieldsAndGettersSetters", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		TestClassWithPublicFieldsAndGettersSetters.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertFieldAttribute(obj, false, false, false, true);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.bb", attr.getName());
		assertEquals(long.class, attr.getType());
		AttributeValueRange range = attr.getValueRange();
		assertFalse(range.canBeNull());
		assertNull(range.getComparator());
		assertNull(range.getMax());
		assertNull(range.getMin());
		assertNull(range.getPossibleValues());
		assertEquals(Long.valueOf(100), attr.getValue());
		TestClassWithPublicFieldsAndGettersSetters.bb = 1;
		assertEquals(Long.valueOf(101), attr.getValue());
		try
		{
			attr.setValue(Long.valueOf(2));
			fail();
		}
		catch (ReflectionAttributeException e)
		{
			
		}
		assertEquals(Long.valueOf(101), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.cc", attr.getName());
		assertEquals(null, attr.getValue());
		TestClassWithPublicFieldsAndGettersSetters.cc = "x";
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("-y", attr.getValue());
	}
	
	private static class TestClassWithOnlyMethods
	{
		private int xx;
		private static int aa;
		private static long bb;
		private static String cc;
		
		public int getXx()
		{
			return xx;
		}
		
		public void setXx(int x)
		{
			xx = x;
		}
		
		public static int getAa()
		{
			return aa;
		}
		
		public static void setAa(int aa)
		{
			TestClassWithOnlyMethods.aa = aa;
		}
		
		public static long getBb()
		{
			return bb;
		}
		
		public static void setBb(long bb)
		{
			TestClassWithOnlyMethods.bb = bb;
		}
		
		public static String getCc()
		{
			return cc;
		}
		
		public static void setCc(String cc)
		{
			TestClassWithOnlyMethods.cc = cc;
		}
	}
	
	@Test
	public void testWithOnlyMethods()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithOnlyMethods.class);
		assertEquals("TestClassWithOnlyMethods", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithOnlyMethods.aa", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		TestClassWithOnlyMethods.setAa(1);
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithOnlyMethods.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		TestClassWithOnlyMethods.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithOnlyMethods.cc", attr.getName());
		assertEquals(null, attr.getValue());
		TestClassWithOnlyMethods.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
	}
	
	private static class TestClassWithEverything
	{
		public static int aa;
		private static long bb;
		private static String cc;
		
		public static int getAa()
		{
			return 100 + aa;
		}
		
		public static void setAa(int aa)
		{
			TestClassWithEverything.aa = 10 + aa;
		}
		
		public static long getBb()
		{
			return bb;
		}
		
		public static void setBb(long bb)
		{
			TestClassWithEverything.bb = bb;
		}
		
		public static String getCc()
		{
			return cc;
		}
		
		public static void setCc(String cc)
		{
			TestClassWithEverything.cc = cc;
		}
	}
	
	@Test
	public void testWithEverything()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithEverything.class);
		assertEquals("TestClassWithEverything", group.getName());
		
		testWithEverything(group);
	}
	
	private static void testWithEverything(AttributeGroup group)
	{
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEverything.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		TestClassWithEverything.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEverything.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		TestClassWithEverything.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEverything.cc", attr.getName());
		assertEquals(null, attr.getValue());
		TestClassWithEverything.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
	}
	
	private static class TestClassWithExtends extends TestClassWithEverything
	{
		public static int dd;
		private static long ee;
		
		public static long getEe()
		{
			return ee;
		}
		
		public static void setEe(long ee)
		{
			TestClassWithExtends.ee = ee;
		}
	}
	
	@Test
	public void testWithExtends()
	{
		TestClassWithEverything.aa = 0;
		TestClassWithEverything.bb = 0;
		TestClassWithEverything.cc = null;
		
		ReflectionGroup group = new ReflectionGroup(TestClassWithExtends.class);
		assertEquals("TestClassWithExtends", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(5, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEverything.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		TestClassWithExtends.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEverything.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		TestClassWithExtends.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEverything.cc", attr.getName());
		assertEquals(null, attr.getValue());
		TestClassWithExtends.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
		
		// dd
		obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithExtends.dd", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		TestClassWithExtends.dd = 1;
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// ee
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithExtends.ee", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		TestClassWithExtends.setEe(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
	}
	
	private static class TestClassWithFieldListener
	{
		private static long aa;
		private static final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
		
		public static long getAa()
		{
			return aa;
		}
		
		public static void setAa(long aa)
		{
			TestClassWithFieldListener.aa = aa;
			
			notifyOnChange();
		}
		
		private static void notifyOnChange()
		{
			for (AttributeListener listener : listeners)
			{
				listener.onChange();
			}
		}
		
		public static void addAaListener(AttributeListener listener)
		{
			listeners.add(listener);
		}
		
		public static void removeAaListener(AttributeListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithFieldListener()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithFieldListener.class);
		assertEquals("TestClassWithFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithFieldListener.aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final int[] called = new int[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0]++;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithFieldListener.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithFieldListener.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private interface StrangeListener
	{
		boolean mudou();
		
		void add(int i);
	}
	
	private static class TestClassWithStrangeFieldListener
	{
		private static long aa;
		private static final List<StrangeListener> listeners = new ArrayList<StrangeListener>();
		
		public static long getAa()
		{
			return aa;
		}
		
		public static void setAa(long aa)
		{
			TestClassWithStrangeFieldListener.aa = aa;
			
			notifyOnChange();
		}
		
		private static void notifyOnChange()
		{
			for (StrangeListener listener : listeners)
			{
				listener.mudou();
			}
		}
		
		public static void addAaListener(StrangeListener listener)
		{
			listeners.add(listener);
		}
		
		public static void removeAaListener(StrangeListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithStrangeFieldListener()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithStrangeFieldListener.class);
		assertEquals("TestClassWithStrangeFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithStrangeFieldListener.aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final int[] called = new int[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0]++;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithStrangeFieldListener.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithStrangeFieldListener.setAa(1);
		assertEquals(0, called[0]);
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithStrangeFieldListener.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithStrangeFieldListener.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private interface WrongListener
	{
		boolean mudou();
		
		void mudou2();
		
		void add(int i);
	}
	
	private static class TestClassWithWrongFieldListener
	{
		private static long aa;
		private static final List<WrongListener> listeners = new ArrayList<WrongListener>();
		
		public static long getAa()
		{
			return aa;
		}
		
		public static void setAa(long aa)
		{
			TestClassWithWrongFieldListener.aa = aa;
			
			notifyOnChange();
		}
		
		private static void notifyOnChange()
		{
			for (WrongListener listener : listeners)
			{
				listener.mudou();
			}
		}
		
		public static void addAaListener(WrongListener listener)
		{
			listeners.add(listener);
		}
		
		public static void removeAaListener(WrongListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithWrongFieldListener()
	{
		// First without knowing how to create listener
		
		ReflectionGroup group = new ReflectionGroup(TestClassWithWrongFieldListener.class);
		assertEquals("TestClassWithWrongFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		
		// Now with the converter
		
		ReflectionData data = new ReflectionData();
		data.attributeListenerConverters.put(WrongListener.class, new AttributeListenerConverter<WrongListener>() {
			public WrongListener wrapListener(final AttributeListener listener)
			{
				return new WrongListener() {
					
					public void add(int i)
					{
					}
					
					public boolean mudou()
					{
						listener.onChange();
						return false;
					}
					
					public void mudou2()
					{
					}
				};
			}
		});
		group = new ReflectionGroup(TestClassWithWrongFieldListener.class, data);
		assertEquals("TestClassWithWrongFieldListener", group.getName());
		
		attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		it = attributes.iterator();
		
		obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithWrongFieldListener.aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final int[] called = new int[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0]++;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithWrongFieldListener.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithWrongFieldListener.setAa(1);
		assertEquals(0, called[0]);
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithWrongFieldListener.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithWrongFieldListener.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private static interface GroupListener
	{
		void changed(int field);
	}
	
	private static class TestClassWithGroupListener
	{
		private static long aa;
		private static long bb;
		private static final List<GroupListener> listeners = new ArrayList<GroupListener>();
		
		public static long getAa()
		{
			return aa;
		}
		
		public static void setAa(long aa)
		{
			TestClassWithGroupListener.aa = aa;
			
			notifyOnChange(1);
		}
		
		public static long getBb()
		{
			return bb;
		}
		
		public static void setBb(long bb)
		{
			TestClassWithGroupListener.bb = bb;
			
			notifyOnChange(2);
		}
		
		private static void notifyOnChange(int field)
		{
			for (GroupListener listener : listeners)
			{
				listener.changed(field);
			}
		}
		
		public static void addListener(GroupListener listener)
		{
			listeners.add(listener);
		}
		
		public static void removeListener(GroupListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithGroupListener()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithGroupListener.class);
		assertEquals("TestClassWithGroupListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(2, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithGroupListener.aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final int[] called = new int[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0]++;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithGroupListener.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithGroupListener.setAa(1);
		assertEquals(0, called[0]);
		
		// bb
		obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithGroupListener.bb", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithGroupListener.setBb(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithGroupListener.setBb(1);
		assertEquals(0, called[0]);
		
		attr.addListener(listener);
		
		called[0] = 0;
		TestClassWithGroupListener.setAa(1);
		assertEquals(0, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		TestClassWithGroupListener.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private static class TestClassWithOther
	{
		public static ObjectReflectionGroupTest.TestClassWithEverything aa;
	}
	
	@Test
	public void testAsGroup()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithOther.class);
		assertEquals("TestClassWithOther", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithOther.aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(ObjectReflectionGroupTest.TestClassWithEverything.class, attr.getType());
		assertEquals(false, attr.canListen());
		
		AttributeGroup ag = attr.asGroup();
		assertNull(ag);
		
		TestClassWithOther.aa = new ObjectReflectionGroupTest.TestClassWithEverything();
		
		ag = attr.asGroup();
		assertNotNull(ag);
		
		ObjectReflectionGroupTest.testWithEverything(TestClassWithOther.aa, ag);
	}
	
	private static enum TestEnum
	{
		EnumValue1,
		EnumValue2,
		EnumValue3
	}
	
	private static class TestClassWithEnum
	{
		public static TestEnum aa;
	}
	
	@Test
	public void testWithPublicEnum()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithEnum.class);
		assertEquals("TestClassWithEnum", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<?> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithEnum.aa", attr.getName());
		assertEquals(TestEnum.class, attr.getType());
		assertEquals(null, attr.getValue());
		TestClassWithEnum.aa = TestEnum.EnumValue2;
		assertEquals(TestEnum.EnumValue2, attr.getValue());
		attr.setValue(TestEnum.EnumValue1);
		assertEquals(TestEnum.EnumValue1, attr.getValue());
		
		AttributeValueRange range = attr.getValueRange();
		assertNotNull(range);
		assertTrue(range.canBeNull());
		assertNull(range.getComparator());
		assertNull(range.getMax());
		assertNull(range.getMin());
		
		Collection<?> values = range.getPossibleValues();
		assertNotNull(values);
		assertEquals(3, values.size());
		
		it = values.iterator();
		assertEquals(TestEnum.EnumValue1, it.next());
		assertEquals(TestEnum.EnumValue2, it.next());
		assertEquals(TestEnum.EnumValue3, it.next());
	}
	
	private static class TestComparator implements Comparator<String>
	{
		public int compare(String o1, String o2)
		{
			return 0;
		}
	}
	
	private static class TestClassWithAnnotations
	{
		@NotNull
		public static int aa;
		
		@Range(min = 1, maxf = 5)
		public static long bb;
		
		@CompareWith(TestComparator.class)
		public static String cc;
	}
	
	@Test
	public void testWithAnnotations()
	{
		ReflectionGroup group = new ReflectionGroup(TestClassWithAnnotations.class);
		assertEquals("TestClassWithAnnotations", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithAnnotations.aa", attr.getName());
		AttributeValueRange range = attr.getValueRange();
		assertFalse(range.canBeNull());
		assertNull(range.getMin());
		assertNull(range.getMax());
		assertNull(range.getPossibleValues());
		assertNull(range.getComparator());
		
		// bb
		obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithAnnotations.bb", attr.getName());
		range = attr.getValueRange();
		assertFalse(range.canBeNull());
		assertEquals(Long.valueOf(1), range.getMin());
		assertEquals(Long.valueOf(5), range.getMax());
		assertNull(range.getPossibleValues());
		assertNull(range.getComparator());
		
		// cc
		obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ClassReflectionGroupTest$TestClassWithAnnotations.cc", attr.getName());
		range = attr.getValueRange();
		assertTrue(range.canBeNull());
		assertNull(range.getMin());
		assertNull(range.getMax());
		assertNull(range.getPossibleValues());
		assertEquals(TestComparator.class, range.getComparator().getClass());
	}
	
}
