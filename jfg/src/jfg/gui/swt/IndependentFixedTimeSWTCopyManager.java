package jfg.gui.swt;

import java.util.HashMap;
import java.util.Map;

import jfg.gui.GuiWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

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
