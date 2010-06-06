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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.gui.GuiCopyManager;

abstract class AbstractSWTWidget implements SWTGuiWidget
{
	protected final Attribute attrib;
	protected final JfgFormData data;
	
	protected boolean ignoreToGUI;
	protected boolean ignoreToAttribute;
	protected AttributeListener attributeListener;
	private GuiCopyManager manager;
	
	public AbstractSWTWidget(Attribute attrib, JfgFormData data)
	{
		this.attrib = attrib;
		this.data = data;
	}
	
	public void init(SWTLayoutBuilder layout, GuiCopyManager aManager)
	{
		manager = aManager;
		createWidgets(layout);
	}
	
	protected abstract void createWidgets(SWTLayoutBuilder layout);
	
	protected void addAttributeListener()
	{
		if (attrib.canListen())
		{
			attributeListener = new AttributeListener() {
				public void onChange()
				{
					if (ignoreToGUI)
						return;
					
					onModelChange();
				}
			};
			
			attrib.addListener(attributeListener);
		}
	}
	
	protected void onModelChange()
	{
		manager.modelChanged(this);
	}
	
	protected Listener getModifyListener()
	{
		return new Listener() {
			public void handleEvent(Event event)
			{
				onWidgetModify();
			}
		};
	}
	
	protected void onWidgetModify()
	{
		onGuiUpdated();
		
		if (!attrib.canWrite())
			return;
		
		if (ignoreToAttribute)
			return;
		
		onGuiChange();
	}
	
	protected void onGuiUpdated()
	{
		manager.guiUpdated(this);
	}
	
	protected void onGuiChange()
	{
		if (data.markFieldsWhithUncommitedChanges)
			markField();
		
		manager.guiChanged(this);
	}
	
	protected Listener getDisposeListener()
	{
		return new Listener() {
			public void handleEvent(Event event)
			{
				if (attrib.canListen())
					attrib.removeListener(attributeListener);
			}
		};
	}
	
	public void copyToModel()
	{
		if (!attrib.canWrite())
			return;
		
		ignoreToGUI = true;
		
		guiToAttribute();
		
		if (data.markFieldsWhithUncommitedChanges)
			unmarkField();
		
		ignoreToGUI = false;
	}
	
	protected void guiToAttribute()
	{
		attrib.setValue(getValue());
	}
	
	public void copyToGUI()
	{
		ignoreToAttribute = true;
		
		attibuteToGUI();
		
		if (data.markFieldsWhithUncommitedChanges)
			unmarkField();
		
		ignoreToAttribute = false;
	}
	
	protected void attibuteToGUI()
	{
		setValue(attrib.getValue());
	}
	
	protected void markField()
	{
	}
	
	protected void unmarkField()
	{
	}
	
	public Attribute getAttribute()
	{
		return attrib;
	}
	
	public void setEnabled(boolean enabled)
	{
	}
	
	public void setVisible(boolean visible)
	{
	}
}
