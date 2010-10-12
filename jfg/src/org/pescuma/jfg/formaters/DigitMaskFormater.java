package org.pescuma.jfg.formaters;

import static java.lang.Character.*;
import static java.lang.Math.*;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.WidgetFormater;

public class DigitMaskFormater implements WidgetFormater
{
	public static final char DIGIT = 'X';
	
	private final String mask;
	private final boolean alignMaskToLeft;
	private final boolean allowTextBefore;
	private final boolean allowTextAfter;
	
	public static final int ALIGN_MASK_RIGHT = 1;
	public static final int ALLOW_TEXT_BEFORE = 2;
	public static final int ALLOW_TEXT_AFTER = 6;
	
	public DigitMaskFormater(String mask)
	{
		this(mask, 0);
	}
	
	public DigitMaskFormater(String mask, int style)
	{
		this.mask = mask;
		alignMaskToLeft = ((style & ALIGN_MASK_RIGHT) == 0);
		allowTextAfter = ((style & ALLOW_TEXT_AFTER) == ALLOW_TEXT_AFTER);
		allowTextBefore = (!allowTextAfter && (style & ALLOW_TEXT_BEFORE) == ALLOW_TEXT_BEFORE);
	}
	
	class BufferAndPos
	{
		private StringBuilder text;
		private int pos;
		
		public BufferAndPos(String text, int pos)
		{
			this.text = new StringBuilder(text);
			this.pos = pos;
		}
		
		public int getPos()
		{
			return pos;
		}
		
		public void setPos(int pos)
		{
			this.pos = pos;
		}
		
		public String getText()
		{
			return text.toString();
		}
		
		public void remove(int start)
		{
			remove(start, start + 1);
		}
		
		public void remove(int start, int end)
		{
			replace(start, end, "");
		}
		
		public void replace(int start, char c)
		{
			replace(start, start + 1, Character.toString(c));
		}
		
		public void replace(int start, int end, String string)
		{
			if (pos >= end)
				pos += string.length() - (end - start);
			else if (pos > start)
				pos = start + Math.min(string.length(), pos - start);
			
			text.replace(start, end, string);
		}
		
		public void insert(int pos, char character)
		{
			insert(pos, Character.toString(character));
		}
		
		public void insert(int pos, String text)
		{
			if (pos < this.pos)
				this.pos += text.length();
			
			this.text.insert(max(0, pos), text);
		}
		
		public int length()
		{
			return text.length();
		}
		
		public char charAt(int i)
		{
			if (i >= text.length())
				return '\0';
			
			return text.charAt(i);
		}
	}
	
