package org.pescuma.jfg.gui.swt;

import static org.pescuma.jfg.gui.swt.FileUtils.*;
import static org.pescuma.jfg.gui.swt.SWTUtils.*;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;

abstract class FileSWTWidget extends AbstractLabelControlSWTWidget
{
	private Text text;
	private Color background;
	private Button select;
	
	FileSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected Control createWidget(Composite parent)
	{
		Control ret;
		
		if (attrib.canWrite())
		{
			Composite composite = data.componentFactory.createComposite(parent, SWT.NONE);
			composite.setLayout(createBorderlessGridLayout(2, false));
			
			text = data.componentFactory.createText(composite, SWT.NONE);
			
			select = data.componentFactory.createButton(composite, SWT.PUSH | SWT.FLAT);
			select.setText(data.textTranslator.translate("FileWidget:Select"));
			select.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event)
				{
					String sel = openDialog(attrib, text);
					if (sel != null)
						text.setText(sel);
				}
			});
			
			ret = composite;
		}
		else
		{
			text = data.componentFactory.createText(parent, SWT.READ_ONLY);
			
			ret = text;
		}
		
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addListener(SWT.Modify, getModifyListener());
		text.addListener(SWT.Dispose, getDisposeListener());
		setTextLimit(attrib, text);
		
		background = text.getBackground();
		
		return ret;
	}
	
	private void setTextLimit(Attribute attrib, Text text)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return;
		
		Object max = range.getMax();
		if (max != null && (max instanceof Number))
			text.setTextLimit(((Number) max).intValue());
	}
	
	@Override
	public Object getValue()
	{
		String str = text.getText().trim();
		if (str.isEmpty() && canBeNull())
			return null;
		
		if (str.isEmpty())
			str = ".";
		File file = new File(str);
		
		if (attrib.getType() == String.class)
			return getFullPath(file);
		else
			return file;
	}
	
	@Override
	public void setValue(Object value)
	{
		int caretPosition = text.getCaretPosition();
		text.setText(convertToString(value));
		text.setSelection(caretPosition);
	}
	
	@Override
	protected void updateColor()
	{
		text.setBackground(createColor(text, background));
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		text.setEnabled(enabled);
		
		if (select != null)
			select.setEnabled(enabled);
	}
	
	protected String convertToString(Object value)
	{
		if (value == null)
		{
			return "";
		}
		else if (value instanceof File)
		{
			return getFullPath((File) value);
		}
		else
		{
			return value.toString();
		}
	}
	
	protected abstract String openDialog(Attribute attrib, Text text);
}