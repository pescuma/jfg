package jfg.reflect;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jfg.Attribute;
import jfg.AttributeListener;
import jfg.AttributeListenerConverter;

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
		assertSimpleFieldAttribute(obj);
		
		Attribute attr = (Attribute) obj;
		assertEquals("aa", attr.getName());
		assertEquals(int.class, attr.getType());
		assertEquals(new Integer(0), attr.getValue());
		tc.aa = 1;
		assertEquals(new Integer(1), attr.getValue());
		attr.setValue(new Integer(2));
		assertEquals(new Integer(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("bb", attr.getName());
		assertEquals(long.class, attr.getType());
		assertEquals(new Long(0), attr.getValue());
		tc.bb = 1;
		assertEquals(new Long(1), attr.getValue());
		attr.setValue(new Long(2));
		assertEquals(new Long(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("cc", attr.getName());
		assertEquals(String.class, attr.getType());
		assertEquals(null, attr.getValue());
		tc.cc = "x";
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
		
	}
	
	private void assertSimpleFieldAttribute(Object obj)
	{
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals(true, attr.canWrite());
		assertEquals(false, attr.canListen());
		
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
			}
		};
		
		try
		{
			attr.addListener(listener);
			fail();
		}
		catch (RuntimeException e)
		{
		}
		
		try
		{
			attr.removeListener(listener);
			fail();
		}
		catch (RuntimeException e)
		{
		}
		
		assertEquals(null, attr.getValueRange());
	}
	
	private static class TestClassWithPublicFieldsAndGettersSetters
	{
		public static float xx;
		
		public int aa;
		public long bb;
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
		assertSimpleFieldAttribute(obj);
		
		Attribute attr = (Attribute) obj;
		assertEquals("aa", attr.getName());
		assertEquals(int.class, attr.getType());
		assertEquals(new Integer(100), attr.getValue());
		tc.aa = 1;
		assertEquals(new Integer(101), attr.getValue());
		attr.setValue(new Integer(2));
		assertEquals(new Integer(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("bb", attr.getName());
		assertEquals(long.class, attr.getType());
		assertEquals(new Long(100), attr.getValue());
		tc.bb = 1;
		assertEquals(new Long(101), attr.getValue());
		attr.setValue(new Long(2));
		assertEquals(new Long(102), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("cc", attr.getName());
		assertEquals(String.class, attr.getType());
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
		assertSimpleFieldAttribute(obj);
		
		Attribute attr = (Attribute) obj;
		assertEquals("aa", attr.getName());
		assertEquals(int.class, attr.getType());
		assertEquals(new Integer(0), attr.getValue());
		tc.setAa(1);
		assertEquals(new Integer(1), attr.getValue());
		attr.setValue(new Integer(2));
		assertEquals(new Integer(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("bb", attr.getName());
		assertEquals(long.class, attr.getType());
		assertEquals(new Long(0), attr.getValue());
		tc.setBb(1);
		assertEquals(new Long(1), attr.getValue());
		attr.setValue(new Long(2));
		assertEquals(new Long(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("cc", attr.getName());
		assertEquals(String.class, attr.getType());
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
		
		Collection<Object> attributes = group.getAttributes();
		assertEquals(3, attributes.size());
		
		Iterator<Object> it = attributes.iterator();
		
		// aa
		Object obj = it.next();
		assertSimpleFieldAttribute(obj);
		
		Attribute attr = (Attribute) obj;
		assertEquals("aa", attr.getName());
		assertEquals(int.class, attr.getType());
		assertEquals(new Integer(100), attr.getValue());
		tc.aa = 1;
		assertEquals(new Integer(101), attr.getValue());
		attr.setValue(new Integer(2));
		assertEquals(new Integer(112), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("bb", attr.getName());
		assertEquals(long.class, attr.getType());
		assertEquals(new Long(0), attr.getValue());
		tc.setBb(1);
		assertEquals(new Long(1), attr.getValue());
		attr.setValue(new Long(2));
		assertEquals(new Long(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("cc", attr.getName());
		assertEquals(String.class, attr.getType());
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
		
		// dd
		Object obj = it.next();
		assertSimpleFieldAttribute(obj);
		
		Attribute attr = (Attribute) obj;
		assertEquals("dd", attr.getName());
		assertEquals(int.class, attr.getType());
		assertEquals(new Integer(0), attr.getValue());
		tc.dd = 1;
		assertEquals(new Integer(1), attr.getValue());
		attr.setValue(new Integer(2));
		assertEquals(new Integer(2), attr.getValue());
		
		// aa
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		
		attr = (Attribute) obj;
		assertEquals("aa", attr.getName());
		assertEquals(int.class, attr.getType());
		assertEquals(new Integer(100), attr.getValue());
		tc.aa = 1;
		assertEquals(new Integer(101), attr.getValue());
		attr.setValue(new Integer(2));
		assertEquals(new Integer(112), attr.getValue());
		
		// ee
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("ee", attr.getName());
		assertEquals(long.class, attr.getType());
		assertEquals(new Long(0), attr.getValue());
		tc.setEe(1);
		assertEquals(new Long(1), attr.getValue());
		attr.setValue(new Long(2));
		assertEquals(new Long(2), attr.getValue());
		
		// bb
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("bb", attr.getName());
		assertEquals(long.class, attr.getType());
		assertEquals(new Long(0), attr.getValue());
		tc.setBb(1);
		assertEquals(new Long(1), attr.getValue());
		attr.setValue(new Long(2));
		assertEquals(new Long(2), attr.getValue());
		
		// cc
		obj = it.next();
		assertSimpleFieldAttribute(obj);
		attr = (Attribute) obj;
		assertEquals("cc", attr.getName());
		assertEquals(String.class, attr.getType());
		assertEquals(null, attr.getValue());
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("y", attr.getValue());
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
		assertEquals("aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final boolean[] called = new boolean[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0] = true;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = false;
		tc.setAa(1);
		assertTrue(called[0]);
		
		attr.removeListener(listener);
		
		called[0] = false;
		tc.setAa(1);
		assertFalse(called[0]);
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
		assertEquals("aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final boolean[] called = new boolean[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0] = true;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = false;
		tc.setAa(1);
		assertTrue(called[0]);
		
		attr.removeListener(listener);
		
		called[0] = false;
		tc.setAa(1);
		assertFalse(called[0]);
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
		assertSimpleFieldAttribute(obj);
		
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
		
		// aa
		obj = it.next();
		assertTrue(obj instanceof Attribute);
		
		Attribute attr = (Attribute) obj;
		assertEquals("aa", attr.getName());
		assertEquals(true, attr.canWrite());
		assertEquals(long.class, attr.getType());
		
		assertEquals(true, attr.canListen());
		
		final boolean[] called = new boolean[1];
		AttributeListener listener = new AttributeListener() {
			public void onChange()
			{
				called[0] = true;
			}
		};
		
		attr.addListener(listener);
		
		called[0] = false;
		tc.setAa(1);
		assertTrue(called[0]);
		
		attr.removeListener(listener);
		
		called[0] = false;
		tc.setAa(1);
		assertFalse(called[0]);
	}
}
