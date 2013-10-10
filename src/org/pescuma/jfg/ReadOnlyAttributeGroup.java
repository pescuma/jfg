package org.pescuma.jfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReadOnlyAttributeGroup implements AttributeGroup
{
	private final AttributeGroup group;
	
	public ReadOnlyAttributeGroup(AttributeGroup group)
	{
		this.group = group;
	}
	
	@Override
	public String getName()
	{
		return group.getName();
	}
	
	@Override
	public Collection<Attribute> getAttributes()
	{
		List<Attribute> result = new ArrayList<Attribute>();
		
		for (Attribute attribute : group.getAttributes())
			result.add(new ReadOnlyAttribute(attribute));
		
		return result;
	}
	
	@Override
	public boolean canListen()
	{
		return group.canListen();
	}
	
	@Override
	public void addListener(AttributeListener listener)
	{
		group.addListener(listener);
	}
	
	@Override
	public void removeListener(AttributeListener listener)
	{
		group.removeListener(listener);
	}
	
}
