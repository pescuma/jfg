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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class FrameSWTWidget extends AbstractSWTWidget
{
	private Group frame;
	private boolean empty;
	
	FrameSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
	{
		empty = !innerBuilder.canBuildInnerAttribute();
		if (empty)
			return;
		
		AttributeGroup group = attrib.asGroup();
		
		frame = layout.addGroup(group.getName(), createLayoutHints(attrib), createHeightHint(attrib));
		
		SWTLayoutBuilder innerLayout = data.createLayoutFor(group.getName(), frame, layout.getLayoutListener());
		
		for (Attribute ga : group.getAttributes())
			innerBuilder.buildInnerAttribute(innerLayout, ga);
	}
	
	@Override
	public Object getValue()
	{
		return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		if (empty)
			return;
		
		if (value != attrib.getValue())
			throw new NotImplementedException();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		if (empty)
			return;
		
		frame.setEnabled(enabled);
	}
}