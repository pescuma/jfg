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

package jfg.gui.swt;

import static org.junit.Assert.*;

import org.junit.Test;

public class SWTNumberBuilderTest
{
	@Test
	public void testIsValidNumber()
	{
		assertEquals(true, SWTNumberBuilder.isValidNumber("000", 0));
		assertEquals(true, SWTNumberBuilder.isValidNumber("123123", 0));
		assertEquals(true, SWTNumberBuilder.isValidNumber("-6587", 0));
		assertEquals(true, SWTNumberBuilder.isValidNumber("", 0));
		assertEquals(true, SWTNumberBuilder.isValidNumber("000", 1));
		assertEquals(true, SWTNumberBuilder.isValidNumber("123123", 1));
		assertEquals(true, SWTNumberBuilder.isValidNumber("", 1));
		
		assertEquals(false, SWTNumberBuilder.isValidNumber("658a", 0));
		assertEquals(false, SWTNumberBuilder.isValidNumber("65a87", 0));
		assertEquals(false, SWTNumberBuilder.isValidNumber("a6587", 0));
		assertEquals(false, SWTNumberBuilder.isValidNumber("a-6587", 0));
		assertEquals(false, SWTNumberBuilder.isValidNumber("-a6587", 0));
		assertEquals(false, SWTNumberBuilder.isValidNumber("-6587", 1));
		assertEquals(false, SWTNumberBuilder.isValidNumber("658a", 1));
		assertEquals(false, SWTNumberBuilder.isValidNumber("65a87", 1));
		assertEquals(false, SWTNumberBuilder.isValidNumber("a6587", 1));
		assertEquals(false, SWTNumberBuilder.isValidNumber("a-6587", 1));
		assertEquals(false, SWTNumberBuilder.isValidNumber("-a6587", 1));
	}
}
