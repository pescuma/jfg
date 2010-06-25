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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiUpdateListener;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.GuiWidgetList;

public class JfgFormComposite extends Composite
{
	private final JfgFormData data;
	private final BaseWidgetList widgets = new BaseWidgetList();
	private final GuiCopyManager copyManager;
	private final BaseGuiListenerManager listenerManager = new BaseGuiListenerManager();
	private int initializing = 0;
	private boolean layoutInitialized = false;
	private boolean postponeFinishInitialize = false;
	
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
		
		final Composite root = getRoot();
		if (!root.isVisible())
		{
			postponeFinishInitialize = true;
			
			root.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event)
				{
					postponeFinishInitialize = false;
					finishInitialize();
					
					root.removeListener(SWT.Show, this);
				}
			});
		}
	}
	
	public GuiWidgetList getWidgets()
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
			data.layout.init(this, new Runnable() {
				@Override
				public void run()
				{
					layout();
					
					LayoutEvent event = new LayoutEvent(JfgFormComposite.this);
					event.widget = JfgFormComposite.this;
					event.display = event.widget.getDisplay();
					event.time = 0; // TODO
					
					for (LayoutListener l : layoutListeners)
						l.layoutChanged(event);
				}
			}, data);
			layoutInitialized = true;
		}
		return data.layout;
	}
	
	private void startInitialize()
	{
		initializing++;
	}
	
	private void finishInitialize()
	{
		finishInitialize(null);
	}
	
	private void finishInitialize(Set<AttributeWidgetPair> filter)
	{
		if (postponeFinishInitialize)
			return;
		
		copyToGUI(filter);
		notifyAllListeners(filter);
		
		initializing--;
	}
	
	private void notifyAllListeners(Set<AttributeWidgetPair> filter)
	{
		for (AttributeWidgetPair aw : widgets)
		{
			if (filter != null && filter.contains(aw))
				continue;
			
			listenerManager.notifyChange(aw.attrib.getName(), aw.widget, widgets);
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
		addAttribute(initLayout(), builder, attrib, 0);
		finishInitialize();
	}
	
	private void addAttribute(SWTLayoutBuilder layout, SWTWidgetBuilder builder, Attribute attrib,
			final int currentLevel)
	{
		if (builder == null)
			builder = data.getBuilderFor(attrib);
		if (builder == null)
			return;
		
		if (!builder.accept(attrib))
			throw new IllegalArgumentException("Wrong configuration");
		
		for (SWTAttributeFilter filter : data.attributeFilters)
			if (filter.hideAttribute(attrib))
				return;
		
		SWTGuiWidget.InnerBuilder innerBuilder = new SWTGuiWidget.InnerBuilder() {
			@Override
			public boolean canBuildInnerAttribute()
			{
				return currentLevel < data.maxAttributeSubLevels;
			}
			
			@Override
			public void buildInnerAttribute(SWTLayoutBuilder layout, Attribute attrib)
			{
				Set<AttributeWidgetPair> filter = new HashSet<AttributeWidgetPair>(widgets.getWidgetList());
				
				startInitialize();
				addAttribute(layout, null, attrib, currentLevel + 1);
				finishInitialize(filter);
			}
		};
		
		final SWTGuiWidget widget = builder.build(attrib, data);
		widget.init(layout, innerBuilder, copyManager);
		widgets.add(attrib, widget);
		
		widget.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				if (!widgets.remove(widget))
					throw new IllegalStateException();
			}
		});
	}
	
	private void addAttributes(SWTLayoutBuilder layout, AttributeGroup group, int currentLevel)
	{
		for (Attribute attrib : group.getAttributes())
			addAttribute(layout, null, attrib, currentLevel);
	}
	
	public void copyToGUI()
	{
		copyToGUI(null);
	}
	
	private void copyToGUI(Set<AttributeWidgetPair> filter)
	{
		Set<AttributeWidgetPair> handled = new HashSet<AttributeWidgetPair>();
		
		int oldSize;
		do
		{
			oldSize = widgets.size();
			
			for (AttributeWidgetPair aw : new ArrayList<AttributeWidgetPair>(widgets.getWidgetList()))
			{
				if (filter != null && filter.contains(aw))
					continue;
				if (handled.contains(aw))
					continue;
				handled.add(aw);
				
				aw.widget.copyToGUI();
			}
		}
		while (oldSize != widgets.size());
	}
	
	public void copyToModel()
	{
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToModel();
	}
	
	void onGuiUpdated(GuiWidget widget)
	{
		if (initializing > 0)
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
