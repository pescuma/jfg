package jfg.gui.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class IndependentSWTCopyManager extends AbstractSWTCopyManager
{
	private Map<SWTAttribute, Runnable> attributeTimers = new HashMap<SWTAttribute, Runnable>();
	
	public IndependentSWTCopyManager(JfgFormComposite composite, JfgFormData data)
	{
		super(composite, data);
		
		composite.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event)
			{
				while (attributeTimers.size() > 0)
				{
					attributeTimers.values().iterator().next().run();
				}
			}
		});
	}
	
	public void guiChanged(final SWTAttribute attrib)
	{
		if (attributeTimers.get(attrib) != null)
			return;
		
		Runnable copyTimer = new Runnable() {
			public void run()
			{
				update(attrib);
			}
		};
		attributeTimers.put(attrib, copyTimer);
		
		getDisplay().timerExec(data.timeToUpdateModelWhenGuiChanges, copyTimer);
	}
	
	final void update(SWTAttribute attrib)
	{
		int x = 0;
		
		Runnable copyTimer = attributeTimers.get(attrib);
		if (copyTimer == null)
			return;
		getDisplay().timerExec(-1, copyTimer);
		attributeTimers.remove(attrib);
		
		attrib.copyToModel();
	}
}
