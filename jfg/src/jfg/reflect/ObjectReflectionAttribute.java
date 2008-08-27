package jfg.reflect;

import static jfg.reflect.ObjectReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.AttributeListener;
import jfg.AttributeValueRange;

class ObjectReflectionAttribute implements Attribute
{
	private final AttributeGroup parent;
	private final ObjectReflectionData data;
	private final Object obj;
	private final Field field;
	private final Method setter;
	private final Method getter;
	private final Method addListener;
	private final Method removeListener;
	private final String name;
	private Class<?> type;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	public ObjectReflectionAttribute(AttributeGroup parent, Object obj, Field field, ObjectReflectionData data)
	{
		this.parent = parent;
		this.data = data;
		this.obj = obj;
		this.field = field;
		name = field.getName();
		type = field.getType();
		
		getter = getMethod(obj, data.getGetterNames(name));
		
		if (!Modifier.isFinal(field.getModifiers()))
			setter = getMethod(obj, void.class, data.getSetterNames(name), type);
		else
			setter = null;
		
		addListener = getListenerMethod(obj, data.getAddFieldListenerNames(name), data.getRemoveFieldListenerNames(name), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveFieldListenerNames(name), addListener.getParameterTypes());
		else
			removeListener = null;
	}
	
	public ObjectReflectionAttribute(AttributeGroup parent, Object obj, String name, ObjectReflectionData data)
	{
		this.parent = parent;
		this.data = data;
		this.obj = obj;
		field = null;
		this.name = name;
		
		getter = getMethod(obj, data.getGetterNames(name));
		if (getter == null || getter.getReturnType() == void.class)
			throw new IllegalArgumentException();
		
		type = getter.getReturnType();
		setter = getMethod(obj, void.class, data.getSetterNames(name), type);
		
		addListener = getListenerMethod(obj, data.getAddFieldListenerNames(name), data.getRemoveFieldListenerNames(name), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveFieldListenerNames(name), addListener.getParameterTypes());
		else
			removeListener = null;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Object getType()
	{
		return type;
	}
	
	public AttributeValueRange getValueRange()
	{
		return null;
	}
	
	public AttributeGroup asGroup()
	{
		if (type.isPrimitive())
			return null;
		if (data.ignoreForAsGroup(type.getName()))
			return null;
		
		Object value = getValue();
		if (value == null)
			return null;
		
		return new ObjectReflectionGroup(getName(), value, data);
	}
	
	public boolean canWrite()
	{
		return setter != null || (field != null && !Modifier.isFinal(field.getModifiers()));
	}
	
	public Object getValue()
	{
		if (getter != null)
		{
			return invoke(obj, getter);
		}
		else if (field != null)
		{
			return get(obj, field);
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	public void setValue(Object value)
	{
		if (!canWrite())
			throw new ObjectReflectionException("Field is ready-only");
		
		if (setter != null)
		{
			invoke(obj, setter, value);
		}
		else if (field != null)
		{
			set(obj, field, value);
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	public boolean canListen()
	{
		return (addListener != null && removeListener != null) || parent.canListen();
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
			if (addListener != null)
			{
				listener = wrapListener(addListener.getParameterTypes()[0], new AttributeListener() {
					public void onChange()
					{
						notifyChange();
					}
				}, data);
				
				invoke(obj, addListener, listener);
			}
			else
			{
				listener = new AttributeListener() {
					public void onChange()
					{
						notifyChange();
					}
				};
				parent.addListener((AttributeListener) listener);
			}
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
			if (removeListener != null)
				invoke(obj, removeListener, listener);
			else
				parent.removeListener((AttributeListener) listener);
			
			listener = null;
		}
	}
}
