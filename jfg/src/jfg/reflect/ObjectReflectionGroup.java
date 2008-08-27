package jfg.reflect;

import static jfg.reflect.ObjectReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.AttributeListener;

public class ObjectReflectionGroup implements AttributeGroup
{
	private final String name;
	private final Object obj;
	private final ObjectReflectionData data;
	private ArrayList<Object> attributes;
	private Method addListener;
	private Method removeListener;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	public ObjectReflectionGroup(Object obj)
	{
		this(null, obj, new ObjectReflectionData());
	}
	
	public ObjectReflectionGroup(String name, Object obj)
	{
		this(name, obj, new ObjectReflectionData());
	}
	
	public ObjectReflectionGroup(Object obj, ObjectReflectionData data)
	{
		this(null, obj, data);
	}
	
	public ObjectReflectionGroup(String name, Object obj, ObjectReflectionData data)
	{
		if (name == null)
			this.name = obj.getClass().getSimpleName();
		else
			this.name = name;
		this.obj = obj;
		this.data = data;
	}
	
	public String getName()
	{
		return name;
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
		
		addListener = getListenerMethod(obj, data.getAddObjectListenerNames(), data.getRemoveObjectListenerNames(), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveObjectListenerNames(), addListener.getParameterTypes());
		
		Class<?> cls = obj.getClass();
		
		for (Field field : cls.getFields())
		{
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			attributes.add(new ObjectReflectionAttribute(this, obj, field, data));
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
			
			attributes.add(new ObjectReflectionAttribute(this, obj, name, data));
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
	
	public boolean canListen()
	{
		if (attributes == null)
			loadAttributes();
		
		return addListener != null && removeListener != null;
	}
	
	private void notifyChange()
	{
		for (AttributeListener al : listeners)
		{
			al.onChange();
		}
	}
	
	public void addListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ObjectReflectionException("Can't add listener");
		
		if (listener == null)
		{
			Class<?> interfaceClass = addListener.getParameterTypes()[0];
			
			listener = wrapListener(interfaceClass, new AttributeListener() {
				public void onChange()
				{
					notifyChange();
				}
			}, data);
			
			invoke(obj, addListener, listener);
		}
		
		listeners.add(attributeListener);
	}
	
	public void removeListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ObjectReflectionException("Can't add listener");
		
		listeners.remove(attributeListener);
		
		if (listeners.size() <= 0)
		{
			invoke(obj, removeListener, listener);
			listener = null;
		}
	}
}
