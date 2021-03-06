package org.pescuma.jfg.reflect;

import static org.pescuma.jfg.reflect.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pescuma.jfg.AbstractAttributeValueRange;
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
	@SuppressWarnings("rawtypes")
	private final List list;
	private final ReflectionData data;
	private final List<ReflectionListAttribute> attributes = new ArrayList<ReflectionListAttribute>();
	private boolean canWrite;
	
	public ReflectionList(String name, List<?> list, Class<?> elementType)
	{
		this(name, list, elementType, true, new ReflectionData());
	}
	
	public ReflectionList(String name, List<?> list, Class<?> elementType, boolean canChangeList, ReflectionData data)
	{
		if (list == null)
			throw new IllegalArgumentException();
		if (elementType == null)
			throw new IllegalArgumentException();
		
		this.name = name;
		this.list = list;
		this.data = data;
		this.elementType = elementType;
		this.canWrite = canChangeList;
	}
	
	public ReflectionList(String name, Field field, Method getter, Method setter, Object obj, ReflectionData data)
	{
		if (obj == null)
			throw new IllegalArgumentException();
		if (!(obj instanceof List<?>))
			throw new IllegalArgumentException();
		
		this.name = name;
		this.list = (List<?>) obj;
		this.data = data;
		this.elementType = findElementType(field, getter);
		this.canWrite = !ReflectionUtils.isReadOnly(field, getter, setter);
	}
	
	private void updateAtributesList()
	{
		int listSise = list.size();
		
		int attributesSize = attributes.size();
		if (attributesSize > listSise)
		{
			for (int i = listSise; i < attributesSize; i++)
				attributes.remove(listSise);
		}
		else
		{
			for (int i = attributesSize; i < listSise; i++)
				attributes.add(null);
		}
		
		for (int i = 0; i < listSise; i++)
		{
			ReflectionListAttribute attrib = attributes.get(i);
			if (attrib != null && attrib.obj != list.get(i))
			{
				attrib.connected = false;
				attributes.set(i, null);
			}
		}
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
		updateAtributesList();
		
		ReflectionListAttribute attrib = attributes.get(index);
		if (attrib == null)
		{
			Object obj = list.get(index);
			attrib = new ReflectionListAttribute(obj, true, obj == null ? elementType : obj.getClass(), false);
			attributes.set(index, attrib);
		}
		return attrib;
	}
	
	@Override
	public int indexOf(Attribute attrib)
	{
		updateAtributesList();
		
		return attributes.indexOf(attrib);
	}
	
	@Override
	public boolean canWrite()
	{
		return canWrite;
	}
	
	@Override
	public Attribute createNewElement()
	{
		return new ReflectionListAttribute(null, false, elementType, true);
	}
	
	private Object createNewElementInstance()
	{
		Object obj = newInstance(elementType);
		if (obj == null)
			throw new ReflectionAttributeException("Could not create new object of class " + elementType.getName());
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(int index, Attribute item)
	{
		if (!canWrite())
			throw new ReflectionAttributeException("Field is ready-only");
		if (item == null)
			throw new IllegalArgumentException("item can't be null");
		if (!(item instanceof ReflectionListAttribute))
			throw new IllegalArgumentException("Wrong item type");
		
		updateAtributesList();
		
		ReflectionListAttribute rla = (ReflectionListAttribute) item;
		Object obj = rla.getValue();
		
		attributes.add(index, rla);
		list.add(index, obj);
		rla.connected = true;
	}
	
	@Override
	public void remove(int index)
	{
		if (!canWrite())
			throw new ReflectionAttributeException("Field is ready-only");
		
		updateAtributesList();
		
		list.remove(index);
		ReflectionListAttribute item = attributes.remove(index);
		item.connected = false;
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
		private boolean buildOnGet;
		private boolean connected;
		
		public ReflectionListAttribute(Object obj, boolean connected, Class<?> type, boolean buildOnGet)
		{
			this.obj = obj;
			this.connected = connected;
			this.type = type;
			this.buildOnGet = buildOnGet;
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
			return new AbstractAttributeValueRange() {
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
			buildObjIfNeeded();
			return obj;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void setValue(Object value)
		{
			if (!type.isInstance(value))
				throw new ReflectionAttributeException("Invalid value type");
			
			if (connected)
			{
				int index = attributes.indexOf(this);
				if (index < 0)
					throw new ReflectionAttributeException("This object was removed from the list");
				
				list.set(index, value);
			}
			
			obj = value;
			
			buildOnGet = false;
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
			
			buildObjIfNeeded();
			if (obj == null)
				return null;
			
			return new ReflectionGroup(getName(), obj, data);
		}
		
		private void buildObjIfNeeded()
		{
			if (!buildOnGet)
				return;
			
			if (obj == null)
				obj = createNewElementInstance();
			
			buildOnGet = false;
		}
		
		@Override
		public AttributeList asList()
		{
			return null;
		}
	}
	
}
