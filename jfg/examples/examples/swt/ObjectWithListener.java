package examples.swt;

import java.util.ArrayList;
import java.util.List;

class ObjectWithListener
{
	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
	
	protected void notifyListeners()
	{
		for (ChangeListener l : listeners)
			l.onChange();
	}
	
	public boolean addListener(ChangeListener e)
	{
		return listeners.add(e);
	}
	
	public boolean removeListener(ChangeListener o)
	{
		return listeners.remove(o);
	}
}
