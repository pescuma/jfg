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

package org.pescuma.jfg.gui.swt;


import org.eclipse.swt.widgets.Display;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiWidget;

public abstract class AbstractSWTCopyManager implements GuiCopyManager
{
	protected final JfgFormComposite composite;
	protected final JfgFormData data;
	
	public AbstractSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		this.composite = composite;
		this.data = data;
	}
	
	protected Display getDisplay()
	{
		return composite.getDisplay();
	}
	
	public void modelChanged(GuiWidget widget)
	{
		if (data.updateGuiWhenModelChanges)
			widget.copyToGUI();
	}
	
	public void guiUpdated(GuiWidget widget)
	{
		composite.onGuiUpdated(widget);
	}
}
