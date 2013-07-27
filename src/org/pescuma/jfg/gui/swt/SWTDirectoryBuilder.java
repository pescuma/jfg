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

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.Attribute;

public class SWTDirectoryBuilder extends SWTFileBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == File.class || type == String.class || "directory".equals(type);
	}
	
	@Override
	protected String openDialog(Attribute attrib, Text text)
	{
		DirectoryDialog dialog = new DirectoryDialog(text.getShell());
		String str = text.getText().trim();
		if (!str.isEmpty())
			dialog.setFilterPath(getFullPath(new File(str)));
		return dialog.open();
	}
	
}
