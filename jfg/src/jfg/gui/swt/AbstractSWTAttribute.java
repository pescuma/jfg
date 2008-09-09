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
	private SWTCopyManager manager;
	
	public AbstractSWTAttribute(Composite parent, Attribute attrib, JfgFormData data)
	{
		this.attrib = attrib;
		this.data = data;
		this.parent = parent;
	}
	
	public void init(SWTCopyManager aManager)
	{
		manager = aManager;
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
					
					onModelChange();
				}
			};
			
			attrib.addListener(attributeListener);
		}
	}
	
	protected void onModelChange()
	{
		manager.modelChanged(AbstractSWTAttribute.this);
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
				
				onGuiChange();
			}
		};
	}
	
	protected void onGuiChange()
	{
		if (data.markFieldsWhithUncommitedChanges)
			markField();
		
		manager.guiChanged(AbstractSWTAttribute.this);
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
	
	public void copyToModel()
	{
		if (!attrib.canWrite())
			return;
		
		ignoreToGUI = true;
		
		guiToAttribute();
		
		if (data.markFieldsWhithUncommitedChanges)
			unmarkField();
		
		ignoreToGUI = false;
	}
	
	protected abstract void guiToAttribute();
	
	public void copyToGUI()
	{
		ignoreToAttribute = true;
		
		attibuteToGUI();
		
		if (data.markFieldsWhithUncommitedChanges)
			unmarkField();
		
		ignoreToAttribute = false;
	}
	
	protected abstract void attibuteToGUI();
	
	protected void markField()
	{
	}
	
	protected void unmarkField()
	{
	}
	
}
