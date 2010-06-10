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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiUpdateListener;
import org.pescuma.jfg.gui.GuiWidget;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class JfgFormComposite extends Composite
{
	private final JfgFormData data;
	private final BaseWidgetList widgets = new BaseWidgetList();
	private final GuiCopyManager copyManager;
	private final BaseGuiListenerManager listenerManager = new BaseGuiListenerManager();
	private boolean initializing = true;
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
		initializing = true;
	}
	
	private void finishInitialize()
	{
		if (postponeFinishInitialize)
			return;
		
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToGUI();
		
		for (AttributeWidgetPair aw : widgets)
			listenerManager.notifyChange(aw.attrib.getName(), aw.widget, widgets);
		
		initializing = false;
	}
	
	/** Add all the attributes from the group, without adding the group itself */
	public void addContentsFrom(AttributeGroup group)
	{
		startInitialize();
		buildAttributes(initLayout(), group, 0);
		finishInitialize();
	}
	
	public void add(Attribute attrib)
	{
		startInitialize();
		buildAttribute(initLayout(), attrib, 0);
		finishInitialize();
	}
	
	public void add(AttributeGroup group)
	{
		startInitialize();
		buildGroup(initLayout(), group, 0);
		finishInitialize();
	}
	
	public void addText(Attribute text)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTTextBuilder(), text);
		finishInitialize();
	}
	
	public void addNumer(Attribute number)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTNumberBuilder(), number);
		finishInitialize();
	}
	
	public void addReal(Attribute real)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTRealBuilder(), real);
		finishInitialize();
	}
	
	public void addCheckbox(Attribute bool)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTCheckboxBuilder(), bool);
		finishInitialize();
	}
	
	public void addCombo(Attribute enumer)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTComboBuilder(), enumer);
		finishInitialize();
	}
	
	public void addPassword(Attribute enumer)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTPasswordBuilder(), enumer);
		finishInitialize();
	}
	
	public void addScale(Attribute enumer)
	{
		startInitialize();
		addAttribute(initLayout(), new SWTScaleBuilder(), enumer);
		finishInitialize();
	}
	
	public void addCustom(SWTWidgetBuilder builder, Attribute custom)
	{
		startInitialize();
		addAttribute(initLayout(), builder, custom);
		finishInitialize();
	}
	
	private void addAttribute(SWTLayoutBuilder layout, SWTWidgetBuilder builder, Attribute attrib)
	{
		if (!builder.accept(attrib))
			throw new IllegalArgumentException("Wrong configuration");
		
		for (SWTAttributeFilter filter : data.attributeFilters)
			if (filter.hideAttribute(attrib))
				return;
		
		final SWTGuiWidget widget = builder.build(attrib, data);
		widget.init(layout, copyManager);
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
	
	private void buildAttributes(SWTLayoutBuilder layout, AttributeGroup group, int currentLevel)
	{
		for (Object attrib : group.getAttributes())
		{
			if (attrib instanceof AttributeGroup)
				buildGroup(layout, (AttributeGroup) attrib, currentLevel);
			else if (attrib instanceof Attribute)
				buildAttribute(layout, (Attribute) attrib, currentLevel);
		}
	}
	
	private void buildAttribute(SWTLayoutBuilder layout, Attribute attrib, int currentLevel)
	{
		SWTWidgetBuilder builder = getBuilderFor(attrib);
		if (builder != null)
		{
			addAttribute(layout, builder, attrib);
			return;
		}
		
		AttributeList list = attrib.asList();
		if (list != null)
		{
			if (attrib.canWrite())
				System.out.println("[JFG] Creating GUI for read/write list. "
						+ "I'll only change the list in place and will not check for changes in it!");
			
			buildList(layout, list, currentLevel);
			return;
		}
		
		AttributeGroup group = attrib.asGroup();
		if (group != null)
		{
			if (attrib.canWrite())
				System.out.println("[JFG] Creating GUI for read/write object. "
						+ "I'll only change the object in place and will not check for changes in it!");
			
			buildGroup(layout, group, currentLevel + 1);
		}
	}
	
	private void buildList(SWTLayoutBuilder layout, final AttributeList list, final int currentLevel)
	{
		layout.startList(list.getName());
		
		for (int i = 0; i < list.size(); i++)
			buildAttributeInsideList(layout, list, list.get(i), currentLevel);
		
		final SWTLayoutBuilder[] listLayout = new SWTLayoutBuilder[1];
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				buildAttributeInsideList(listLayout[0], list, list.add(), currentLevel);
			}
		};
		
		Control addMore = data.componentFactory.createFlatButton(layout.getParentForAddMore(),
				data.textTranslator.translate("Add", list.getName()), "icons/add.png", listener);
		
		listLayout[0] = layout.endList(list.getName(), addMore);
	}
	
	private void buildAttributeInsideList(final SWTLayoutBuilder layout, final AttributeList list,
			final Attribute attrib, int currentLevel)
	{
		layout.startListItem(list.getName());
		
		SWTWidgetBuilder builder = getBuilderFor(attrib);
		if (builder != null)
		{
			addAttribute(layout, builder, attrib);
		}
		else
		{
			AttributeList innerList = attrib.asList();
			if (innerList != null)
				// TODO
				throw new NotImplementedException();
			
			AttributeGroup group = attrib.asGroup();
			if (group != null)
				buildAttributes(layout, group, currentLevel);
		}
		
		final SWTLayoutBuilder.ListItem[] listItem = new SWTLayoutBuilder.ListItem[1];
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				int indexOf = list.indexOf(attrib);
				if (indexOf < 0)
					throw new IllegalStateException();
				
				list.remove(indexOf);
				layout.removeListItem(listItem[0]);
			}
		};
		Control remove = data.componentFactory.createFlatButton(layout.getParentForRemove(),
				data.textTranslator.translate("Remove", list.getName()), "icons/delete.png", listener);
		listItem[0] = layout.endListItem(list.getName(), remove);
	}
	
	private void buildGroup(SWTLayoutBuilder layout, AttributeGroup group, int currentLevel)
	{
		if (currentLevel > data.maxGroupAttributeLevels)
			return;
		
		layout.startGroup(group.getName());
		
		buildAttributes(layout, group, currentLevel);
		
		layout.endGroup(group.getName());
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
