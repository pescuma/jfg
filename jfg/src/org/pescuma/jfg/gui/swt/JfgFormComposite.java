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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiUpdateListener;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.TextBasedGuiWidget;
import org.pescuma.jfg.gui.WidgetValidator;
import org.pescuma.jfg.gui.swt.JfgFormData.FieldConfig;

public class JfgFormComposite extends Composite
{
	private final JfgFormData data;
	private final RootSWTWidget widgets;
	private final GuiCopyManager copyManager;
	private final BaseGuiListenerManager listenerManager = new BaseGuiListenerManager();
	private int initializing = 0;
	private SWTLayoutBuilder layout;
	private boolean layoutInitialized = false;
	private boolean postponeFinishInitialize = false;
	
	public JfgFormComposite(Composite parent, int style, JfgFormData data)
	{
		super(parent, style);
		this.data = data;
		widgets = new RootSWTWidget(data);
		
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
		
		final Composite root = getRoot();
		if (!root.isVisible())
		{
			postponeFinishInitialize = true;
			
			root.addListener(SWT.Show, new Listener() {
				@Override
				public void handleEvent(Event event)
				{
					postponeFinishInitialize = false;
					finishInitialize();
					
					root.removeListener(SWT.Show, this);
				}
			});
		}
		
		for (Entry<String, FieldConfig> entry : data.fieldsConfig.entrySet())
		{
			String attributeName = entry.getKey();
			FieldConfig config = entry.getValue();
			
			for (GuiUpdateListener listener : config.guiUpdateListeners)
				addGuiUpdateListener(attributeName, listener);
		}
	}
	
	public GuiWidget getWidgets()
	{
		return widgets;
	}
	
	private Composite getRoot()
	{
		Composite root = getParent();
		while (root.getParent() != null)
			root = root.getParent();
		return root;
	}
	
	private SWTLayoutBuilder initLayout()
	{
		if (!layoutInitialized)
		{
			layout = data.createLayoutFor(null, this, new Runnable() {
				@Override
				public void run()
				{
					if (postponeFinishInitialize)
						return;
					
					layout();
					
					LayoutEvent event = new LayoutEvent(JfgFormComposite.this);
					event.widget = JfgFormComposite.this;
					event.display = event.widget.getDisplay();
					event.time = 0; // TODO
					
					for (LayoutListener l : layoutListeners)
						l.layoutChanged(event);
				}
			});
			layoutInitialized = true;
		}
		return layout;
	}
	
	private void startInitialize()
	{
		initializing++;
	}
	
	private void finishInitialize()
	{
		finishInitialize(null);
	}
	
	private void finishInitialize(Set<GuiWidget> filter)
	{
		initializing--;
		
		if (postponeFinishInitialize)
			return;
		
		copyToGUI(filter);
		notifyAllListeners(filter);
	}
	
	private void notifyAllListeners(Set<GuiWidget> filter)
	{
		for (GuiWidget widget : widgets.findAllWidgets())
		{
			if (filter != null && filter.contains(widget))
				continue;
			
			if (widget.getAttribute() == null)
				continue;
			
			listenerManager.notifyChange(widget.getAttribute().getName(), widget);
		}
	}
	
	/** Add all the attributes from the group, without adding the group itself */
	public void addContentsFrom(AttributeGroup group)
	{
		startInitialize();
		addAttributes(initLayout(), group, 0);
		finishInitialize();
	}
	
	public void add(Attribute attrib)
	{
		addRootAttribute(null, attrib);
	}
	
	public void addText(Attribute text)
	{
		addRootAttribute(new SWTTextBuilder(), text);
	}
	
	public void addNumer(Attribute number)
	{
		addRootAttribute(new SWTNumberBuilder(), number);
	}
	
	public void addReal(Attribute real)
	{
		addRootAttribute(new SWTRealBuilder(), real);
	}
	
	public void addCheckbox(Attribute bool)
	{
		addRootAttribute(new SWTCheckboxBuilder(), bool);
	}
	
	public void addCombo(Attribute enumer)
	{
		addRootAttribute(new SWTComboBuilder(), enumer);
	}
	
	public void addPassword(Attribute attrib)
	{
		addRootAttribute(new SWTPasswordBuilder(), attrib);
	}
	
	public void addScale(Attribute attrib)
	{
		addRootAttribute(new SWTScaleBuilder(), attrib);
	}
	
	public void addCustom(SWTWidgetBuilder builder, Attribute custom)
	{
		addRootAttribute(builder, custom);
	}
	
	private void addRootAttribute(SWTWidgetBuilder builder, Attribute attrib)
	{
		startInitialize();
		widgets.addWidget(addAttribute(initLayout(), builder, attrib, 0));
		finishInitialize();
	}
	
