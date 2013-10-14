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

import static org.eclipse.swt.layout.GridData.*;
import static org.pescuma.jfg.gui.swt.SWTUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.pescuma.jfg.gui.ObjectListGuiWidget;
import org.pescuma.jfg.gui.swt.JfgFormData.FieldConfig;
import org.pescuma.jfg.gui.swt.SWTLayoutBuilder.ListBuilder;

public class SWTSimpleFormListBuilder implements ListBuilder
{
	private final Group parent;
	private final Runnable layoutListener;
	private final JfgFormData data;
	private final int originalColumnCount;
	private final boolean allowToCollapse;
	
	private int listItemStart = 0;
	private Composite addMoreParent;
	private boolean addingEmptyItem;
	
	private static class ControlsToRemove implements ListItem
	{
		final List<Control> constrols = new ArrayList<Control>();
	}
	
	public SWTSimpleFormListBuilder(String attributeName, Group frame, Runnable layoutListener, JfgFormData data)
	{
		this.parent = frame;
		this.layoutListener = layoutListener;
		this.data = data;
		originalColumnCount = ((GridLayout) parent.getLayout()).numColumns;
		
		FieldConfig config = data.fieldsConfig.get(attributeName);
		if (config != null && config.widgetData != null)
		{
			ObjectListGuiWidget.Data widgetData = (ObjectListGuiWidget.Data) config.widgetData;
			allowToCollapse = widgetData.collapse;
		}
		else
		{
			allowToCollapse = data.allowCollapseObjectsInListByDefault;
		}
	}
	
	@Override
	public Composite getContents()
	{
		return parent;
	}
	
	@Override
	public Runnable getLayoutListener()
	{
		return layoutListener;
	}
	
	@Override
	public Composite getParentForAddMore()
	{
		addMoreParent = data.componentFactory.createComposite(parent, SWT.NONE);
		setupHorizontalComposite(addMoreParent, Integer.MAX_VALUE);
		return addMoreParent;
	}
	
	@Override
	public void addAddMore(Control addMore)
	{
		if (addMore != null)
			addMore.setLayoutData(new GridData(HORIZONTAL_ALIGN_BEGINNING));
	}
	
	@Override
	public Composite startListItem(String attributeName, boolean addingNewAndEmpty)
	{
		addingEmptyItem = addingNewAndEmpty;
		listItemStart = parent.getChildren().length;
		return parent;
	}
	
	@Override
	public Composite getParentForRemove()
	{
		Control[] children = parent.getChildren();
		if (children.length == listItemStart)
		{
			// Create empty composite to fill space
			Composite composite = data.componentFactory.createComposite(parent, SWT.NONE);
			setupHorizontalComposite(composite, getCurrentParentNumColumns());
		}
		return parent;
	}
	
