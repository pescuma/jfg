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

package org.pescuma.jfg.gui.swt;

import static org.junit.Assert.*;
import static org.pescuma.jfg.gui.swt.SWTRealBuilder.*;

import java.util.Locale;

import org.junit.Test;

public class SWTRealBuilderTest
{
	static
	{
		Locale.setDefault(new Locale("pt"));
	}
	
	@Test
	public void testIsValidNumber()
	{
		assertEquals(true, isValidNumber("000"));
		assertEquals(true, isValidNumber("123123"));
		assertEquals(true, isValidNumber("-6587"));
		assertEquals(true, isValidNumber(""));
		assertEquals(true, isValidNumber("000"));
		assertEquals(true, isValidNumber("123123"));
		assertEquals(true, isValidNumber(""));
		assertEquals(true, isValidNumber("123123,"));
		assertEquals(true, isValidNumber("1231,23"));
		assertEquals(true, isValidNumber("12,3123"));
		assertEquals(true, isValidNumber(",123123"));
		assertEquals(true, isValidNumber(","));
		
		assertEquals(false, isValidNumber("658a"));
		assertEquals(false, isValidNumber("65a87"));
		assertEquals(false, isValidNumber("a6587"));
		assertEquals(false, isValidNumber("a-6587"));
		assertEquals(false, isValidNumber("-a6587"));
	}
}
