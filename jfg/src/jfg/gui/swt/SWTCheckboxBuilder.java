package jfg.gui.swt;

import jfg.Attribute;
import jfg.AttributeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SWTCheckboxBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == Boolean.class || type == boolean.class;
	}
	
	public boolean wantNameLabel()
	{
		return false;
	}
	
	public SWTAttribute build(final Composite parent, final Attribute attrib, final JfgFormData data)
	{
		return new SWTAttribute() {
			
			private boolean ignoreToGUI;
			private boolean ignoreToAttribute;
			private AttributeListener attributeListener;
			private Button chk;
			
			public void init()
			{
				chk = data.componentFactory.createCheckbox(parent, attrib.canWrite() ? 0 : SWT.READ_ONLY);
				chk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				chk.setText(data.textTranslator.fieldName(attrib.getName()));
				
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
					chk.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event)
						{
							if (ignoreToAttribute)
								return;
							
							copyToAttribute();
						}
					});
				}
				
				chk.addListener(SWT.Dispose, new Listener() {
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
				
				attrib.setValue(chk.getSelection() ? Boolean.TRUE : Boolean.FALSE);
				
				ignoreToGUI = false;
			}
			
			public void copyToGUI()
			{
				ignoreToAttribute = true;
				
				chk.setSelection((Boolean) attrib.getValue());
				
				ignoreToAttribute = false;
			}
		};
	}
	
}
