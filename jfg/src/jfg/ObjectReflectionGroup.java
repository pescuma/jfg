package jfg;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
		
		Class<?> cls = obj.getClass();
		
		for (Field field : cls.getFields())
		{
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			attributes.add(new ObjectReflectionAttribute(obj, field));
		}
		
		for (Method method : cls.getMethods())
		{
			if (Modifier.isStatic(method.getModifiers()))
				continue;
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes != null && parameterTypes.length > 0)
				continue;
			
			if (method.getReturnType() == void.class)
				continue;
			
			String name = method.getName();
			if (name.equals("getClass"))
				continue;
			else if (name.startsWith("get"))
				name = firstLower(name.substring(3));
			else if (name.startsWith("is"))
				name = firstLower(name.substring(2));
			else
				continue;
			
			if (hasAttribute(name))
				continue;
			
			attributes.add(new ObjectReflectionAttribute(obj, name));
		}
	}
	private boolean hasAttribute(String name)
	{
		for (Object attribute : attributes)
		{
			if (!(attribute instanceof Attribute))
				continue;
			
			Attribute attr = (Attribute) attribute;
			if (attr.getName().equals(name))
				return true;
		}
		return false;
	}
	private String firstLower(String str)
	{
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	public String getName()
	{
		return obj.getClass().getSimpleName();
	}
	
}
