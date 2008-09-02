package jfg.gui.swt;

import jfg.Attribute;
import jfg.AttributeValueRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
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
		return new AbstractSWTAttribute(parent, attrib, data) {
			
			private Text text;
			
			public void init()
			{
				text = data.componentFactory.createText(parent, (attrib.canWrite() ? 0 : SWT.READ_ONLY) | getAdditionalTextStyle());
				text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				text.addListener(SWT.Modify, getModifyListener());
				text.addListener(SWT.Dispose, getDisposeListener());
				addValidation(text, attrib.getType());
				setTextLimit();
				
				addAttributeListener();
				
				copyToGUI();
			}
			
			private void setTextLimit()
			{
				AttributeValueRange range = attrib.getValueRange();
				if (range == null)
					return;
				
				Object max = range.getMax();
				if (max != null && (max instanceof Number))
					text.setTextLimit(((Number) max).intValue());
			}
			
			@Override
			protected void guiToAttribute()
			{
				attrib.setValue(convertToObject(text.getText(), attrib.getType(), canBeNull()));
			}
			
			private boolean canBeNull()
			{
				AttributeValueRange range = attrib.getValueRange();
				if (range == null)
					return true;
				
				return range.canBeNull();
			}
			
			@Override
			protected void attibuteToGUI()
			{
				int caretPosition = text.getCaretPosition();
				text.setText(convertToString(text, attrib.getValue(), attrib.getType()));
				text.setSelection(caretPosition);
			}
		};
	}
	
	protected int getAdditionalTextStyle()
	{
		return 0;
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
	
	protected Object convertToObject(String value, Object type, boolean canBeNull)
	{
		if (value == null && !canBeNull)
			return "";
		return value;
	}
	
}
