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
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import static org.eclipse.swt.layout.GridData.*;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

class SWTUtils
{
	public static GridLayout createBorderlessGridLayout(int numColumns, boolean makeColumnsEqualWidth)
	{
		GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}
	
	public static Composite setupHorizontalComposite(Composite composite, int horizontalSpan)
	{
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		composite.setLayoutData(gd);
		
		composite.setLayout(createBorderlessGridLayout(1, true));
		
		return composite;
	}
	
	public static int layoutHintsToGridDataStyle(int layoutHints)
	{
		int style = 0;
		if ((layoutHints & JfgFormData.HORIZONTAL_SHRINK) == 0)
			style += FILL_HORIZONTAL;
		if ((layoutHints & JfgFormData.VERTICAL_FILL) != 0)
			style += FILL_VERTICAL;
		return style;
	}
	
	public static void asyncExec(Runnable run)
	{
		Display display = Display.getDefault();
		
		if (Thread.currentThread() == display.getThread())
			run.run();
		else
			display.asyncExec(run);
	}
	
}
