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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.WidgetValidator;
import org.pescuma.jfg.gui.swt.JfgFormData.FieldConfig;

abstract class AbstractSWTWidget implements SWTGuiWidget
{
	protected final Attribute attrib;
	protected final JfgFormData data;
	
	protected boolean ignoreToGUI = false;
	protected boolean ignoreToAttribute = false;
	protected AttributeListener attributeListener;
	private GuiCopyManager manager;
	private final List<DisposeListener> disposeListeners = new LinkedList<DisposeListener>();
	protected WidgetValidator validator;
	
	public AbstractSWTWidget(Attribute attrib, JfgFormData data)
	{
		this.attrib = attrib;
		this.data = data;
	}
	
	protected boolean canCopyToAttribute()
	{
		return attrib.canWrite();
	}
	
	public void init(SWTLayoutBuilder layout, InnerBuilder innerBuilder, GuiCopyManager aManager)
	{
		manager = aManager;
		createWidgets(layout, innerBuilder);
	}
	
	protected abstract void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder);
	
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
		manager.guiUpdated(this);
		
		if (!canCopyToAttribute())
			return;
		
		if (ignoreToAttribute)
			return;
		
		if (data.markFieldsWhithUncommitedChanges)
			markFieldAsUncommited();
		
		manager.guiChanged(this);
	}
	
	protected Listener getDisposeListener()
	{
		return new Listener() {
			public void handleEvent(Event event)
			{
				if (attrib.canListen())
					attrib.removeListener(attributeListener);
				
				for (DisposeListener l : disposeListeners)
					l.widgetDisposed(new DisposeEvent(event));
			}
		};
	}
	
	public void copyToModel()
	{
		if (!canCopyToAttribute())
			return;
		
		ignoreToGUI = true;
		
		guiToAttribute();
		
		if (data.markFieldsWhithUncommitedChanges)
			unmarkFieldAsUncommited();
		
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
			unmarkFieldAsUncommited();
		
		ignoreToAttribute = false;
	}
	
	protected void attibuteToGUI()
	{
		setValue(attrib.getValue());
	}
	
	protected void markFieldAsUncommited()
	{
	}
	
	protected void unmarkFieldAsUncommited()
	{
	}
	
	public Attribute getAttribute()
	{
		return attrib;
	}
	
	public void setEnabled(boolean enabled)
	{
	}
	
	@Override
	public void addDisposeListener(DisposeListener listener)
	{
		disposeListeners.add(listener);
	}
	
	@Override
	public void setShadowText(String text)
	{
	}
	
	@Override
	public void setValidator(WidgetValidator validator)
	{
		this.validator = validator;
	}
	
	@Override
	public int getDefaultLayoutHint()
	{
		return 0;
	}
	
	protected int createLayoutHints(Attribute attrib)
	{
		int defaultLayoutHint = getDefaultLayoutHint();
		
		FieldConfig config = data.fieldsConfig.get(attrib.getName());
		if (config == null)
			return defaultLayoutHint;
		
		int ret = 0;
		
		if ((config.layoutHint & JfgFormData.HORIZONTAL_HINT_MASK) != 0)
			ret += config.layoutHint & JfgFormData.HORIZONTAL_HINT_MASK;
		else
			ret += defaultLayoutHint & JfgFormData.HORIZONTAL_HINT_MASK;
		
		if ((config.layoutHint & JfgFormData.VERTICAL_HINT_MASK) != 0)
			ret += config.layoutHint & JfgFormData.VERTICAL_HINT_MASK;
		else
			ret += defaultLayoutHint & JfgFormData.VERTICAL_HINT_MASK;
		
		return ret;
	}
}
