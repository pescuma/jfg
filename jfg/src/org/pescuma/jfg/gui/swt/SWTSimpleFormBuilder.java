package org.pescuma.jfg.gui.swt;

import static org.eclipse.swt.layout.GridData.*;
import static org.pescuma.jfg.gui.swt.SWTHelper.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SWTSimpleFormBuilder implements SWTLayoutBuilder
{
	private static class Item
	{
		Composite parent;
		
		public Item(Composite parent)
		{
			this.parent = parent;
		}
	}
	
	private static class ListItem extends Item
	{
		int listItemStart = 0;
		Control addMoreParent;
		boolean ended = false;
		
		public ListItem(Composite parent)
		{
			super(parent);
		}
	}
	
	private JfgFormData data;
	private Runnable layoutListener;
	private final List<Item> currents = new ArrayList<Item>();
	
	public SWTSimpleFormBuilder()
	{
	}
	
	SWTSimpleFormBuilder(ListItem start, Runnable layoutListener, JfgFormData data)
	{
		this.layoutListener = layoutListener;
		this.data = data;
		currents.add(start);
	}
	
	@Override
	public void init(Composite root, Runnable layoutListener, JfgFormData data)
	{
		if (currents.size() != 0)
			throw new IllegalStateException();
		
		this.layoutListener = layoutListener;
		this.data = data;
		
		GridLayout layout = (GridLayout) root.getLayout();
		
		if (layout == null)
			root.setLayout(createBorderlessGridLayout(2, false));
		
		else if (!(layout instanceof GridLayout))
			throw new IllegalArgumentException("JfgFormComposite needs a GridLayout");
		
		else
			layout.numColumns = 2;
		
		currents.add(new Item(root));
	}
	
	private Composite getCurrent()
	{
		return currents.get(currents.size() - 1).parent;
	}
	
	public Composite[] getParentsForLabelWidget(String attributeName)
	{
		Composite current = getCurrent();
		return new Composite[] { current, current };
	}
	
	public void addLabelWidget(String attributeName, Label label, Control widget, boolean wantToFillVertical)
	{
		label.setLayoutData(new GridData(HORIZONTAL_ALIGN_END));
		widget.setLayoutData(new GridData(wantToFillVertical ? FILL_BOTH : FILL_HORIZONTAL));
	}
	
	public Composite getParentForWidget(String attributeName)
	{
		return createFullRowComposite();
	}
	
	public void addWidget(String attributeName, Control widget, boolean wantToFillVertical)
	{
		widget.setLayoutData(new GridData(wantToFillVertical ? FILL_BOTH : FILL_HORIZONTAL));
	}
	
	public Group startGroup(String groupName)
	{
		Group frame = data.componentFactory.createGroup(createFullRowComposite(), SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.groupName(groupName));
		
		currents.add(new Item(frame));
		
		return frame;
	}
	
	public void endGroup(String groupName)
	{
		if (currents.size() < 2)
			throw new IllegalStateException();
		
		currents.remove(currents.size() - 1);
	}
	
	private Composite createFullRowComposite()
	{
		Composite composite = data.componentFactory.createComposite(getCurrent(), SWT.NONE);
		setupHorizontalComposite(composite, 2);
		return composite;
	}
	
	@Override
	public Composite startList(String attributeName)
	{
		Group frame = data.componentFactory.createGroup(createFullRowComposite(), SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(3, false));
		frame.setText(data.textTranslator.fieldName(attributeName));
		
		currents.add(new ListItem(frame));
		
		return frame;
	}
	
	@Override
	public Composite getParentForAddMore()
	{
		assertCurrentIsList();
		
		Composite composite = data.componentFactory.createComposite(getCurrent(), SWT.NONE);
		setupHorizontalComposite(composite, 3);
		
		getCurrentList().addMoreParent = composite;
		
		return composite;
	}
	
	@Override
	public SWTLayoutBuilder endList(String attributeName, Control addMore)
	{
		assertCurrentIsList();
		
		if (addMore != null)
			addMore.setLayoutData(new GridData(FILL_HORIZONTAL));
		
		ListItem current = getCurrentList();
		current.ended = true;
		
		currents.remove(currents.size() - 1);
		
		return new SWTSimpleFormBuilder(current, layoutListener, data);
	}
	
	@Override
	public void startListItem(String attributeName)
	{
		assertCurrentIsList();
		
		ListItem item = getCurrentList();
		item.listItemStart = item.parent.getChildren().length;
	}
	
	@Override
	public Composite getParentForRemove()
	{
		assertCurrentIsList();
		
		ListItem item = getCurrentList();
		Control[] children = item.parent.getChildren();
		if (children.length == item.listItemStart)
			createFullRowComposite();
		
		return getCurrent();
	}
	
	private static class ControlsToRemove implements SWTLayoutBuilder.ListItem
	{
		final List<Control> constrols = new ArrayList<Control>();
	}
	
	@Override
	public SWTLayoutBuilder.ListItem endListItem(String attributeName, Control remove)
	{
		assertCurrentIsList();
		
		ListItem item = getCurrentList();
		int addMoreOffset = (item.addMoreParent != null ? -1 : 0);
		
		if (item.addMoreParent != null)
		{
			Control[] children = item.parent.getChildren();
			
			int index = indexOf(children, item.addMoreParent);
			if (index < 0)
				throw new IllegalStateException();
			if (index < item.listItemStart)
				item.listItemStart--;
			
			item.addMoreParent.moveBelow(null);
		}
		
		if (remove != null)
		{
			remove.moveBelow(null);
			
			Control[] children = item.parent.getChildren();
			
			int items = 0;
			for (int i = item.listItemStart; i < children.length - 1 + addMoreOffset; i++)
				items += getGridData(children[i]).horizontalSpan;
			items /= 2;
			
			if (items == 0)
				throw new IllegalStateException();
			
			int moveBelow = item.listItemStart + (getGridData(children[0]).horizontalSpan == 1 ? 1 : 0);
			remove.moveBelow(children[moveBelow]);
			
			if (items > 1)
			{
				Label empty = data.componentFactory.createLabel(item.parent, SWT.NONE);
				GridData gridData = new GridData();
				gridData.verticalSpan = items - 1;
				empty.setLayoutData(gridData);
				
				moveBelow++;
				moveBelow += (getGridData(children[moveBelow]).horizontalSpan == 1 ? 1 : 0);
				empty.moveBelow(children[moveBelow]);
			}
		}
		else
		{
			GridLayout layout = (GridLayout) item.parent.getLayout();
			layout.numColumns = 2;
		}
		
		Control[] children = item.parent.getChildren();
		
		ControlsToRemove constrols = new ControlsToRemove();
		for (int i = item.listItemStart; i < children.length + addMoreOffset; i++)
			constrols.constrols.add(children[i]);
		
		if (item.ended)
			layoutListener.run();
		
		return constrols;
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
	
	@Override
	public void moveAfter(SWTLayoutBuilder.ListItem baseItem, SWTLayoutBuilder.ListItem itemToMove)
	{
		assertCurrentIsList();
		ListItem item = getCurrentList();
		
		ControlsToRemove constrolsToMove = (ControlsToRemove) itemToMove;
		ControlsToRemove baseConstrols = (ControlsToRemove) baseItem;
		
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
		
		if (item.ended)
			layoutListener.run();
	}
	
	@Override
	public void removeListItem(SWTLayoutBuilder.ListItem item)
	{
		ControlsToRemove constrols = (ControlsToRemove) item;
		for (Control control : constrols.constrols)
			control.dispose();
		layoutListener.run();
	}
	
	private GridData getGridData(Control children)
	{
		return ((GridData) children.getLayoutData());
	}
	
	private void assertCurrentIsList()
	{
		Item item = currents.get(currents.size() - 1);
		if (!(item instanceof ListItem))
			throw new IllegalStateException();
	}
	
	private ListItem getCurrentList()
	{
		return (ListItem) currents.get(currents.size() - 1);
	}
}
