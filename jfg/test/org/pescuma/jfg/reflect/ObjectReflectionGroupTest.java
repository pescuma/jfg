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
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
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

@SuppressWarnings("unused")
public class ObjectReflectionGroupTest
{
	private static class TestClassNoData
	{
	}
	
	@Test
	public void testNoData()
	{
		ReflectionGroup group = new ReflectionGroup(new TestClassNoData());
		assertEquals("TestClassNoData", group.getName());
		assertEquals(0, group.getAttributes().size());
	}
	
	private static class TestClassWithPublicFields
	{
		public int aa;
		public long bb;
		public String cc;
		protected int dd;
	}
	
	@Test
	public void testWithPublicFields()
	{
		TestClassWithPublicFields tc = new TestClassWithPublicFields();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithPublicFields", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithPublicFields.aa", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithPublicFields.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.bb = 1;
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithPublicFields.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.cc = "x";
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
		public static float xx;
		
		public int aa;
		protected long bb;
		public String cc;
		protected int dd;
		
		public int getAa()
		{
			return 100 + aa;
		}
		
		public void setAa(int aa)
		{
			this.aa = 10 + aa;
		}
		
		public long getBb()
		{
			return 100 + bb;
		}
		
		public void setCc(String cc)
		{
			this.cc = "-" + cc;
		}
	}
	
