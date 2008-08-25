package jfg;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class ObjectReflectionGroup implements AttributeGroup
{
	private final Object obj;
	private ArrayList<Object> attributes;
	
	public ObjectReflectionGroup(Object obj)
	{
		this.obj = obj;
	}
	
	public Collection<Object> getAttributes()
	{
		if (attributes == null)
			loadAttributes();
		
		return attributes;
	}
	
	private void loadAttributes()
	{
		attributes = new ArrayList<Object>();
		
		for (Field field : obj.getClass().getFields())
		{
			attributes.add(new ObjectReflectionAttribute(obj, field));
		}
	}
	
	public String getName()
	{
		return obj.getClass().getSimpleName();
	}
	
}
