package jfg;

import java.util.Collection;

public interface AttributeGroup
{
	String getName();
	
	/** @return a list of Attributes/Groups */
	Collection<Object> getAttributes();
}