	@Override
	public ListItem endListItem(String attributeName, Control remove)
	{
		int addMoreOffset = (addMoreParent != null ? -1 : 0);
		
		if (addMoreParent != null)
		{
			Control[] children = parent.getChildren();
			
			int index = indexOf(children, addMoreParent);
			if (index < 0)
				throw new IllegalStateException();
			if (index < listItemStart)
				listItemStart--;
			
			addMoreParent.moveBelow(null);
		}
		
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		boolean isFirstItem = (originalColumnCount == parentLayout.numColumns);
		
		int lines = computeNumberOfLines(remove, addMoreOffset);
		
		if (remove != null)
		{
			int internalNumColumns = parentLayout.numColumns;
			
			if (isFirstItem)
				parentLayout.numColumns++;
			
			remove.moveBelow(null);
			remove.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
			Control[] children = parent.getChildren();
			
			if (lines == 0)
				throw new IllegalStateException();
			
			int moveBelow = findControlAfterColumns(children, listItemStart, internalNumColumns) - 1;
			remove.moveBelow(children[moveBelow]);
			
			for (int i = 1; i < lines; i++)
			{
				Label empty = data.componentFactory.createLabel(parent, SWT.NONE);
				empty.setLayoutData(new GridData());
				
				moveBelow = findControlAfterColumns(children, moveBelow + 1, internalNumColumns) - 1;
				empty.moveBelow(children[moveBelow]);
			}
		}
		
		if (allowToCollapse && lines > 1)
		{
			final int internalNumColumns = parentLayout.numColumns;
			
			if (isFirstItem)
				parentLayout.numColumns++;
			
			Control[] allChildren = parent.getChildren();
			final Control[] children = Arrays.copyOfRange(allChildren, listItemStart, allChildren.length
					+ addMoreOffset);
			
			final Control[] expand = new Control[1];
			
			Listener expandListener = new Listener() {
				private boolean expanded = true;
				
				@Override
				public void handleEvent(Event event)
				{
					expanded = !expanded;
					
					data.componentFactory.changeFlatButtonIcon(expand[0], expanded ? "icons/collapse.png"
							: "icons/expand.png");
					
					int firstCtrl = findControlAfterColumns(children, 0, internalNumColumns);
					for (int i = firstCtrl; i < children.length; i++)
					{
						children[i].setVisible(expanded);
						getGridData(children[i]).exclude = !expanded;
					}
					
					layoutListener.run();
				}
			};
			
			expand[0] = data.componentFactory.createFlatButton(parent, "", "icons/collapse.png", expandListener);
			
			expand[0].setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			expand[0].moveAbove(children[0]);
			
			int firstCtrlInLine = findControlAfterColumns(children, 0, internalNumColumns);
			for (int i = 1; i < lines; i++)
			{
				getGridData(children[firstCtrlInLine]).horizontalSpan++;
				// +1 because we just added one to firstCtrlInLine
				firstCtrlInLine = findControlAfterColumns(children, firstCtrlInLine, internalNumColumns + 1);
			}
			
			if (!addingEmptyItem)
				expandListener.handleEvent(null);
		}
		
		if (addMoreParent != null)
			getGridData(addMoreParent).horizontalSpan = getCurrentParentNumColumns();
		
		SWTSimpleFormListBuilder.ControlsToRemove constrols = new ControlsToRemove();
		
		Control[] children = parent.getChildren();
		for (int i = listItemStart; i < children.length + addMoreOffset; i++)
			constrols.constrols.add(children[i]);
		
		layoutListener.run();
		
		return constrols;
	}
	
	private int computeNumberOfLines(Control remove, int addMoreOffset)
	{
		int lines;
		{
			Control[] children = parent.getChildren();
			
			lines = 0;
			for (int i = listItemStart; i < children.length + addMoreOffset; i++)
				if (children[i] != remove)
					lines += getGridData(children[i]).horizontalSpan;
			lines /= getCurrentParentNumColumns();
		}
		return lines;
	}
	
	private int getCurrentParentNumColumns()
	{
		GridLayout layout = (GridLayout) parent.getLayout();
		if (layout == null)
			return 1;
		return layout.numColumns;
	}
	
	private int findControlAfterColumns(Control[] children, int start, int numCols)
	{
		for (int columns = 0; columns < numCols; start++)
			columns += getGridData(children[start]).horizontalSpan;
		return start;
	}
	
	private int indexOf(Control[] arr, Control find)
	{
		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i] == find)
				return i;
		}
		return -1;
	}
	
	private GridData getGridData(Control children)
	{
		return (GridData) children.getLayoutData();
	}
	
	@Override
	public void removeListItem(ListItem item)
	{
		SWTSimpleFormListBuilder.ControlsToRemove constrols = (SWTSimpleFormListBuilder.ControlsToRemove) item;
		for (Control control : constrols.constrols)
			control.dispose();
		layoutListener.run();
	}
	
	@Override
	public void moveAfter(ListItem baseItem, ListItem itemToMove)
	{
		SWTSimpleFormListBuilder.ControlsToRemove constrolsToMove = (SWTSimpleFormListBuilder.ControlsToRemove) itemToMove;
		SWTSimpleFormListBuilder.ControlsToRemove baseConstrols = (SWTSimpleFormListBuilder.ControlsToRemove) baseItem;
		
		if (baseConstrols == null)
		{
			for (int i = constrolsToMove.constrols.size() - 1; i > 0; i--)
				constrolsToMove.constrols.get(i).moveAbove(null);
		}
		else
		{
			Control base = baseConstrols.constrols.get(baseConstrols.constrols.size() - 1);
			for (int i = constrolsToMove.constrols.size() - 1; i > 0; i--)
				constrolsToMove.constrols.get(i).moveBelow(base);
		}
		
		layoutListener.run();
	}
}
