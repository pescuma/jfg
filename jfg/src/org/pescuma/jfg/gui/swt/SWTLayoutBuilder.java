package org.pescuma.jfg.gui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Simple layout manager for widgets.
 * 
 * A new layout builder has to be created for each group/list
 */
public interface SWTLayoutBuilder
{
	void init(Composite parent, Runnable layoutListener, JfgFormData data);
	Runnable getLayoutListener();
	
	Composite[] getParentsForLabelWidget(String attributeName);
	void addLabelWidget(String attributeName, Label label, Control widget, int layoutHints, int heightHint);
	
	Composite getParentForWidget(String attributeName);
	void addWidget(String attributeName, Control widget, int layoutHints, int heightHint);
	
	Group addGroup(String groupName, int layoutHints, int heightHint);
	ListBuilder addList(String attributeName, int layoutHints, int heightHint);
	
	// To use one model as a factory
	SWTLayoutBuilder clone();
	
	static interface ListBuilder
	{
		static interface ListItem {}
		
		Composite getContents();
		Runnable getLayoutListener();
		
		Composite getParentForAddMore();
		void addAddMore(Control addMore);

		Composite startListItem(String attributeName);
		Composite getParentForRemove();
		ListItem endListItem(String attributeName, Control remove);
		void removeListItem(ListItem item);
		void moveAfter(ListItem baseItem, ListItem itemToMove);
	}
}
