package jfg.gui.swt;

import jfg.Attribute;
import jfg.AttributeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SWTTextBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == String.class;
	}
	
	public boolean wantNameLabel()
	{
		return true;
	}
	
	public SWTAttribute build(final Composite parent, final Attribute attrib, final JfgFormData data)
	{
		return new SWTAttribute() {
			
			private boolean ignoreToGUI;
			private boolean ignoreToAttribute;
			private AttributeListener attributeListener;
			private Text text;
			
			public void init()
			{
				text = data.componentFactory.createText(parent, attrib.canWrite() ? 0 : SWT.READ_ONLY);
				text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				addValidation(text, attrib.getType());
				
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
				
				if (attrib.canWrite())
				{
					text.addListener(SWT.Modify, new Listener() {
						public void handleEvent(Event event)
						{
							if (ignoreToAttribute)
								return;
							
							copyToAttribute();
						}
					});
				}
				
				text.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event)
					{
						if (attrib.canListen())
							attrib.removeListener(attributeListener);
					}
				});
				
				copyToGUI();
			}
			
			public void copyToAttribute()
			{
				if (!attrib.canWrite())
					return;
				
				ignoreToGUI = true;
				
				attrib.setValue(convertToObject(text.getText(), attrib.getType()));
				
				ignoreToGUI = false;
			}
			
			public void copyToGUI()
			{
				ignoreToAttribute = true;
				
				int caretPosition = text.getCaretPosition();
				text.setText(convertToString(text, attrib.getValue(), attrib.getType()));
				text.setSelection(caretPosition);
				
				ignoreToAttribute = false;
			}
		};
	}
	
	protected void addValidation(Text text, Object type)
	{
	}
	
	protected String convertToString(Text text, Object value, Object type)
	{
		if (value == null)
			return "";
		else
			return value.toString();
	}
	
	protected Object convertToObject(String value, Object type)
	{
		return value;
	}
	
}
