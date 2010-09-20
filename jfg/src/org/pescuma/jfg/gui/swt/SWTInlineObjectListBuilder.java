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

import java.util.List;

import org.pescuma.jfg.Attribute;

public class SWTInlineObjectListBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		if (attrib.canWrite())
			return false;
		
		Object type = attrib.getType();
		return type == List.class || "inline_obj_list".equals(type);
	}
	
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
//		if (attrib.canWrite())
//			System.out.println("[JFG] Creating GUI for read/write object. "
//					+ "I'll only change the object in place and will not check for changes in it!");
		
		return new InlineObjectListSWTWidget(attrib, data);
	}
}
