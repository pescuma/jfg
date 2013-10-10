package org.pescuma.jfg;

public class ReadOnlyAttributeList implements AttributeList
{
	private final AttributeList list;
	
	public ReadOnlyAttributeList(AttributeList list)
	{
		this.list = list;
	}
	
	@Override
	public String getName()
	{
		return list.getName();
	}
	
	@Override
	public Object getElementType()
	{
		return list.getElementType();
	}
	
	@Override
	public int size()
	{
		return list.size();
	}
	
	@Override
	public Attribute get(int index)
	{
		return new ReadOnlyAttribute(list.get(index));
	}
	
	@Override
	public int indexOf(Attribute attrib)
	{
		return list.indexOf(((ReadOnlyAttribute) attrib).getAttribute());
	}
	
	@Override
	public boolean canWrite()
	{
		return false;
	}
	
	@Override
	public Attribute createNewElement()
	{
		return null;
	}
	
	@Override
	public void add(int index, Attribute item)
	{
	}
	
	@Override
	public void remove(int index)
	{
	}
	
	@Override
	public boolean canListen()
	{
		return list.canListen();
	}
	
	@Override
	public void addListener(AttributeListener listener)
	{
		list.addListener(listener);
	}
	
	@Override
	public void removeListener(AttributeListener listener)
	{
		list.removeListener(listener);
	}
	
}
