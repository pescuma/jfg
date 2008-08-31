package jfg.gui.swt;

import static jfg.gui.swt.SWTRealBuilder.*;
import static org.junit.Assert.*;

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
		assertEquals(true, isValidNumber("000", 0));
		assertEquals(true, isValidNumber("123123", 0));
		assertEquals(true, isValidNumber("-6587", 0));
		assertEquals(true, isValidNumber("", 0));
		assertEquals(true, isValidNumber("000", 1));
		assertEquals(true, isValidNumber("123123", 1));
		assertEquals(true, isValidNumber("", 1));
		assertEquals(true, isValidNumber("123123,", 1));
		assertEquals(true, isValidNumber("1231,23", 1));
		assertEquals(true, isValidNumber("12,3123", 1));
		assertEquals(true, isValidNumber(",123123", 1));
		assertEquals(true, isValidNumber(",", 1));
		
		assertEquals(false, isValidNumber("658a", 0));
		assertEquals(false, isValidNumber("65a87", 0));
		assertEquals(false, isValidNumber("a6587", 0));
		assertEquals(false, isValidNumber("a-6587", 0));
		assertEquals(false, isValidNumber("-a6587", 0));
		assertEquals(false, isValidNumber("-6587", 1));
		assertEquals(false, isValidNumber("658a", 1));
		assertEquals(false, isValidNumber("65a87", 1));
		assertEquals(false, isValidNumber("a6587", 1));
		assertEquals(false, isValidNumber("a-6587", 1));
		assertEquals(false, isValidNumber("-a6587", 1));
	}
}
