package org.pescuma.jfg;

public class ReadOnlyAttribute implements Attribute
{
	private final Attribute attrib;
	
	public ReadOnlyAttribute(Attribute attrib)
	{
		this.attrib = attrib;
	}
	
	@Override
	public String getName()
	{
		return attrib.getName();
	}
	
	@Override
	public Object getType()
	{
		return attrib.getType();
	}
	
	@Override
	public AttributeValueRange getValueRange()
	{
		return attrib.getValueRange();
	}
	
	@Override
	public boolean canWrite()
	{
		return false;
	}
	
	@Override
	public Object getValue()
	{
		return attrib.getValue();
	}
	
	@Override
	public void setValue(Object obj)
	{
	}
	
	@Override
	public boolean canListen()
	{
		return attrib.canListen();
	}
	
	@Override
	public void addListener(AttributeListener listener)
	{
		attrib.addListener(listener);
	}
	
	@Override
	public void removeListener(AttributeListener listener)
	{
		attrib.removeListener(listener);
	}
	
	@Override
	public AttributeGroup asGroup()
	{
		AttributeGroup group = attrib.asGroup();
		if (group == null)
			return null;
		else
			return new ReadOnlyAttributeGroup(group);
	}
	
	@Override
	public AttributeList asList()
	{
		AttributeList list = attrib.asList();
		if (list == null)
			return null;
		else
			return new ReadOnlyAttributeList(list);
	}
	
	public Attribute getAttribute()
	{
		return attrib;
	}
	
}
