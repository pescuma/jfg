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

import java.util.HashMap;
import java.util.Map;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.gui.GuiWidget;

public class IndependentFixedTimeSWTCopyManager extends AbstractSWTCopyManager
{
	private Map<GuiWidget, Runnable> widgetTimers = new HashMap<GuiWidget, Runnable>();
	
	public IndependentFixedTimeSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
		
		composite.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event)
			{
				while (widgetTimers.size() > 0)
					widgetTimers.values().iterator().next().run();
			}
		});
	}
	
	public void guiChanged(final GuiWidget widget)
	{
		if (widgetTimers.get(widget) != null)
			return;
		
		Runnable copyTimer = new Runnable() {
			public void run()
			{
				getDisplay().timerExec(-1, this);
				widgetTimers.remove(widget);
				
				widget.copyToModel();
			}
		};
		widgetTimers.put(widget, copyTimer);
		
		getDisplay().timerExec(data.modelUpdateTimeout, copyTimer);
	}
}
