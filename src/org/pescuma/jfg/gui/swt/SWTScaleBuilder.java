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

import static org.pescuma.jfg.gui.swt.TypeUtils.*;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeValueRange;

public class SWTScaleBuilder implements SWTWidgetBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null || range.getMin() == null || range.getMax() == null)
			return false;
		
		Object type = attrib.getType();
		return typeIsNumber(type) || typeIsReal(type) || "scale".equals(type);
	}
	
	@Override
	public SWTGuiWidget build(Attribute attrib, JfgFormData data, InnerBuilder innerBuilder)
	{
		return new ScaleSWTWidget(attrib, data);
	}
	
}
