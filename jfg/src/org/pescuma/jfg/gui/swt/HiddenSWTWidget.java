package org.pescuma.jfg.gui.swt;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.GuiWidgetListener;

public class HiddenSWTWidget extends AbstractSWTWidget
{
	public HiddenSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected void onChildDisposed(SWTGuiWidget child)
	{
		super.onChildDisposed(child);
		
		if (children.size() == 0)
		{
			// Dispose this one too
			for (GuiWidgetListener l : listeners)
				l.onWidgetDisposed(this);
		}
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
	{
	}
	
	@Override
	protected boolean canCopyToAttribute()
	{
		return false;
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
	public Object getValue()
	{
		return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		throw new IllegalStateException();
	}
	
}
