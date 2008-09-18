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
