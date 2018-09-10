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

import org.eclipse.swt.widgets.Group;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;

class FrameSWTWidget extends AbstractSWTWidget
{
	private Group frame;
	
	FrameSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
	{
		AttributeGroup group = attrib.asGroup();
		
		frame = layout.addGroup(group.getName(), createLayoutHints(attrib), createHeightHint(attrib));
		
		SWTLayoutBuilder innerLayout = data.createLayoutFor(group.getName(), frame);
		
		innerBuilder.startBuilding();
		for (Attribute ga : group.getAttributes())
			addWidget(innerBuilder.buildInnerAttribute(innerLayout, ga));
		innerBuilder.finishBuilding();
	}
	
	@Override
	public Object getValue()
	{
		return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		if (value != attrib.getValue())
			throw new UnsupportedOperationException();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		frame.setEnabled(enabled);
		
		super.setEnabled(enabled);
	}
	
}