	@Test
	public void testWithPublicFieldsAndGettersSetters()
	{
		TestClassWithPublicFieldsAndGettersSetters tc = new TestClassWithPublicFieldsAndGettersSetters();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithPublicFieldsAndGettersSetters", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertFieldAttribute(obj, false, false, false, true);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.bb", attr.getName());
		assertEquals(long.class, attr.getType());
		AttributeValueRange range = attr.getValueRange();
		assertFalse(range.canBeNull());
		assertNull(range.getComparator());
		assertNull(range.getMax());
		assertNull(range.getMin());
		assertNull(range.getPossibleValues());
		assertEquals(Long.valueOf(100), attr.getValue());
		tc.bb = 1;
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
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.cc = "x";
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("-y", attr.getValue());
	}
	
	private static class TestClassWithOnlyMethods
	{
		private static int xx;
		private int aa;
		private long bb;
		private String cc;
		
		public static int getXx()
		{
			return xx;
		}
		
		public static void setXx(int x)
		{
			xx = x;
		}
		
		public int getAa()
		{
			return aa;
		}
		
		public void setAa(int aa)
		{
			this.aa = aa;
		}
		
		public long getBb()
		{
			return bb;
		}
		
		public void setBb(long bb)
		{
			this.bb = bb;
		}
		
		public String getCc()
		{
			return cc;
		}
		
		public void setCc(String cc)
		{
			this.cc = cc;
		}
	}
	
	@Test
	public void testWithOnlyMethods()
	{
		TestClassWithOnlyMethods tc = new TestClassWithOnlyMethods();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithOnlyMethods", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithOnlyMethods.aa", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		tc.setAa(1);
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithOnlyMethods.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithOnlyMethods.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
	}
	
	static class TestClassWithEverything
	{
		public int aa;
		private long bb;
		private String cc;
		
		public int getAa()
		{
			return 100 + aa;
		}
		
		public void setAa(int aa)
		{
			this.aa = 10 + aa;
		}
		
		public long getBb()
		{
			return bb;
		}
		
		public void setBb(long bb)
		{
			this.bb = bb;
		}
		
		public String getCc()
		{
			return cc;
		}
		
		public void setCc(String cc)
		{
			this.cc = cc;
		}
	}
	
	@Test
	public void testWithEverything()
	{
		TestClassWithEverything tc = new TestClassWithEverything();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithEverything", group.getName());
		
		testWithEverything(tc, group);
	}
	
	static void testWithEverything(TestClassWithEverything tc, AttributeGroup group)
	{
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEverything.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEverything.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEverything.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
	}
	
	private static class TestClassWithExtends extends TestClassWithEverything
	{
		public int dd;
		private long ee;
		
		public long getEe()
		{
			return ee;
		}
		
		public void setEe(long ee)
		{
			this.ee = ee;
		}
	}
	
	@Test
	public void testWithExtends()
	{
		TestClassWithExtends tc = new TestClassWithExtends();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithExtends", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(5, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEverything.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEverything.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEverything.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
		
		// dd
		obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithExtends.dd", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		tc.dd = 1;
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// ee
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithExtends.ee", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setEe(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
	}
	
	private static class TestClassWithFieldListener
	{
		private long aa;
		private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
		
		public long getAa()
		{
			return aa;
		}
		
		public void setAa(long aa)
		{
			this.aa = aa;
			
			notifyOnChange();
		}
		
		private void notifyOnChange()
		{
			for (AttributeListener listener : listeners)
			{
				listener.onChange();
			}
		}
		
		public void addAaListener(AttributeListener listener)
		{
			listeners.add(listener);
		}
		
		public void removeAaListener(AttributeListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithFieldListener()
	{
		TestClassWithFieldListener tc = new TestClassWithFieldListener();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithFieldListener.aa", attr.getName());
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
		tc.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private interface StrangeListener
	{
		boolean mudou();
		
		void add(int i);
	}
	
	private static class TestClassWithStrangeFieldListener
	{
		private long aa;
		private final List<StrangeListener> listeners = new ArrayList<StrangeListener>();
		
		public long getAa()
		{
			return aa;
		}
		
		public void setAa(long aa)
		{
			this.aa = aa;
			
			notifyOnChange();
		}
		
		private void notifyOnChange()
		{
			for (StrangeListener listener : listeners)
			{
				listener.mudou();
			}
		}
		
		public void addAaListener(StrangeListener listener)
		{
			listeners.add(listener);
		}
		
		public void removeAaListener(StrangeListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithStrangeFieldListener()
	{
		TestClassWithStrangeFieldListener tc = new TestClassWithStrangeFieldListener();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithStrangeFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithStrangeFieldListener.aa", attr.getName());
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
		tc.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
		
		attr.addListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
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
		private long aa;
		private final List<WrongListener> listeners = new ArrayList<WrongListener>();
		
		public long getAa()
		{
			return aa;
		}
		
		public void setAa(long aa)
		{
			this.aa = aa;
			
			notifyOnChange();
		}
		
		private void notifyOnChange()
		{
			for (WrongListener listener : listeners)
			{
				listener.mudou();
			}
		}
		
		public void addAaListener(WrongListener listener)
		{
			listeners.add(listener);
		}
		
		public void removeAaListener(WrongListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithWrongFieldListener()
	{
		TestClassWithWrongFieldListener tc = new TestClassWithWrongFieldListener();
		
		// First without knowing how to create listener
		
		ReflectionGroup group = new ReflectionGroup(tc);
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
		group = new ReflectionGroup(tc, data);
		assertEquals("TestClassWithWrongFieldListener", group.getName());
		
		attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		it = attributes.iterator();
		
		obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithWrongFieldListener.aa", attr.getName());
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
		tc.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
		
		attr.addListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private static interface GroupListener
	{
		void changed(int field);
	}
	
	private static class TestClassWithGroupListener
	{
		private long aa;
		private long bb;
		private final List<GroupListener> listeners = new ArrayList<GroupListener>();
		
		public long getAa()
		{
			return aa;
		}
		
		public void setAa(long aa)
		{
			this.aa = aa;
			
			notifyOnChange(1);
		}
		
		public long getBb()
		{
			return bb;
		}
		
		public void setBb(long bb)
		{
			this.bb = bb;
			
			notifyOnChange(2);
		}
		
		private void notifyOnChange(int field)
		{
			for (GroupListener listener : listeners)
			{
				listener.changed(field);
			}
		}
		
		public void addListener(GroupListener listener)
		{
			listeners.add(listener);
		}
		
		public void removeListener(GroupListener listener)
		{
			listeners.remove(listener);
		}
	}
	
	@Test
	public void testWithGroupListener()
	{
		TestClassWithGroupListener tc = new TestClassWithGroupListener();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithGroupListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(2, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithGroupListener.aa", attr.getName());
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
		tc.setAa(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
		
		// bb
		obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithGroupListener.bb", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		attr.addListener(listener);
		
		called[0] = 0;
		tc.setBb(1);
		assertEquals(1, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setBb(1);
		assertEquals(0, called[0]);
		
		attr.addListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
		
		attr.removeListener(listener);
		
		called[0] = 0;
		tc.setAa(1);
		assertEquals(0, called[0]);
	}
	
	private static class TestClassWithOther
	{
		public TestClassWithEverything aa;
	}
	
	@Test
	public void testAsGroup()
	{
		TestClassWithOther tc = new TestClassWithOther();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithOther", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithOther.aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(TestClassWithEverything.class, attr.getType());
		assertEquals(false, attr.canListen());
		
		AttributeGroup ag = attr.asGroup();
		assertNull(ag);
		
		tc.aa = new TestClassWithEverything();
		
		ag = attr.asGroup();
		assertNotNull(ag);
		
		testWithEverything(tc.aa, ag);
	}
	
	private static enum TestEnum
	{
		EnumValue1,
		EnumValue2,
		EnumValue3
	}
	
	private static class TestClassWithEnum
	{
		public TestEnum aa;
	}
	
	@Test
	public void testWithPublicEnum()
	{
		TestClassWithEnum tc = new TestClassWithEnum();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithEnum", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<?> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithEnum.aa", attr.getName());
		assertEquals(TestEnum.class, attr.getType());
		assertEquals(null, attr.getValue());
		tc.aa = TestEnum.EnumValue2;
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
		public int aa;
		
		@Range(min = 1, maxf = 5)
		public long bb;
		
		@CompareWith(TestComparator.class)
		public String cc;
	}
	
	@Test
	public void testWithAnnotations()
	{
		TestClassWithAnnotations tc = new TestClassWithAnnotations();
		ReflectionGroup group = new ReflectionGroup(tc);
		assertEquals("TestClassWithAnnotations", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		
		Attribute attr = (Attribute) obj;
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithAnnotations.aa", attr.getName());
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
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithAnnotations.bb", attr.getName());
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
		assertEquals("org.pescuma.jfg.reflect.ObjectReflectionGroupTest$TestClassWithAnnotations.cc", attr.getName());
		range = attr.getValueRange();
		assertTrue(range.canBeNull());
		assertNull(range.getMin());
		assertNull(range.getMax());
		assertNull(range.getPossibleValues());
		assertEquals(TestComparator.class, range.getComparator().getClass());
	}
}