	private SWTGuiWidget addAttribute(SWTLayoutBuilder layout, SWTWidgetBuilder builder, Attribute attrib,
			final int currentLevel)
	{
		if (currentLevel > data.maxAttributeSubLevels)
			return null;
		
		if (builder == null)
			builder = data.getBuilderFor(attrib);
		if (builder == null)
			return null;
		
		if (!builder.accept(attrib))
			throw new IllegalArgumentException("Wrong configuration");
		
		if (data.hideAttribute(attrib))
			return null;
		
		InnerBuilder innerBuilder = createInnerBuilder(currentLevel + 1);
		
		final SWTGuiWidget widget = builder.build(attrib, data, innerBuilder);
		if (widget == null)
			return null;
		
		widget.init(layout, innerBuilder, copyManager);
		configureWidget(widget, attrib);
		
		return widget;
	}
	
	private InnerBuilder createInnerBuilder(final int level)
	{
		if (level <= data.maxAttributeSubLevels)
		{
			return new InnerBuilder() {
				private boolean building = false;
				private Set<GuiWidget> filter;
				
				@Override
				public boolean canBuildInnerAttribute()
				{
					return true;
				}
				
				@Override
				public void startBuilding()
				{
					if (building)
						throw new IllegalStateException();
					
					building = true;
					
					filter = new HashSet<GuiWidget>(widgets.findAllWidgets());
					startInitialize();
				}
				
				@Override
				public SWTGuiWidget buildInnerAttribute(SWTLayoutBuilder layout, Attribute attrib)
				{
					if (!building)
						throw new IllegalStateException();
					
					return addAttribute(layout, null, attrib, level);
				}
				
				@Override
				public void finishBuilding()
				{
					if (!building)
						throw new IllegalStateException();
					
					building = false;
					
					finishInitialize(filter);
					filter = null;
				}
			};
		}
		else
		{
			return new InnerBuilder() {
				@Override
				public boolean canBuildInnerAttribute()
				{
					return false;
				}
				
				@Override
				public void startBuilding()
				{
				}
				
				@Override
				public SWTGuiWidget buildInnerAttribute(SWTLayoutBuilder layout, Attribute attrib)
				{
					return null;
				}
				
				@Override
				public void finishBuilding()
				{
				}
			};
		}
	}
	
	private void configureWidget(final SWTGuiWidget widget, Attribute attrib)
	{
		FieldConfig config = data.fieldsConfig.get(attrib.getName());
		AttributeValueRange range = attrib.getValueRange();
		
		if (config != null)
		{
			if (config.showNameAsShadowText && config.shadowText == null)
			{
				String attribDescription = attrib.getName();
				if (attribDescription != null)
					attribDescription = data.textTranslator.fieldName(attribDescription);
				widget.setShadowText(attribDescription);
			}
			else
			{
				widget.setShadowText(config.shadowText);
			}
			
			if (widget instanceof TextBasedGuiWidget)
				((TextBasedGuiWidget) widget).setFormater(config.formater);
		}
		
		List<WidgetValidator> validators = new ArrayList<WidgetValidator>();
		if (config != null && config.validators != null)
			validators.addAll(Arrays.asList(config.validators));
		if (range != null && range.getValidators() != null)
			validators.addAll(Arrays.asList(range.getValidators()));
		if (validators.size() > 0)
			widget.setValidators(validators.toArray(new WidgetValidator[validators.size()]));
	}
	
	private void addAttributes(SWTLayoutBuilder layout, AttributeGroup group, int currentLevel)
	{
		for (Attribute attrib : group.getAttributes())
			widgets.addWidget(addAttribute(layout, null, attrib, currentLevel));
	}
	
	public void copyToGUI()
	{
		copyToGUI(null);
	}
	
	private void copyToGUI(Set<GuiWidget> filter)
	{
		Set<GuiWidget> handled = new HashSet<GuiWidget>();
		if (filter != null)
			handled.addAll(filter);
		
		Collection<GuiWidget> allWidgets = widgets.findAllLeafWidgets();
		int oldSize;
		do
		{
			oldSize = allWidgets.size();
			
			for (GuiWidget widget : allWidgets)
			{
				if (handled.contains(widget))
					continue;
				handled.add(widget);
				
				widget.copyToGUI();
			}
			allWidgets = widgets.findAllLeafWidgets();
		}
		while (oldSize != allWidgets.size());
	}
	
	public void copyToModel()
	{
		widgets.copyToModel();
	}
	
	void onGuiUpdated(GuiWidget widget)
	{
		if (initializing > 0)
			return;
		
		listenerManager.notifyChange(widget.getAttribute().getName(), widget);
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
	
	public static class LayoutEvent extends TypedEvent
	{
		private static final long serialVersionUID = 41527952251823222L;
		
		public LayoutEvent(Event e)
		{
			super(e);
		}
		
		public LayoutEvent(Object object)
		{
			super(object);
		}
	}
	
	public static interface LayoutListener extends SWTEventListener
	{
		/**
		 * Sent when the layout changes in this composite due to add or remove
		 * items from a list.
		 * 
		 * @param e an event containing information about the operation
		 */
		public void layoutChanged(LayoutEvent e);
	}
	
	private final List<LayoutListener> layoutListeners = new LinkedList<LayoutListener>();
	
	public void addLayoutListener(LayoutListener listener)
	{
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		layoutListeners.add(listener);
	}
	
	public void removeLayoutListener(LayoutListener listener)
	{
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		layoutListeners.remove(listener);
	}
}
