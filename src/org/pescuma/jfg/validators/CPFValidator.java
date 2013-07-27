package org.pescuma.jfg.validators;

import java.util.regex.Pattern;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.WidgetValidator;

public class CPFValidator implements WidgetValidator
{
	private static final Pattern re = Pattern.compile("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}");
	
	@Override
	public boolean isValid(Attribute attrib, Object value)
	{
		if (!(value instanceof String))
			return false;
		
		String text = (String) value;
		
		// Others worry about empty text
		if (text.length() < 1)
			return true;
		
		if (!re.matcher(text).matches())
			return false;
		
		text = text.replace(".", "");
		text = text.replace("-", "");
		
		int[] cpf = new int[11];
		for (int i = 0; i < cpf.length; i++)
		{
			char c = text.charAt(i);
			cpf[i] = c - '0';
		}
		
		boolean allSameDigit = true;
		for (int i = 1; i < cpf.length; i++)
		{
			if (cpf[i] != cpf[0])
			{
				allSameDigit = false;
				break;
			}
		}
		if (allSameDigit)
			return false;
		
		int d1 = 10 * cpf[0] + 9 * cpf[1] + 8 * cpf[2];
		d1 += 7 * cpf[3] + 6 * cpf[4] + 5 * cpf[5];
		d1 += 4 * cpf[6] + 3 * cpf[7] + 2 * cpf[8];
		d1 = 11 - d1 % 11;
		if (d1 >= 10)
			d1 = 0;
		
		int d2 = 11 * cpf[0] + 10 * cpf[1] + 9 * cpf[2];
		d2 += 8 * cpf[3] + 7 * cpf[4] + 6 * cpf[5];
		d2 += 5 * cpf[6] + 4 * cpf[7] + 3 * cpf[8];
		d2 += 2 * d1;
		d2 = 11 - d2 % 11;
		if (d2 >= 10)
			d2 = 0;
		
		return d1 == cpf[9] && d2 == cpf[10];
	}
	
}
