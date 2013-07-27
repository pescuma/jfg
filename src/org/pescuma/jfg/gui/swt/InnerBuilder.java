package org.pescuma.jfg.gui.swt;

import org.pescuma.jfg.Attribute;

public interface InnerBuilder
{
	boolean canBuildInnerAttribute();
	
	void startBuilding();
	
	SWTGuiWidget buildInnerAttribute(SWTLayoutBuilder layout, Attribute attrib);
	
	void finishBuilding();
}