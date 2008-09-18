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

package jfg.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

class SWTHelper
{
	public static GridLayout createBorderlessGridLayout(int numColumns, boolean makeColumnsEqualWidth)
	{
		GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}
	
	public static Composite createHorizontalComposite(Composite parent, int horizontalSpan)
	{
		Composite ret = new Composite(parent, SWT.NONE);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		ret.setLayoutData(gd);
		
		ret.setLayout(createBorderlessGridLayout(1, true));
		
		return ret;
	}
}
