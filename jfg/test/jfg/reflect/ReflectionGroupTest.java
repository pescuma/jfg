package jfg.reflect;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.AttributeListener;
import jfg.AttributeListenerConverter;
import jfg.AttributeValueRange;
import jfg.model.ann.CompareWith;
import jfg.model.ann.NotNull;
import jfg.model.ann.Range;

import org.junit.Test;

public class ReflectionGroupTest
{
	private static class TestClassNoData
	{
	}
	
	@Test
	public void testNoData()
	{
		ObjectReflectionGroup group = new ObjectReflectionGroup(new TestClassNoData());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithPublicFields", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithPublicFields.aa", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithPublicFields.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.bb = 1;
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithPublicFields.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.cc = "x";
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
		
	}
	
	private void assertSimpleFieldAttribute(Object obj, Class<?> cls)
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
	
	private void assertFieldAttribute(Object obj, boolean canWrite, boolean canListen, boolean asGroup, boolean hasRange)
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithPublicFieldsAndGettersSetters", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertFieldAttribute(obj, false, false, false, true);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.bb", attr.getName());
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
		catch (ObjectReflectionException e)
		{
			
		}
		assertEquals(Long.valueOf(101), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithPublicFieldsAndGettersSetters.cc", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithOnlyMethods", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithOnlyMethods.aa", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		tc.setAa(1);
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithOnlyMethods.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithOnlyMethods.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
	}
	
	private static class TestClassWithEverything
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithEverything", group.getName());
		
		testWithEverything(tc, group);
	}
	
	private void testWithEverything(TestClassWithEverything tc, AttributeGroup group)
	{
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEverything.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEverything.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEverything.cc", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithExtends", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(5, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEverything.aa", attr.getName());
		assertEquals(Integer.valueOf(100), attr.getValue());
		tc.aa = 1;
		assertEquals(Integer.valueOf(101), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEverything.bb", attr.getName());
		assertEquals(Long.valueOf(0), attr.getValue());
		tc.setBb(1);
		assertEquals(Long.valueOf(1), attr.getValue());
		attr.setValue(Long.valueOf(2));
		assertEquals(Long.valueOf(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj, String.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEverything.cc", attr.getName());
		assertEquals(null, attr.getValue());
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
		
		// dd
		obj = it.next();
		assertSimpleFieldAttribute(obj, int.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithExtends.dd", attr.getName());
		assertEquals(Integer.valueOf(0), attr.getValue());
		tc.dd = 1;
		assertEquals(Integer.valueOf(1), attr.getValue());
		attr.setValue(Integer.valueOf(2));
		assertEquals(Integer.valueOf(2), attr.getValue());
		
		// ee
		obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithExtends.ee", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithFieldListener.aa", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithStrangeFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithStrangeFieldListener.aa", attr.getName());
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
		
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithWrongFieldListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		Object obj = it.next();
		assertSimpleFieldAttribute(obj, long.class);
		
		// Now with the converter
		
		ObjectReflectionData data = new ObjectReflectionData();
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
		group = new ObjectReflectionGroup(tc, data);
		assertEquals("TestClassWithWrongFieldListener", group.getName());
		
		attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		it = attributes.iterator();
		
		obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithWrongFieldListener.aa", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithGroupListener", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(2, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithGroupListener.aa", attr.getName());
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
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithGroupListener.bb", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithOther", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithOther.aa", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithEnum", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(1, attributes.size());
		
		Iterator<?> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithEnum.aa", attr.getName());
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
		ObjectReflectionGroup group = new ObjectReflectionGroup(tc);
		assertEquals("TestClassWithAnnotations", group.getName());
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertFieldAttribute(obj, true, false, false, true);
		
		Attribute attr = (Attribute) obj;
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithAnnotations.aa", attr.getName());
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
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithAnnotations.bb", attr.getName());
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
		assertEquals("jfg.reflect.ReflectionGroupTest$TestClassWithAnnotations.cc", attr.getName());
		range = attr.getValueRange();
		assertTrue(range.canBeNull());
		assertNull(range.getMin());
		assertNull(range.getMax());
		assertNull(range.getPossibleValues());
		assertEquals(TestComparator.class, range.getComparator().getClass());
	}
}
