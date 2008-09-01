package jfg;

public abstract class AbstractReadOnlyAttribute implements Attribute
{
	public AttributeValueRange getValueRange()
	{
		return null;
	}
	
	public boolean canWrite()
	{
		return false;
	}
	
	public void setValue(Object obj)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean canListen()
	{
		return false;
	}
	
	public void addListener(AttributeListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	public void removeListener(AttributeListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	public AttributeGroup asGroup()
	{
		return null;
	}
	
}
