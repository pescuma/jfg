package jfg;

import java.util.Collection;
import java.util.Comparator;

public interface AttributeValueRange
{
	Comparator<Object> getComparator();
	Object getMax();
	Object getMin();
	
	Collection<Object> getPossibleValues();
	
	boolean canBeNull();
	
}
