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
