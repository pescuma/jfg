package jfg;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractListenerAttributeTest
{
	private AbstractListenerAttribute attrib;
	
	@Before
	public void setup()
	{
		attrib = new AbstractListenerAttribute() {
			public void addListener(AttributeListener listener)
			{
			}
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
			public void removeListener(AttributeListener listener)
			{
			}
			public void setValue(Object obj)
			{
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
		assertEquals(true, attrib.canWrite());
	}
	
	@Test
	public void testCanListen()
	{
		assertEquals(true, attrib.canListen());
	}
	
	@Test
	public void testAsGroup()
	{
		assertEquals(null, attrib.asGroup());
	}
	
}
