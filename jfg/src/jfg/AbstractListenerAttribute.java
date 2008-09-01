package jfg;

public abstract class AbstractListenerAttribute implements Attribute
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
		return true;
	}
	
	public AttributeGroup asGroup()
	{
		return null;
	}
}
