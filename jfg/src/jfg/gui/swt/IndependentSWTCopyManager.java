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

import java.util.HashMap;
import java.util.Map;

import jfg.gui.GuiWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class IndependentSWTCopyManager extends AbstractSWTCopyManager
{
	private Map<GuiWidget, Runnable> widgetTimers = new HashMap<GuiWidget, Runnable>();
	
	public IndependentSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
		
		composite.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event)
			{
				for (Runnable run : widgetTimers.values())
					run.run();
				widgetTimers.clear();
			}
		});
	}
	
	public void guiChanged(final GuiWidget widget)
	{
		Runnable copyTimer = widgetTimers.get(widget);
		if (copyTimer == null)
		{
			copyTimer = new Runnable() {
				public void run()
				{
					getDisplay().timerExec(-1, this);
					
					widget.copyToModel();
				}
			};
			widgetTimers.put(widget, copyTimer);
		}
		
		getDisplay().timerExec(-1, copyTimer);
		getDisplay().timerExec(data.modelUpdateTimeout, copyTimer);
	}
}
