package org.pescuma.jfg.gui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public interface SWTLayoutBuilder
{
	void init(Composite root, JfgFormData data);
	
	void finish();
	
	void startGroup(String groupName);
	
	void endGroup(String groupName);
	
	Composite[] getParentsForLabelWidget(String attributeName);
	
	void addLabelWidget(String attributeName, Label label, Control widget, boolean wantToFillVertical);
	
	Composite getParentForWidget(String attributeName);
	
	void addWidget(String attributeName, Control widget, boolean wantToFillVertical);
	
}
