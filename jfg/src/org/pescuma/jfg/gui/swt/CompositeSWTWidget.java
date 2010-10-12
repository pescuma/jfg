package org.pescuma.jfg.gui.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.WidgetValidator;

public abstract class CompositeSWTWidget extends AbstractSWTWidget
{
	protected final List<GuiWidget> children = new ArrayList<GuiWidget>();
	
	public CompositeSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	public void addWidget(final SWTGuiWidget widget)
	{
		if (widget == null)
			return;
		
		children.add(widget);
		
		widget.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				if (!children.remove(widget))
					throw new IllegalStateException();
			}
		});
	}
	
	@Override
	public void copyToGUI()
	{
		for (GuiWidget widget : children)
			widget.copyToGUI();
	}
	
	@Override
	public void copyToModel()
	{
		for (GuiWidget widget : children)
			widget.copyToModel();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		for (GuiWidget widget : children)
			widget.setEnabled(enabled);
	}
	
	@Override
	public Object getValue()
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setValue(Object value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setShadowText(String text)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setValidators(WidgetValidator... validator)
	{
		for (GuiWidget widget : children)
			widget.setValidators(validator);
	}
	
	@Override
	public Collection<ValidationError> getValidationErrors()
	{
		List<ValidationError> ret = new LinkedList<GuiWidget.ValidationError>();
		for (GuiWidget widget : children)
		{
			Collection<ValidationError> errors = widget.getValidationErrors();
			if (errors != null)
				ret.addAll(errors);
		}
		return ret;
	}
	
	@Override
	public Collection<GuiWidget> getChildren()
	{
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public GuiWidget getChild(String attributeName)
	{
		return new ChildManipulationLogic(this).getChild(attributeName);
	}
	
	@Override
	public Collection<GuiWidget> getChildren(String attributeName)
	{
		return new ChildManipulationLogic(this).getChildren(attributeName);
	}
	
	@Override
	public GuiWidget findChild(String attributeName)
	{
		return new ChildManipulationLogic(this).findChild(attributeName);
	}
	
	@Override
	public Collection<GuiWidget> findChildren(String attributeName)
	{
		return new ChildManipulationLogic(this).findChildren(attributeName);
	}
	
}
