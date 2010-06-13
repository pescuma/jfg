/*
 * Copyright 2010 Ricardo Pescuma Domenecci
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

public class SWTGroupBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		if (attrib.canWrite())
			return false;
		
		return attrib.asGroup() != null;
	}
	
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
//		if (attrib.canWrite())
//			System.err.println("[JFG] Creating GUI for read/write object. "
//					+ "I'll only change the object in place and will not check for changes in it!");
		
		return new AbstractSWTWidget(attrib, data) {
			private Group frame;
			
			@Override
			protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
			{
				if (!innerBuilder.canBuildInnerAttribute())
					return;
				
				AttributeGroup group = attrib.asGroup();
				
				frame = layout.startGroup(group.getName());
				
				for (Attribute ga : group.getAttributes())
					innerBuilder.buildInnerAttribute(layout, ga);
				
				layout.endGroup(group.getName());
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
					throw new NotImplementedException();
			}
			
			@Override
			public void setEnabled(boolean enabled)
			{
				frame.setEnabled(enabled);
			}
		};
	}
}
