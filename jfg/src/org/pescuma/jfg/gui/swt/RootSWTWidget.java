package org.pescuma.jfg.gui.swt;

import java.util.Collection;

import org.pescuma.jfg.gui.GuiWidget;

public class RootSWTWidget extends CompositeSWTWidget
{
	public RootSWTWidget(JfgFormData data)
	{
		super(null, data);
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
	{
	}
	
	public Collection<GuiWidget> findAllLeafWidgets()
	{
		return new ChildManipulationLogic(this).findAllLeafWidgets();
	}
	
	public Collection<GuiWidget> findAllWidgets()
	{
		return new ChildManipulationLogic(this).findAllWidgets();
	}
}
