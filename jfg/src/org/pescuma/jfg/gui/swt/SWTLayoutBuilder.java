package org.pescuma.jfg.gui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public interface SWTLayoutBuilder
{
	void init(Composite root, Runnable layoutListener, JfgFormData data);
	
	Group startGroup(String groupName);
	void endGroup(String groupName);
	
	Composite[] getParentsForLabelWidget(String attributeName);
	void addLabelWidget(String attributeName, Label label, Control widget, boolean wantToFillVertical);
	
	Composite getParentForWidget(String attributeName);
	void addWidget(String attributeName, Control widget, boolean wantToFillVertical);
	
	void startList(String attributeName);
	Composite getParentForAddMore();
	SWTLayoutBuilder endList(String attributeName, Control addMore);

	static interface ListItem {}
	
	void startListItem(String attributeName);
	Composite getParentForRemove();
	ListItem endListItem(String attributeName, Control remove);
	void removeListItem(ListItem item);
	void moveAfter(ListItem baseItem, ListItem itemToMove);
}
