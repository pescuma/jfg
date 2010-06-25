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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SWTObjectListBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		if (attrib.canWrite())
			return false;
		
		Object type = attrib.getType();
		return type == List.class || "list_objects".equals(type);
	}
	
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
//		if (attrib.canWrite())
//			System.out.println("[JFG] Creating GUI for read/write object. "
//					+ "I'll only change the object in place and will not check for changes in it!");
		
		return new AbstractSWTWidget(attrib, data) {
			
			class Item
			{
				SWTLayoutBuilder.ListBuilder.ListItem listItem;
				Attribute attrib;
			}
			
			private SWTLayoutBuilder.ListBuilder listLayout;
			private InnerBuilder innerBuilder;
			private AttributeList list;
			private final List<Item> items = new ArrayList<Item>();
			private Composite frame;
			private Color background;
			
			@Override
			protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
			{
				if (!innerBuilder.canBuildInnerAttribute())
					return;
				
				this.innerBuilder = innerBuilder;
				list = attrib.asList();
				
				listLayout = layout.addList(list.getName());
				frame = listLayout.getContents();
				background = frame.getBackground();
				
				Control addMore = null;
				if (list.canWrite())
				{
					Listener listener = new Listener() {
						@Override
						public void handleEvent(Event event)
						{
							buildAttributeInsideList(list.createNewElement());
							onWidgetModify();
						}
					};
					addMore = data.componentFactory.createFlatButton(listLayout.getParentForAddMore(),
							data.textTranslator.translate(list.getName() + ":Add"), "icons/add.png", listener);
					listLayout.addAddMore(addMore);
				}
			}
			
			private void buildAttributeInsideList(final Attribute itemAttribute)
			{
				final Item item = new Item();
				
				Composite composite = listLayout.startListItem(list.getName());
				
				AttributeGroup group = itemAttribute.asGroup();
				if (group != null)
				{
					SWTLayoutBuilder layout = data.createLayoutFor(group.getName(), composite, listLayout.getLayoutListener());
					
					for (Attribute ga : group.getAttributes())
						innerBuilder.buildInnerAttribute(layout, ga);
				}
				else
				{
					SWTLayoutBuilder layout = data.createLayoutFor(null, composite, listLayout.getLayoutListener());
					
					innerBuilder.buildInnerAttribute(layout, itemAttribute);
				}
				
				Control remove = null;
				if (list.canWrite())
				{
					Listener listener = new Listener() {
						@Override
						public void handleEvent(Event event)
						{
							listLayout.removeListItem(item.listItem);
							items.remove(item);
							onWidgetModify();
						}
					};
					
					remove = data.componentFactory.createFlatButton(listLayout.getParentForRemove(),
							data.textTranslator.translate(list.getName() + ":Remove"), "icons/delete.png", listener);
				}
				
				item.listItem = listLayout.endListItem(list.getName(), remove);
				item.attrib = itemAttribute;
				items.add(item);
			}
			
			@Override
			protected boolean canCopyToAttribute()
			{
				return list.canWrite();
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
			protected void attibuteToGUI()
			{
				for (int i = 0; i < items.size(); i++)
				{
					Item item = items.get(i);
					if (list.indexOf(item.attrib) < 0)
					{
						listLayout.removeListItem(item.listItem);
						items.remove(i);
						--i;
					}
				}
				
				for (int i = 0; i < list.size(); i++)
				{
					Attribute a = list.get(i);
					int index = indexOf(a);
					if (index < 0)
					{
						buildAttributeInsideList(a);
						index = items.size() - 1;
					}
					if (index != i)
					{
						Item item = items.get(index);
						
						listLayout.moveAfter(i > 0 ? items.get(i - 1).listItem : null, item.listItem);
						
						items.remove(index);
						items.add(i, item);
					}
				}
			}
			
			@Override
			protected void guiToAttribute()
			{
				for (int i = 0; i < list.size(); i++)
				{
					if (indexOf(list.get(i)) < 0)
					{
						list.remove(i);
						--i;
					}
				}
				
				for (int i = 0; i < items.size(); i++)
				{
					Item item = items.get(i);
					int index = list.indexOf(item.attrib);
					if (index < 0)
					{
						list.add(i, item.attrib);
					}
					else if (index != i)
					{
						list.remove(index);
						list.add(i, item.attrib);
					}
				}
			}
			
			private int indexOf(Attribute a)
			{
				for (int i = 0; i < items.size(); i++)
				{
					Item item = items.get(i);
					if (item.attrib == a)
						return i;
				}
				return -1;
			}
			
			@Override
			public void setEnabled(boolean enabled)
			{
				frame.setEnabled(enabled);
			}
			
			@Override
			protected void markField()
			{
				super.markField();
				
				frame.setBackground(data.createBackgroundColor(frame, background));
			}
			
			@Override
			protected void unmarkField()
			{
				super.unmarkField();
				
				frame.setBackground(background);
			}
		};
	}
}
