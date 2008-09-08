package jfg.reflect;

import static jfg.reflect.ObjectReflectionUtils.*;

import java.lang.reflect.AnnotatedElement;
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
import jfg.model.ann.CompareWith;
import jfg.model.ann.NotNull;
import jfg.model.ann.Range;

public class ObjectReflectionAttribute implements Attribute
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
	private final AttributeValueRange attributeValueRange;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	public ObjectReflectionAttribute(Object obj, Field field)
	{
		this(obj, field, new ObjectReflectionData());
	}
	
	public ObjectReflectionAttribute(Object obj, Field field, ObjectReflectionData data)
	{
		this(new ObjectReflectionGroup(obj, data), obj, field, data.clone());
	}
	
	public ObjectReflectionAttribute(Object obj, String fieldName)
	{
		this(obj, fieldName, new ObjectReflectionData());
	}
	
	public ObjectReflectionAttribute(Object obj, String fieldName, ObjectReflectionData data)
	{
		this(new ObjectReflectionGroup(obj, data), obj, obj.getClass().getName() + "." + fieldName, fieldName, data.clone());
	}
	
	/** Used by ObjectReflectionGroup */
	ObjectReflectionAttribute(AttributeGroup parent, Object obj, Field field, ObjectReflectionData data)
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
		
		assertValid();
		
		if (!Modifier.isFinal(field.getModifiers()))
			setter = getMethod(obj, void.class, data.getSetterNames(simpleName), type);
		else
			setter = null;
		
		addListener = getListenerMethod(obj, data.getAddFieldListenerNames(simpleName), data.getRemoveFieldListenerNames(simpleName), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveFieldListenerNames(simpleName), addListener.getParameterTypes());
		else
			removeListener = null;
		
		attributeValueRange = getRangeData(field);
		
		setAccessible();
	}
	
	/** Used by ObjectReflectionGroup */
	ObjectReflectionAttribute(AttributeGroup parent, Object obj, String fullName, String simpleName, ObjectReflectionData data)
	{
		Field aField = getField(obj, simpleName);
		
		this.parent = parent;
		this.data = data;
		this.obj = obj;
		name = fullName;
		
		if (aField != null && Modifier.isPublic(aField.getModifiers()))
			field = aField;
		else
			field = null;
		
		getter = getMethod(obj, data.getGetterNames(simpleName));
		
		assertValid();
		
		type = getter.getReturnType();
		
		setter = getMethod(obj, void.class, data.getSetterNames(simpleName), type);
		
		addListener = getListenerMethod(obj, data.getAddFieldListenerNames(simpleName), data.getRemoveFieldListenerNames(simpleName), data);
		if (addListener != null)
			removeListener = getMethod(obj, data.getRemoveFieldListenerNames(simpleName), addListener.getParameterTypes());
		else
			removeListener = null;
		
		attributeValueRange = getRangeData(aField);
		
		setAccessible();
	}
	
	private static class RangeData
	{
		long min = Long.MIN_VALUE;
		double minf = Double.NEGATIVE_INFINITY;
		long max = Long.MAX_VALUE;
		double maxf = Double.POSITIVE_INFINITY;
		boolean canBeNull = true;
		Class<? extends Comparator<?>> comparator = null;
	}
	
	private void addRangeFrom(AnnotatedElement element, RangeData range)
	{
		if (element == null)
			return;
		
		if (element.getAnnotation(NotNull.class) != null)
			range.canBeNull = false;
		
		Range r = element.getAnnotation(Range.class);
		if (r != null)
		{
			range.min = Math.max(range.min, r.min());
			range.minf = Math.max(range.minf, r.minf());
			range.max = Math.min(range.max, r.max());
			range.maxf = Math.min(range.maxf, r.maxf());
		}
		
		CompareWith comp = element.getAnnotation(CompareWith.class);
		if (comp != null && range.comparator == null)
			range.comparator = comp.value();
	}
	
	private AttributeValueRange getRangeData(Field aField)
	{
		final RangeData range = new RangeData();
		
		addRangeFrom(aField, range);
		addRangeFrom(getter, range);
		addRangeFrom(setter, range);
		
		boolean hasNotNull = !range.canBeNull;
		boolean hasMin = (range.min > Long.MIN_VALUE || !Double.isInfinite(range.minf));
		boolean hasMax = (range.max > Long.MAX_VALUE || !Double.isInfinite(range.maxf));
		boolean hasValues = type.isEnum();
		boolean hasComparator = (range.comparator != null);
		if (!hasNotNull && !hasMin && !hasMax && !hasValues && !hasComparator)
			return null;
		
		final Object min;
		if (range.min > Long.MIN_VALUE)
			min = ObjectReflectionUtils.valueOf(Long.valueOf(range.min), type);
		else if (!Double.isInfinite(range.minf))
			min = ObjectReflectionUtils.valueOf(Double.valueOf(range.minf), type);
		else
			min = null;
		
		final Object max;
		if (range.max < Long.MAX_VALUE)
			max = ObjectReflectionUtils.valueOf(Long.valueOf(range.max), type);
		else if (!Double.isInfinite(range.maxf))
			max = ObjectReflectionUtils.valueOf(Double.valueOf(range.maxf), type);
		else
			max = null;
		
		final Collection<Object> values;
		if (hasValues)
		{
			values = new ArrayList<Object>();
			Collections.addAll(values, type.getEnumConstants());
		}
		else
			values = null;
		
		return new AttributeValueRange() {
			
			public boolean canBeNull()
			{
				return range.canBeNull;
			}
			
			public Comparator<?> getComparator()
			{
				if (range.comparator != null)
				{
					return newInstance(range.comparator);
				}
				return null;
			}
			
			public Object getMax()
			{
				return max;
			}
			
			public Object getMin()
			{
				return min;
			}
			
			public Collection<Object> getPossibleValues()
			{
				return values;
			}
		};
	}
	
	private void assertValid()
	{
		if (field == null && (getter == null || getter.getReturnType() == void.class))
			throw new IllegalArgumentException();
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
		return attributeValueRange;
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
		return (addListener != null && removeListener != null) || (parent != null && parent.canListen());
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
			else if (parent != null)
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
