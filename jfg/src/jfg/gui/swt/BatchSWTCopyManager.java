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

import java.util.HashSet;
import java.util.Set;

import jfg.gui.GuiWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class BatchSWTCopyManager extends AbstractSWTCopyManager
{
	private final Set<GuiWidget> changedWidgets = new HashSet<GuiWidget>();
	private final Runnable copyTimer = new Runnable() {
		public void run()
		{
			getDisplay().timerExec(-1, copyTimer);
			for (GuiWidget widget : changedWidgets)
				widget.copyToModel();
			changedWidgets.clear();
		}
	};
	
	public BatchSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
		
		composite.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event)
			{
				copyTimer.run();
			}
		});
	}
	
	public void guiChanged(GuiWidget widget)
	{
		getDisplay().timerExec(-1, copyTimer);
		changedWidgets.add(widget);
		getDisplay().timerExec(data.modelUpdateTimeout, copyTimer);
	}
}
