package jfg;

import java.util.Collection;
import java.util.Comparator;

public abstract class AbstractAttributeValueRange implements AttributeValueRange
{
	public Comparator<Object> getComparator()
	{
		return null;
	}
	
	public Object getMax()
	{
		return null;
	}
	
	public Object getMin()
	{
		return null;
	}
	
	public boolean canBeNull()
	{
		return true;
	}
	
	public Collection<Object> getPossibleValues()
	{
		return null;
	}
	
}
