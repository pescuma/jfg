/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.calendarcombo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractSettings implements ISettings
{
	private Font mCarbonFont = null;
	private Font mWindowsPopupFont = null;
	
	@Override
	public int getArrowLeftSpacing()
	{
		return 6;
	}
	
	@Override
	public int getArrowTopSpacing()
	{
		return 4;
	}
	
	@Override
	public int getOneDateBoxSize()
	{
		return 11;
	}
	
	@Override
	public int getBoxSpacer()
	{
		return 6;
	}
	
	@Override
	public int getCalendarHeight()
	{
		return 164;
	}
	
	@Override
	public int getCalendarWidth()
	{
		return 154; // 154?
	}
	
	@Override
	public int getCalendarWidthMacintosh()
	{
		return 154;
	}
	
	@Override
	public int getCalendarHeightMacintosh()
	{
		return 168;
	}
	
	@Override
	public int getDatesLeftMargin()
	{
		return 15;
	}
	
	@Override
	public int getDatesRightMargin()
	{
		return 17;
	}
	
	@Override
	public int getHeaderHeight()
	{
		return 16;
	}
	
	@Override
	public int getHeaderLeftMargin()
	{
		return 6;
	}
	
	@Override
	public int getHeaderRightMargin()
	{
		return 4;
	}
	
	@Override
	public int getHeaderTopMargin()
	{
		return 4;
	}
	
	@Override
	public boolean showCalendarInRightCorner()
	{
		return true;
	}
	
	@Override
	public String getDateFormat()
	{
		// get the date format from the locale, format it to be 4 digit year
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
		String pattern = ((SimpleDateFormat) df).toPattern();
		// some locales (Romanian, Hungarian) have 4 digit years already in their locale, if so, don't do a replacement
		// or we'll end up with way too many y's
		if (pattern.indexOf("yyyy") == -1)
			pattern = pattern.replaceAll("yy", "yyyy");
		
		return pattern;
	}
	
	@Override
	public int getButtonHeight()
	{
		return 20;
	}
	
	@Override
	public int getButtonsHorizontalSpace()
	{
		return 16;
	}
	
	@Override
	public int getCarbonButtonsHorizontalSpace()
	{
		return 0;
	}
	
	@Override
	public int getButtonVerticalSpace()
	{
		return 133;
	}
	
	@Override
	public int getCarbonButtonVerticalSpace()
	{
		return 138;
	}
	
	@Override
	public int getButtonWidth()
	{
		return 45;
	}
	
	@Override
	public int getButtonWidthCarbon()
	{
		return 65;
	}
	
	@Override
	public String getNoneText()
	{
		return "None";
	}
	
	@Override
	public String getTodayText()
	{
		return "Today";
	}
	
	@Override
	public String getNoDateSetText()
	{
		return "";
	}
	
	@Override
	public boolean showMonthPickerOnMonthNameMousePress()
	{
		return true;
	}
	
	@Override
	public int getCarbonButtonHeight()
	{
		return 26;
	}
	
	@Override
	public int getCarbonButtonWidth()
	{
		return 25;
	}
	
	@Override
	public int getGTKButtonWidth()
	{
		return 25;
	}
	
	@Override
	public int getWindowsButtonWidth()
	{
		return 19;
	}
	
	@Override
	public Locale getLocale()
	{
		return Locale.getDefault();
	}
	
	@Override
	public Font getCarbonDrawFont()
	{
		if (mCarbonFont == null || mCarbonFont.isDisposed())
			mCarbonFont = new Font(Display.getDefault(), "Arial", 12, SWT.NORMAL);
		
		return mCarbonFont;
	}
	
	@Override
	public Font getWindowsMonthPopupDrawFont()
	{
		if (mWindowsPopupFont == null || mWindowsPopupFont.isDisposed())
			mWindowsPopupFont = new Font(Display.getDefault(), "Arial", 8, SWT.NORMAL);
		
		return mWindowsPopupFont;
	}
	
	@Override
	public List getAdditionalDateFormats()
	{
		return null;
	}
	
	@Override
	public boolean keyboardNavigatesCalendar()
	{
		return true;
	}
	
	@Override
	public char[] getAcceptedDateSeparatorChars()
	{
		return new char[] { '/', '-', '.' };
	}
	
	@Override
	public char getCarbonArrowUpChar()
	{
		return '-';
	}
	
	@Override
	public char getCarbonArrowDownChar()
	{
		return '+';
	}
	
	/*	public int getNoneAccelerator() {		
			return SWT.CTRL + 'n';
		}

		public int getTodayAccelerator() {
			return SWT.CTRL + 't';
		}
	*/

	@Override
	public boolean allowEmptyDate()
	{
		return true;
	}
	
	@Override
	public Calendar getDefaultDate()
	{
		return Calendar.getInstance();
	}
}
