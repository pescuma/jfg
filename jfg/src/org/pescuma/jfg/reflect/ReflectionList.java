package org.pescuma.jfg.reflect;

import static org.pescuma.jfg.reflect.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.model.ann.ListOf;

public class ReflectionList implements AttributeList
{
	private final String name;
	private final Class<?> elementType;
	@SuppressWarnings("unchecked")
	private final List list;
	private final ReflectionData data;
	private final Map<Object, Attribute> attributes = new HashMap<Object, Attribute>();
	
	public ReflectionList(String name, List<?> list, Class<?> elementType)
	{
		this(name, list, elementType, new ReflectionData());
	}
	
	public ReflectionList(String name, List<?> list, Class<?> elementType, ReflectionData data)
	{
		if (list == null)
			throw new IllegalArgumentException();
		if (elementType == null)
			throw new IllegalArgumentException();
		
		this.name = name;
		this.list = list;
		this.data = data;
		this.elementType = elementType;
	}
	
	public ReflectionList(String name, Field field, Method method, Object obj, ReflectionData data)
	{
		if (obj == null)
			throw new IllegalArgumentException();
		if (!(obj instanceof List<?>))
			throw new IllegalArgumentException();
		
		this.name = name;
		this.list = (List<?>) obj;
		this.data = data;
		this.elementType = findElementType(field, method);
	}
	
	private Class<?> findElementType(Field field, Method method)
	{
		Set<Class<?>> elementTypes = new HashSet<Class<?>>();
		
		if (elementTypes.isEmpty() && method != null)
			guessTypesFromAnnotation(elementTypes, method.getReturnType());
		
		if (elementTypes.isEmpty() && field != null)
			guessTypesFromAnnotation(elementTypes, field.getType());
		
		if (elementTypes.isEmpty() && method != null)
			elementTypes.addAll(getListElementTypes(method));
		
		if (elementTypes.isEmpty() && field != null)
			elementTypes.addAll(getListElementTypes(field));
		
		if (elementTypes.isEmpty())
			elementTypes.addAll(getListElementTypes(list));
		
		if (elementTypes.isEmpty())
		{
			if (field != null)
				throw new ReflectionAttributeException("Could not find list type for " + field.getName());
			else if (method != null)
				throw new ReflectionAttributeException("Could not find list type for " + method.getName());
			else
				throw new ReflectionAttributeException("Could not find list type");
		}
		else if (elementTypes.size() > 1)
		{
			if (field != null)
				throw new ReflectionAttributeException("Not supported yet: Found more than one list type for "
						+ field.getName());
			else if (method != null)
				throw new ReflectionAttributeException("Not supported yet: Found more than one list type for "
						+ method.getName());
			else
				throw new ReflectionAttributeException("Not supported yet: Found more than one list type");
		}
		
		return elementTypes.iterator().next();
	}
	
	private void guessTypesFromAnnotation(Set<Class<?>> elementTypes, Class<?> reflectionClass)
	{
		ListOf lo = reflectionClass.getAnnotation(ListOf.class);
		if (lo != null)
		{
			for (Class<?> cls : lo.value())
				elementTypes.add(cls);
		}
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public Object getElementType()
	{
		return elementType;
	}
	
	@Override
	public int size()
	{
		return list.size();
	}
	
	@Override
	public Attribute get(int index)
	{
		Object obj = list.get(index);
		
		Attribute attrib = attributes.get(obj);
		if (attrib == null)
		{
			attrib = new ReflectionListAttribute(obj);
			attributes.put(obj, attrib);
		}
		
		return attrib;
	}
	
	@Override
	public int indexOf(Attribute attrib)
	{
		for (Entry<Object, Attribute> entry : attributes.entrySet())
		{
			if (entry.getValue() == attrib)
				return list.indexOf(entry.getValue());
		}
		return -1;
	}
	
	@Override
	public boolean canWrite()
	{
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Attribute add()
	{
		Object obj = newInstance(elementType);
		if (obj == null)
			throw new ReflectionAttributeException("Could not create new object of class " + elementType.getName());
		
		if (!list.add(obj))
			return null;
		
		Attribute attrib = new ReflectionListAttribute(obj);
		attributes.put(obj, attrib);
		return attrib;
	}
	
	@Override
	public void remove(int index)
	{
		Object obj = list.remove(index);
		if (obj != null)
			attributes.remove(obj);
	}
	
	@Override
	public boolean canListen()
	{
		// TODO
		return false;
	}
	
	@Override
	public void addListener(AttributeListener listener)
	{
	}
	
	@Override
	public void removeListener(AttributeListener listener)
	{
	}
	
	private class ReflectionListAttribute implements Attribute
	{
		private Object obj;
		private final Class<?> type;
		
		public ReflectionListAttribute(Object obj)
		{
			this.obj = obj;
			this.type = obj.getClass();
		}
		
		@Override
		public String getName()
		{
			return name + "#item";
		}
		
		@Override
		public Object getType()
		{
			return type;
		}
		
		@Override
		public AttributeValueRange getValueRange()
		{
			return new AttributeValueRange() {
				@Override
				public Collection<?> getPossibleValues()
				{
					return null;
				}
				
				@Override
				public Object getMin()
				{
					return null;
				}
				
				@Override
				public Object getMax()
				{
					return null;
				}
				
				@Override
				public Comparator<?> getComparator()
				{
					return null;
				}
				
				@Override
				public boolean canBeNull()
				{
					return false;
				}
			};
		}
		
		@Override
		public boolean canWrite()
		{
			return true;
		}
		
		@Override
		public Object getValue()
		{
			return obj;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void setValue(Object value)
		{
			if (value == null)
				throw new ReflectionAttributeException("Field does not allow null");
			if (!value.getClass().isInstance(type))
				throw new ReflectionAttributeException("Invalid value type");
			
			int index = list.indexOf(obj);
			if (index < 0)
				throw new ReflectionAttributeException("This object was removed from the list");
			
			list.set(index, value);
			obj = value;
		}
		
		@Override
		public boolean canListen()
		{
			// TODO
			return false;
		}
		
		@Override
		public void addListener(AttributeListener listener)
		{
		}
		
		@Override
		public void removeListener(AttributeListener listener)
		{
		}
		
		@Override
		public AttributeGroup asGroup()
		{
			if (type.isPrimitive())
				return null;
			if (data.ignoreForAsGroup(type.getName()))
				return null;
			
			return new ReflectionGroup(getName(), obj, data);
		}
		
		@Override
		public AttributeList asList()
		{
			return null;
		}
	}
}
