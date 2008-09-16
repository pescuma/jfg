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
		getDisplay().timerExec(data.guiUpdateTimeout, copyTimer);
	}
}
