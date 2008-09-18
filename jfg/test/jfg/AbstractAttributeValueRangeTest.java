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
