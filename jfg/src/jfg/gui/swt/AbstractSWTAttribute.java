package jfg.gui.swt;

import jfg.Attribute;
import jfg.AttributeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

abstract class AbstractSWTAttribute implements SWTAttribute
{
	protected final Composite parent;
	protected final Attribute attrib;
	protected final JfgFormData data;
	
	protected boolean ignoreToGUI;
	protected boolean ignoreToAttribute;
	protected AttributeListener attributeListener;
	
	public AbstractSWTAttribute(Composite parent, Attribute attrib, JfgFormData data)
	{
		this.attrib = attrib;
		this.data = data;
		this.parent = parent;
	}
	
	protected void addAttributeListener()
	{
		if (attrib.canListen())
		{
			attributeListener = new AttributeListener() {
				public void onChange()
				{
					if (ignoreToGUI)
						return;
					
					copyToGUI();
				}
			};
			
			attrib.addListener(attributeListener);
		}
	}
	
	protected Listener getModifyListener()
	{
		return new Listener() {
			public void handleEvent(Event event)
			{
				if (!attrib.canWrite())
					return;
				
				if (ignoreToAttribute)
					return;
				
				copyToAttribute();
			}
		};
	}
	
	protected Listener getDisposeListener()
	{
		return new Listener() {
			public void handleEvent(Event event)
			{
				if (attrib.canListen())
					attrib.removeListener(attributeListener);
			}
		};
	}
	
	public void copyToAttribute()
	{
		if (!attrib.canWrite())
			return;
		
		ignoreToGUI = true;
		
		guiToAttribute();
		
		ignoreToGUI = false;
	}
	
	protected abstract void guiToAttribute();
	
	public void copyToGUI()
	{
		ignoreToAttribute = true;
		
		attibuteToGUI();
		
		ignoreToAttribute = false;
	}
	
	protected abstract void attibuteToGUI();
}
