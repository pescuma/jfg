package jfg.gui.swt;

import jfg.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
		return type == Boolean.class || type == boolean.class || "checkbox".equals(type);
	}
	
	public boolean wantNameLabel()
	{
		return false;
	}
	
	public SWTAttribute build(final Composite parent, final Attribute attrib, final JfgFormData data)
	{
		return new AbstractSWTAttribute(parent, attrib, data) {
			
			private Button chk;
			private Color background;
			
			@Override
			public void init(SWTCopyManager aManager)
			{
				super.init(aManager);
				
				chk = data.componentFactory.createCheckbox(parent, SWT.NONE);
				chk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				chk.setText(data.textTranslator.fieldName(attrib.getName()));
				chk.addListener(SWT.Selection, getModifyListener());
				chk.addListener(SWT.Dispose, getDisposeListener());
				
				// SWT does not support a read-only checkbox
				if (!attrib.canWrite())
				{
					chk.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event)
						{
							copyToGUI();
						}
					});
				}
				
				addAttributeListener();
				
				background = chk.getBackground();
			}
			
			@Override
			protected void guiToAttribute()
			{
				attrib.setValue(chk.getSelection() ? Boolean.TRUE : Boolean.FALSE);
			}
			
			@Override
			protected void attibuteToGUI()
			{
				chk.setSelection((Boolean) attrib.getValue());
			}
			
			@Override
			protected void markField()
			{
				chk.setBackground(data.createBackgroundColor(chk, background));
			}
			
			@Override
			protected void unmarkField()
			{
				chk.setBackground(background);
			}
			
		};
	}
	
}
