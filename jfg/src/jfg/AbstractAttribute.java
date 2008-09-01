package jfg;

public abstract class AbstractAttribute implements Attribute
{
	public AttributeValueRange getValueRange()
	{
		return null;
	}
	
	public boolean canWrite()
	{
		return true;
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
