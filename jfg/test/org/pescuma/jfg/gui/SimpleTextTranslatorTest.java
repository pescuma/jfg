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

package org.pescuma.jfg.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pescuma.jfg.gui.SimpleTextTranslator;

public class SimpleTextTranslatorTest
{
	@Test
	public void testFieldName() throws Exception
	{
		SimpleTextTranslator stt = new SimpleTextTranslator();
		assertEquals("A", stt.fieldName("a"));
		assertEquals("A", stt.fieldName("A"));
		assertEquals("Asdf Ghij", stt.fieldName("asdfGhij"));
		assertEquals("", stt.fieldName(""));
		assertEquals("A", stt.fieldName("tetet.a"));
		assertEquals("A", stt.fieldName(".A"));
		assertEquals("Asdf Ghij", stt.fieldName("asd.asd.ad.ad.ad.ad.ad.a.dasd.asdfGhij"));
		assertEquals("SWT Canvas", stt.fieldName("t.SWTCanvas"));
		assertEquals("A SWT Canvas", stt.fieldName("t.aSWTCanvas"));
		assertEquals("SWT", stt.fieldName("t.SWT"));
		assertEquals("A SWT", stt.fieldName("t.aSWT"));
		assertEquals("A - SWT Canvas", stt.fieldName("t.a_SWTCanvas"));
		assertEquals("A SWT - Canvas", stt.fieldName("t.aSWT_Canvas"));
		assertEquals("A SW Ta - Canvas", stt.fieldName("t.aSWTa_Canvas"));
		assertEquals("A - SWT - Canvas", stt.fieldName("t.a_SWT_Canvas"));
		assertEquals("SWT", stt.fieldName("t._SWT"));
		assertEquals("SWT", stt.fieldName("t._SWT_"));
		assertEquals("SWT", stt.fieldName("t.SWT_"));
		assertEquals("A - SWT", stt.fieldName("t.a_SWT"));
	}
	
}
