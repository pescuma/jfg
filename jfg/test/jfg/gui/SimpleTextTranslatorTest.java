package jfg.gui;

import static org.junit.Assert.*;

import org.junit.Test;

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
