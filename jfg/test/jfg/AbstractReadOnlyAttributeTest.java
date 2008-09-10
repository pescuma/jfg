package jfg;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractReadOnlyAttributeTest
{
	private AbstractReadOnlyAttribute attrib;
	
	@Before
	public void setup()
	{
		attrib = new AbstractReadOnlyAttribute() {
			
			public String getName()
			{
				return null;
			}
			
			public Object getType()
			{
				return null;
			}
			
			public Object getValue()
			{
				return null;
			}
		};
	}
	
	@Test
	public void testGetValueRange()
	{
		assertEquals(null, attrib.getValueRange());
	}
	
	@Test
	public void testCanWrite()
	{
		assertEquals(false, attrib.canWrite());
	}
	
	@Test
	public void testCanListen()
	{
		assertEquals(false, attrib.canListen());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSetValue()
	{
		attrib.setValue(null);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testAddListener()
	{
		attrib.addListener(new AttributeListener() {
			public void onChange()
			{
			}
		});
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveListener()
	{
		attrib.removeListener(new AttributeListener() {
			public void onChange()
			{
			}
		});
	}
	
	@Test
	public void testAsGroup()
	{
		assertEquals(null, attrib.asGroup());
	}
	
}
