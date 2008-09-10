package jfg;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractAttributeValueRangeTest
{
	private AbstractAttributeValueRange range;
	
	@Before
	public void setup()
	{
		range = new AbstractAttributeValueRange() {
		};
	}
	
	@Test
	public void testGetComparator()
	{
		assertEquals(null, range.getComparator());
	}
	
	@Test
	public void testGetMax()
	{
		assertEquals(null, range.getMax());
	}
	
	@Test
	public void testGetMin()
	{
		assertEquals(null, range.getMin());
	}
	
	@Test
	public void testCanBeNull()
	{
		assertEquals(true, range.canBeNull());
	}
	
	@Test
	public void testGetPossibleValues()
	{
		assertEquals(null, range.getPossibleValues());
	}
	
}
