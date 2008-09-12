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
