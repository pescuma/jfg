package org.pescuma.jfg.gui.swt;

import java.util.Collection;

import org.eclipse.swt.events.DisposeListener;
import org.pescuma.jfg.gui.GuiCopyManager;
import org.pescuma.jfg.gui.GuiWidget;

public class RootSWTWidget extends AbstractSWTWidget
{
	public RootSWTWidget(JfgFormData data)
	{
		super(null, data);
	}
	
	@Override
	public void init(SWTLayoutBuilder layout, InnerBuilder innerBuilder, GuiCopyManager aManager)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
	{
	}
	
	@Override
	public void copyToModel()
	{
		for (GuiWidget widget : children)
			widget.copyToModel();
	}
	
	@Override
	public void copyToGUI()
	{
		for (GuiWidget widget : children)
			widget.copyToGUI();
	}
	
	@Override
	public void addDisposeListener(DisposeListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object getValue()
	{
		throw new IllegalStateException();
	}
	
	@Override
	public void setValue(Object value)
	{
		throw new IllegalStateException();
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
