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
