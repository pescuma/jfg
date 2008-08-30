package jfg.reflect;

import static jfg.reflect.ObjectReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
	private final Class<?> type;
	private AttributeValueRange range;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	public ObjectReflectionAttribute(AttributeGroup parent, Object obj, Field field, ObjectReflectionData data)
	{
		this.parent = parent;
		this.data = data;
		this.obj = obj;
		
		if (Modifier.isPublic(field.getModifiers()))
			this.field = field;
		else
			this.field = null;
		
		String simpleName = field.getName();
		name = field.getDeclaringClass().getName() + "." + simpleName;
		
		type = field.getType();
		
		getter = getMethod(obj, data.getGetterNames(simpleName));
		
		if (!Modifier.isFinal(field.getModifiers()))
			setter = getMethod(obj, void.class, data.getSetterNames(simpleName), type);
		else
			setter = null;
		
		addListener = getListenerMethod(obj, data.getAddFieldListenerNames(simpleName), data.getRemoveFieldListenerNames(simpleName), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveFieldListenerNames(simpleName), addListener.getParameterTypes());
		else
			removeListener = null;
		
		setAccessible();
	}
	
	public ObjectReflectionAttribute(AttributeGroup parent, Object obj, String fullName, String simpleName, ObjectReflectionData data)
	{
		this.parent = parent;
		this.data = data;
		this.obj = obj;
		field = null;
		name = fullName;
		
		getter = getMethod(obj, data.getGetterNames(simpleName));
		if (getter == null || getter.getReturnType() == void.class)
			throw new IllegalArgumentException();
		
		type = getter.getReturnType();
		
		setter = getMethod(obj, void.class, data.getSetterNames(simpleName), type);
		
		addListener = getListenerMethod(obj, data.getAddFieldListenerNames(simpleName), data.getRemoveFieldListenerNames(simpleName), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveFieldListenerNames(simpleName), addListener.getParameterTypes());
		else
			removeListener = null;
		
		setAccessible();
	}
	
	private void setAccessible()
	{
		if (field != null)
			field.setAccessible(true);
		if (getter != null)
			getter.setAccessible(true);
		if (setter != null)
			setter.setAccessible(true);
		if (addListener != null)
			addListener.setAccessible(true);
		if (removeListener != null)
			removeListener.setAccessible(true);
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
		if (!type.isEnum())
			return null;
		
		if (range == null)
		{
			range = new AttributeValueRange() {
				
				public boolean canBeNull()
				{
					return true;
				}
				
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
				
				private Collection<Object> values;
				
				public Collection<Object> getPossibleValues()
				{
					if (values == null)
					{
						values = new ArrayList<Object>();
						Collections.addAll(values, type.getEnumConstants());
					}
					
					return values;
				}
			};
		}
		
		return range;
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
	
	@Override
	public String toString()
	{
		return "ObjectReflectionAttribute[" + name + "]";
	}
	
}
