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
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import static org.pescuma.jfg.gui.swt.FileUtils.*;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;

public class SWTFileBuilder implements SWTWidgetBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == File.class || type == String.class || "file".equals(type) || "file_open".equals(type);
	}
	
	@Override
	public SWTGuiWidget build(Attribute attrib, JfgFormData data, InnerBuilder innerBuilder)
	{
		return new FileSWTWidget(attrib, data) {
			@Override
			protected String openDialog(Attribute attrib, Text text)
			{
				return SWTFileBuilder.this.openDialog(attrib, text);
			}
		};
	}
	
	protected String openDialog(Attribute attrib, Text text)
	{
		FileDialog dialog = new FileDialog(text.getShell(), SWT.OPEN);
		if (SystemUtils.isWindows())
		{
			dialog.setFilterNames(new String[] { "All Files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.*" });
		}
		else
		{
			dialog.setFilterNames(new String[] { "All Files (*)" });
			dialog.setFilterExtensions(new String[] { "*" });
		}
		
		String str = text.getText().trim();
		if (!str.isEmpty())
		{
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
		}
		
		return dialog.open();
	}
	
}