	@Override
	public TextAndPos format(Attribute attrib, TextAndPos tp)
	{
		TextAndPos ret = new TextAndPos();
		ret.text = tp.text;
		ret.caretPos = tp.caretPos;
		
		BufferAndPos text = new BufferAndPos(ret.text, ret.caretPos);
		
		String oldSuffix = "";
		if (allowTextAfter)
		{
			oldSuffix = findImpossibleSuffix(ret.text);
			text.remove(text.length() - oldSuffix.length(), text.length());
		}
		
		String oldPrefix = "";
		if (allowTextBefore)
		{
			oldPrefix = findImpossiblePrefix(ret.text);
			text.remove(0, oldPrefix.length());
		}
		
		ret.text = text.getText();
		
		if (alignMaskToLeft)
		{
			for (int i = 0; i < text.length(); i++)
			{
				char mc = getMaskChar(i, text.length());
				char tc = text.charAt(i);
				
				if (mc == DIGIT)
				{
					if (isDigit(tc))
						continue;
					
					text.remove(i);
					--i;
				}
				else if (isDigit(tc))
				{
					text.insert(i, mc);
				}
				else if (mc != tc)
				{
					text.replace(i, mc);
				}
			}
			
			int tlen = text.length();
			int diff = mask.length() - tlen;
			if (diff > 0 && maskIsNonDigits(tlen, tlen + diff))
				text.insert(tlen, mask.substring(tlen));
			
//			// Remove left-over at the end
//			if (mask.length() > text.length())
//			{
//				for (int i = text.length() - 1; i >= 0; --i)
//				{
//					if (isDigit(text.charAt(i)))
//						break;
//					text.remove(i);
//				}
//			}
		}
		else
		{
			for (int i = text.length() - 1; i >= 0; i--)
			{
				char mc = getMaskChar(i, text.length());
				char tc = text.charAt(i);
				
				if (mc == DIGIT)
				{
					if (isDigit(tc))
						continue;
					
					// Do this to keep cursor at correct pos
					if (text.charAt(i + 1) == tc)
						text.remove(i + 1);
					else
						text.remove(i);
				}
				else if (isDigit(tc))
				{
					text.insert(i + 1, mc);
					++i;
				}
				else if (mc != tc)
				{
					text.replace(i, mc);
				}
			}
			
			int diff = mask.length() - text.length();
			if (diff > 0 && maskIsNonDigits(0, diff))
				text.insert(0, mask.substring(0, diff));
			
//			// Remove left-over at the start
//			if (mask.length() > text.length())
//			{
//				while (text.length() > 0 && !isDigit(text.charAt(0)))
//					text.remove(0);
//			}
		}
		
		if (allowTextBefore)
		{
			String prefix = leftNonDigitText(ret.text);
			String p2 = leftNonDigitText(text.getText());
			prefix = removeEqualityAtEnd(prefix, p2);
			
			text.insert(-1, prefix);
			text.insert(-1, oldPrefix);
			
			int prefixLen = prefix.length() + oldPrefix.length();
			if (ret.caretPos < prefixLen)
				text.setPos(ret.caretPos);
		}
		else if (allowTextAfter)
		{
			String suffix = rightNonDigitText(ret.text);
			String s2 = rightNonDigitText(text.getText());
			suffix = removeEqualityAtStart(suffix, s2);
			
			text.insert(text.length(), suffix);
			text.insert(text.length(), oldSuffix);
			
			int suffixLen = suffix.length() + oldSuffix.length();
			
			int pos = tp.caretPos - tp.text.length();
			if (-pos < suffixLen)
				text.setPos(text.length() + pos);
		}
		
		ret.text = text.getText();
		ret.caretPos = text.getPos();
		return ret;
	}
	
	private String findImpossibleSuffix(String text)
	{
		for (int i = 0; i < text.length(); i++)
			if (!maskHasChar(text.charAt(i)))
				return text.substring(i);
		
		return "";
	}
	
	private String findImpossiblePrefix(String text)
	{
		for (int i = text.length() - 1; i >= 0; i--)
			if (!maskHasChar(text.charAt(i)))
				return text.substring(0, i + 1);
		
		return "";
	}
	
	private String removeEqualityAtStart(String text, String str2)
	{
		int textLen = text.length();
		int str2Len = str2.length();
		
		int minLen = min(textLen, str2Len);
		
		for (int i = 0; i < minLen; i++)
			if (text.charAt(i) != str2.charAt(i))
				return text.substring(i + 1);
		
		return text.substring(minLen);
	}
	
	private String removeEqualityAtEnd(String text, String str2)
	{
		int textLen = text.length();
		int str2Len = str2.length();
		
		int minLen = min(textLen, str2Len);
		
		for (int i = 1; i <= minLen; i++)
			if (text.charAt(textLen - i) != str2.charAt(str2Len - i))
				return text.substring(0, textLen - i + 1);
		
		return text.substring(0, textLen - minLen);
	}
	
	private String rightNonDigitText(String text)
	{
		for (int i = text.length() - 1; i >= 0; i--)
			if (isDigit(text.charAt(i)))
				return text.substring(i + 1, text.length());
		return text;
	}
	
	private String leftNonDigitText(String text)
	{
		for (int i = 0; i < text.length(); i++)
			if (isDigit(text.charAt(i)))
				return text.substring(0, i);
		return text;
	}
	
	private boolean maskIsNonDigits(int start, int end)
	{
		start = min(start, mask.length());
		end = min(end, mask.length());
		
		if (start >= end)
			return false;
		
		for (int i = start; i < end; i++)
			if (mask.charAt(i) == DIGIT)
				return false;
		
		return true;
	}
	
	private char getMaskChar(int pos, int textLength)
	{
		if (alignMaskToLeft)
		{
			return (pos >= mask.length() ? DIGIT : mask.charAt(pos));
		}
		else
		{
			int j = textLength - pos;
			return (j > mask.length() ? DIGIT : mask.charAt(mask.length() - j));
		}
	}
	
	private boolean maskHasChar(char c)
	{
		if (isDigit(c))
			return true;
		if (c == DIGIT)
			return false;
		return mask.indexOf(c) >= 0;
	}
}
