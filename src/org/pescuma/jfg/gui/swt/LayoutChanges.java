package org.pescuma.jfg.gui.swt;

class LayoutChanges
{
	private final JfgFormComposite parent;
	private final Runnable notify;
	
	private int freezes = 0;
	private boolean changedCalled = false;
	
	public LayoutChanges(JfgFormComposite parent, Runnable notify)
	{
		this.parent = parent;
		this.notify = notify;
	}
	
	void freezeScreenAndExecute(Runnable r)
	{
		parent.setRedraw(false);
		freezes++;
		try
		{
			r.run();
		}
		finally
		{
			freezes--;
			parent.setRedraw(true);
		}
		
		if (freezes == 0 && changedCalled)
		{
			changedCalled = false;
			notify.run();
		}
	}
	
	void changed()
	{
		if (freezes > 0)
			changedCalled = true;
		else
			notify.run();
	}
}
