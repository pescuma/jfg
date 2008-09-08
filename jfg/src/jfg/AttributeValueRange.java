package jfg;

import java.util.Collection;
import java.util.Comparator;

public interface AttributeValueRange
{
	Comparator<?> getComparator();
	Object getMax();
	Object getMin();
	
	Collection<?> getPossibleValues();
	
	boolean canBeNull();
	
}
