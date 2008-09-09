package jfg.gui.swt;

import jfg.Attribute;
import jfg.AttributeValueRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SWTTextBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == String.class || "text".equals(type);
	}
	
	public boolean wantNameLabel()
	{
		return true;
	}
	
	public SWTAttribute build(final Composite parent, final Attribute attrib, final JfgFormData data)
	{
		return new AbstractSWTAttribute(parent, attrib, data) {
			
			private Text text;
			private Color background;
			
			@Override
			public void init(SWTCopyManager aManager)
			{
				super.init(aManager);
				
				text = data.componentFactory.createText(parent, (attrib.canWrite() ? SWT.NONE : SWT.READ_ONLY) | getAdditionalTextStyle());
				text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				text.addListener(SWT.Modify, getModifyListener());
				text.addListener(SWT.Dispose, getDisposeListener());
				addValidation(text, getType(attrib.getType()));
				setTextLimit();
				
				addAttributeListener();
				
				background = text.getBackground();
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
				attrib.setValue(convertToObject(text.getText(), getType(attrib.getType()), canBeNull()));
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
				text.setText(convertToString(text, attrib.getValue(), getType(attrib.getType())));
				text.setSelection(caretPosition);
			}
			
			@Override
			protected void markField()
			{
				text.setBackground(data.createBackgroundColor(text, background));
			}
			
			@Override
			protected void unmarkField()
			{
				text.setBackground(background);
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
	
	protected Object getType(Object type)
	{
		if ("text".equals(type))
			return String.class;
		return type;
	}
	
}
