package jfg.gui.swt;

import jfg.Attribute;
import jfg.gui.GuiWidget;

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
	
	public GuiWidget build(Composite aParent, Attribute attrib, JfgFormData data)
	{
		return new AbstractSWTWidget(aParent, attrib, data) {
			
			private Button chk;
			private Color background;
			
			@Override
			protected void createWidget(Composite parent)
			{
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
			
			public Object getValue()
			{
				return chk.getSelection() ? Boolean.TRUE : Boolean.FALSE;
			}
			
			public void setValue(Object value)
			{
				chk.setSelection((Boolean) value);
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
			
			@Override
			public void setEnabled(boolean enabled)
			{
				super.setEnabled(enabled);
				
				chk.setEnabled(enabled);
			}
		};
	}
}
