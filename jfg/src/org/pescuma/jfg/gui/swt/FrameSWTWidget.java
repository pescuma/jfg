/**
 * 
 */
package org.pescuma.jfg.gui.swt;

import org.eclipse.swt.widgets.Group;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class FrameSWTWidget extends AbstractSWTWidget
{
	private Group frame;
	
	FrameSWTWidget(Attribute attrib, JfgFormData data)
	{
		super(attrib, data);
	}
	
	@Override
	protected void createWidgets(SWTLayoutBuilder layout, InnerBuilder innerBuilder)
	{
		if (!innerBuilder.canBuildInnerAttribute())
			return;
		
		AttributeGroup group = attrib.asGroup();
		
		frame = layout.addGroup(group.getName());
		
		SWTLayoutBuilder innerLayout = data.createLayoutFor(group.getName(), frame, layout.getLayoutListener());
		
		for (Attribute ga : group.getAttributes())
			innerBuilder.buildInnerAttribute(innerLayout, ga);
	}
	
	@Override
	public Object getValue()
	{
		return attrib.getValue();
	}
	
	@Override
	public void setValue(Object value)
	{
		if (value != attrib.getValue())
			throw new NotImplementedException();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		frame.setEnabled(enabled);
	}
}