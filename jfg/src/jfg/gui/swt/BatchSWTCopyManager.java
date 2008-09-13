package jfg.gui.swt;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class BatchSWTCopyManager extends AbstractSWTCopyManager
{
	private final Set<SWTAttribute> changedAttributes = new HashSet<SWTAttribute>();
	private final Runnable copyTimer = new Runnable() {
		public void run()
		{
			getDisplay().timerExec(-1, copyTimer);
			for (SWTAttribute attrib : changedAttributes)
				attrib.copyToModel();
			changedAttributes.clear();
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
	
	public void guiChanged(SWTAttribute attrib)
	{
		getDisplay().timerExec(-1, copyTimer);
		changedAttributes.add(attrib);
		getDisplay().timerExec(data.timeToUpdateModelWhenGuiChanges, copyTimer);
	}
}
