package jfg;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

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
		
		assertFalse(it.hasNext());
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
		
		assertFalse(it.hasNext());
	}
	
	private static class TestClassWithOnlyMethods
	{
		private int aa;
		private long bb;
		private String cc;
		
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
		assertEquals(new Integer(100), attr.getValue());
		tc.setAa(1);
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
		tc.setBb(1);
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
		tc.setCc("x");
		assertEquals("x", attr.getValue());
		attr.setValue("y");
		assertEquals("-y", attr.getValue());
		
		assertFalse(it.hasNext());
	}
}
