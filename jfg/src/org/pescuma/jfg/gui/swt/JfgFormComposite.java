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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiUpdateListener;
import org.pescuma.jfg.gui.GuiWidget;

public class JfgFormComposite extends Composite
{
	private final JfgFormData data;
	private final BaseWidgetList widgets = new BaseWidgetList();
	private final GuiCopyManager copyManager;
	private final BaseGuiListenerManager listenerManager = new BaseGuiListenerManager();
	private boolean initializing = true;
	private boolean layoutInitialized = false;
	
	public JfgFormComposite(Composite parent, int style, JfgFormData data)
	{
		super(parent, style);
		this.data = data;
		
		switch (data.modelUpdateStrategy)
		{
			case Never:
				copyManager = new DontUpdateSWTCopyManager(this, data);
				break;
			case UpdateOnGuiChange:
				copyManager = new FastSWTCopyManager(this, data);
				break;
			case BufferUpdatesForTimeout:
				if (data.modelUpdateTimeout <= 0)
					throw new IllegalArgumentException();
				copyManager = new IndependentFixedTimeSWTCopyManager(this, data);
				break;
			case UpdateAfterFieldStoppedChanging:
				if (data.modelUpdateTimeout <= 0)
					throw new IllegalArgumentException();
				copyManager = new IndependentSWTCopyManager(this, data);
				break;
			case UpdateAfterAllFieldsStoppedChanging:
				if (data.modelUpdateTimeout <= 0)
					throw new IllegalArgumentException();
				copyManager = new BatchSWTCopyManager(this, data);
				break;
			default:
				throw new IllegalArgumentException();
		}
		
		parent.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event)
			{
				if (initializing)
					finishInitialize();
			}
		});
	}
	
	/** Add all the attributes from the group, without adding the group itself */
	public void addContentsFrom(AttributeGroup group)
	{
		buildAttributes(group, 0);
	}
	
	public void add(Attribute attrib)
	{
		buildAttribute(attrib, 0);
	}
	
	public void add(AttributeGroup group)
	{
		buildGroup(group, 0);
	}
	
	public void addText(Attribute text)
	{
		addAttribute(new SWTTextBuilder(), text);
	}
	
	public void addNumer(Attribute number)
	{
		addAttribute(new SWTNumberBuilder(), number);
	}
	
	public void addReal(Attribute real)
	{
		addAttribute(new SWTRealBuilder(), real);
	}
	
	public void addCheckbox(Attribute bool)
	{
		addAttribute(new SWTCheckboxBuilder(), bool);
	}
	
	public void addCombo(Attribute enumer)
	{
		addAttribute(new SWTComboBuilder(), enumer);
	}
	
	public void addPassword(Attribute enumer)
	{
		addAttribute(new SWTPasswordBuilder(), enumer);
	}
	
	public void addScale(Attribute enumer)
	{
		addAttribute(new SWTScaleBuilder(), enumer);
	}
	
	public void addCustom(SWTWidgetBuilder builder, Attribute custom)
	{
		addAttribute(builder, custom);
	}
	
	private void buildAttributes(AttributeGroup group, int currentLevel)
	{
		for (Object attrib : group.getAttributes())
		{
			if (attrib instanceof AttributeGroup)
				buildGroup((AttributeGroup) attrib, currentLevel);
			else if (attrib instanceof Attribute)
				buildAttribute((Attribute) attrib, currentLevel);
		}
	}
	
	private void buildAttribute(Attribute attrib, int currentLevel)
	{
		SWTWidgetBuilder builder = getBuilderFor(attrib);
		if (builder != null)
		{
			addAttribute(builder, attrib);
		}
		else
		{
			// TODO Support groups when attribute is read/write
			//if (!attrib.canWrite())
			AttributeGroup group = attrib.asGroup();
			if (group != null)
				buildGroup(group, currentLevel + 1);
		}
	}
	
	private void addAttribute(SWTWidgetBuilder builder, Attribute attrib)
	{
		if (!builder.accept(attrib))
			throw new IllegalArgumentException("Wrong configuration");
		
		for (SWTAttributeFilter filter : data.attributeFilters)
			if (filter.hideAttribute(attrib))
				return;
		
		initLayout();
		
		SWTGuiWidget widget = builder.build(attrib, data);
		widget.init(data.layout, copyManager);
		widgets.add(attrib, widget);
	}
	
	private void initLayout()
	{
		if (!layoutInitialized)
		{
			data.layout.init(this, data);
			layoutInitialized = true;
		}
	}
	
	private void finishInitialize()
	{
		if (layoutInitialized)
			data.layout.finish();
		
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToGUI();
		
		for (AttributeWidgetPair aw : widgets)
			listenerManager.notifyChange(aw.attrib.getName(), aw.widget, widgets);
		
		initializing = false;
	}
	
	private SWTWidgetBuilder getBuilderFor(Attribute attrib)
	{
		return data.builders.get(getTypeOf(attrib));
	}
	
	private Object getTypeOf(Attribute attrib)
	{
		Object type = null;
		for (SWTBuilderTypeSelector selector : data.builderTypeSelectors)
		{
			type = selector.getTypeFor(attrib);
			if (type != null)
				return type;
		}
		return attrib.getType();
	}
	
	private void buildGroup(AttributeGroup group, int currentLevel)
	{
		if (currentLevel > data.maxGroupAttributeLevels)
			return;
		
		initLayout();
		
		data.layout.startGroup(group.getName());
		
		buildAttributes(group, currentLevel);
		
		data.layout.endGroup(group.getName());
	}
	
	public void copyToGUI()
	{
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToGUI();
	}
	
	public void copyToModel()
	{
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToModel();
	}
	
	void onGuiUpdated(GuiWidget widget)
	{
		if (initializing)
			return;
		
		listenerManager.notifyChange(widgets.getAttribute(widget).getName(), widget, widgets);
	}
	
	public void addGuiUpdateListener(GuiUpdateListener listener)
	{
		listenerManager.addListener(null, listener);
	}
	
	public void addGuiUpdateListener(Attribute attribute, GuiUpdateListener listener)
	{
		addGuiUpdateListener(attribute.getName(), listener);
	}
	
	public void addGuiUpdateListener(String attributeName, GuiUpdateListener listener)
	{
		listenerManager.addListener(attributeName, listener);
	}
	
	public void removeGuiUpdateListener(GuiUpdateListener listener)
	{
		listenerManager.removeListener(null, listener);
	}
	
	public void removeGuiUpdateListener(Attribute attribute, GuiUpdateListener listener)
	{
		removeGuiUpdateListener(attribute.getName(), listener);
	}
	
	public void removeGuiUpdateListener(String attributeName, GuiUpdateListener listener)
	{
		listenerManager.removeListener(attributeName, listener);
	}
	
}
