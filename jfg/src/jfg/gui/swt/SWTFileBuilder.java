/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

package jfg.gui.swt;

import static jfg.gui.swt.SWTHelper.*;

import java.io.File;
import java.io.IOException;

import jfg.Attribute;
import jfg.AttributeValueRange;
import jfg.gui.GuiWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SWTFileBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == File.class || type == String.class || "file".equals(type) || "file_open".equals(type);
	}
	
	public GuiWidget build(Composite aParent, Attribute attrib, JfgFormData data)
	{
		return new AbstractLabeledSWTWidget(aParent, attrib, data) {
			
			private Text text;
			private Color background;
			private Button select;
			
			@Override
			protected void createWidget(Composite parent)
			{
				if (attrib.canWrite())
				{
					Composite composite = data.componentFactory.createComposite(parent, SWT.NONE);
					composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					composite.setLayout(createBorderlessGridLayout(2, false));
					
					text = data.componentFactory.createText(composite, SWT.NONE);
					
					select = data.componentFactory.createButton(composite, SWT.PUSH | SWT.FLAT);
					select.setText(data.textTranslator.translate("..."));
					select.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event)
						{
							String sel = openDialog(attrib, text);
							if (sel != null)
								text.setText(sel);
						}
					});
				}
				else
				{
					text = data.componentFactory.createText(parent, SWT.READ_ONLY);
				}
				
				text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				text.addListener(SWT.Modify, getModifyListener());
				text.addListener(SWT.Dispose, getDisposeListener());
				setTextLimit(attrib, text);
				
				addAttributeListener();
				
				background = text.getBackground();
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
			
			private boolean canBeNull()
			{
				AttributeValueRange range = attrib.getValueRange();
				if (range == null)
					return true;
				
				return range.canBeNull();
			}
			
			public void setValue(Object value)
			{
				int caretPosition = text.getCaretPosition();
				text.setText(convertToString(value));
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
			
			@Override
			public void setEnabled(boolean enabled)
			{
				super.setEnabled(enabled);
				
				text.setEnabled(enabled);
				
				if (select != null)
					select.setEnabled(enabled);
			}
			
		};
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
	
	protected String getFullPath(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException e)
		{
			return file.getAbsolutePath();
		}
	}
	
	protected String openDialog(Attribute attrib, Text text)
	{
		FileDialog dialog = new FileDialog(text.getShell(), SWT.OPEN);
		dialog.setFilterNames(new String[] { "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.*" });
		
		String str = text.getText().trim();
		if (str.isEmpty())
			str = ".";
		File file = new File(str);
		
		if (file.isDirectory())
		{
			dialog.setFilterPath(getFullPath(file));
		}
		else if (file.getParent() != null)
		{
			dialog.setFilterPath(file.getParent());
			dialog.setFileName(file.getName());
		}
		else
		{
			dialog.setFileName(getFullPath(file));
		}
		
		return dialog.open();
	}
	
}
