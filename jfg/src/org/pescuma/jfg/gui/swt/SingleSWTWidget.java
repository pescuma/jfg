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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.WidgetValidator;
import org.pescuma.jfg.gui.swt.JfgFormData.FieldConfig;

public abstract class SingleSWTWidget implements SWTGuiWidget
{
	protected final Attribute attrib;
	protected final JfgFormData data;
	
	protected boolean ignoreToGUI = false;
	protected boolean ignoreToAttribute = false;
	protected AttributeListener attributeListener;
	private GuiCopyManager manager;
	private final List<DisposeListener> disposeListeners = new LinkedList<DisposeListener>();
	protected WidgetValidator[] validators;
	protected final WidgetMarks marks = new WidgetMarks();
	protected final Collection<ValidationError> validationErrors = new ArrayList<ValidationError>();
	
	protected class WidgetMarks
	{
		boolean uncommited = false;
		boolean invalid = false;
	}
	
	public SingleSWTWidget(Attribute attrib, JfgFormData data)
	{
		this.attrib = attrib;
		this.data = data;
	}
	
	protected boolean canCopyToAttribute()
	{
		return attrib.canWrite();
	}
	
	@Override
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
				@Override
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
			@Override
			public void handleEvent(Event event)
			{
				onWidgetModify();
			}
		};
	}
	
	protected void onWidgetModify()
	{
		validate();
		
		if (ignoreToAttribute)
			return;
		
		manager.guiUpdated(this);
		
		if (!canCopyToAttribute())
			return;
		
		if (data.markFieldsWhithUncommitedChanges)
			markFieldAsUncommited();
		
		manager.guiChanged(this);
	}
	
	protected void validate()
	{
		if (testValidation())
			markFieldAsValid();
		else
			markFieldAsInvalid();
	}
	
	protected boolean testValidation()
	{
		validationErrors.clear();
		
		if (validators == null || validators.length < 1)
			return true;
		
		Object value = getValue();
		for (WidgetValidator validator : validators)
		{
			if (!validator.isValid(attrib, value))
			{
				ValidationError ve = new ValidationError();
				ve.attribute = attrib;
				ve.validator = validator;
				validationErrors.add(ve);
			}
		}
		
		return validationErrors.size() < 1;
	}
	
	@Override
	public Collection<ValidationError> getValidationErrors()
	{
		return Collections.unmodifiableCollection(validationErrors);
	}
	
	protected Listener getDisposeListener()
	{
		return new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				if (attrib.canListen())
					attrib.removeListener(attributeListener);
				
				for (DisposeListener l : disposeListeners)
					l.widgetDisposed(new DisposeEvent(event));
			}
		};
	}
	
	@Override
	public void copyToModel()
	{
		if (!canCopyToAttribute())
			return;
		
		ignoreToGUI = true;
		
		guiToAttribute();
		
		if (data.markFieldsWhithUncommitedChanges)
			markFieldAsCommited();
		
		ignoreToGUI = false;
	}
	
	protected void guiToAttribute()
	{
		attrib.setValue(getValue());
	}
	
	@Override
	public void copyToGUI()
	{
		ignoreToAttribute = true;
		
		attibuteToGUI();
		
		if (data.markFieldsWhithUncommitedChanges)
			markFieldAsCommited();
		
		ignoreToAttribute = false;
	}
	
	protected void attibuteToGUI()
	{
		setValue(attrib.getValue());
	}
	
	protected void markFieldAsInvalid()
	{
		marks.invalid = true;
		
		updateColor();
	}
	
	protected void markFieldAsValid()
	{
		marks.invalid = false;
		updateColor();
	}
	
	protected void markFieldAsUncommited()
	{
		marks.uncommited = true;
		updateColor();
	}
	
	protected void markFieldAsCommited()
	{
		marks.uncommited = false;
		updateColor();
	}
	
	protected void updateColor()
	{
	}
	
	protected Color createColor(Control widget, Color background)
	{
		if (marks.invalid && marks.uncommited)
			return data.createUncommitedInvalidBackgroundColor(widget, background);
		else if (marks.uncommited)
			return data.createUncommitedBackgroundColor(widget, background);
		else if (marks.invalid)
			return data.createInvalidBackgroundColor(widget, background);
		else
			return background;
	}
	
	@Override
	public Attribute getAttribute()
	{
		return attrib;
	}
	
	@Override
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
	public void setValidators(WidgetValidator... validators)
	{
		this.validators = validators;
		validate();
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
	
	protected int createHeightHint(Attribute attrib)
	{
		FieldConfig config = data.fieldsConfig.get(attrib.getName());
		if (config == null || config.heightHint < 1)
			return getDefaultHeightHint();
		
		return config.heightHint;
	}
	
	protected int getDefaultHeightHint()
	{
		return SWT.DEFAULT;
	}
	
	protected boolean canBeNull()
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return true;
		
		return range.canBeNull();
	}
	
	@Override
	public Collection<GuiWidget> getChildren()
	{
		return Collections.emptyList();
	}
	
	@Override
	public GuiWidget getChild(String attributeName)
	{
		return null;
	}
	
	@Override
	public Collection<GuiWidget> getChildren(String attributeName)
	{
		return null;
	}
	
	@Override
	public GuiWidget findChild(String attributeName)
	{
		return null;
	}
	
	@Override
	public Collection<GuiWidget> findChildren(String attributeName)
	{
		return null;
	}
}
