/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.reflect;

import static org.pescuma.jfg.reflect.ReflectionUtils.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.AttributeValueRange;
import org.pescuma.jfg.gui.WidgetValidator;
import org.pescuma.jfg.model.ann.CompareWith;
import org.pescuma.jfg.model.ann.NotNull;
import org.pescuma.jfg.model.ann.Range;
import org.pescuma.jfg.model.ann.ValidateWith;

public class ReflectionAttribute implements Attribute
{
	private final AttributeGroup parent;
	private final ReflectionData data;
	private final Object obj;
	private final boolean useStatic;
	private final Field field;
	private final Method setter;
	private final Method getter;
	private Field nonFilteredField;
	private Method nonFilteredGetter;
	private Method nonFilteredSetter;
	private final Method addListener;
	private final Method removeListener;
	private final String name;
	private final Class<?> type;
	private final boolean canWrite;
	private final AttributeValueRange attributeValueRange;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	public ReflectionAttribute(Object obj, String fieldName)
	{
		this(obj, fieldName, new ReflectionData());
	}
	
	public ReflectionAttribute(Object obj, String fieldName, ReflectionData data)
	{
		this(new ReflectionGroup(obj, data), obj, fieldName, data);
	}
	
	ReflectionAttribute(AttributeGroup parent, Object obj, String simpleName, final ReflectionData data)
	{
		this.parent = parent;
		this.obj = obj;
		this.data = data;
		useStatic = (obj instanceof Class<?>);
		
		final MemberFilter simpleMemberField = new MemberFilter() {
			@Override
			public boolean accept(Member member)
			{
				return useStatic == Modifier.isStatic(member.getModifiers());
			}
		};
		final MemberFilter memberFilter = new MemberFilter() {
			@Override
			public boolean accept(Member member)
			{
				if (useStatic != Modifier.isStatic(member.getModifiers()))
					return false;
				
				return data.memberFilter.accept(member);
			}
		};
		
		nonFilteredField = ReflectionUtils.getField(simpleMemberField, getObjClass(), simpleName);
		field = checkMember(nonFilteredField);
		
		nonFilteredGetter = getMethod(simpleMemberField, getObjClass(), data.getGetterNames(simpleName));
		getter = checkMember(nonFilteredGetter);
		
		assertValid();
		
		type = (getter != null ? getter.getReturnType() : field.getType());
		
		name = createFullName(simpleName);
		
		nonFilteredSetter = getMethod(simpleMemberField, getObjClass(), void.class, data.getSetterNames(simpleName),
				type);
		setter = checkMember(nonFilteredSetter);
		
		addListener = getListenerMethod(memberFilter, getObjClass(), data.getAddFieldListenerNames(simpleName),
				data.getRemoveFieldListenerNames(simpleName), data);
		if (addListener != null)
			removeListener = getMethod(memberFilter, getObjClass(), data.getRemoveFieldListenerNames(simpleName),
					addListener.getParameterTypes());
		else
			removeListener = null;
		
		attributeValueRange = getRangeData(nonFilteredField, nonFilteredGetter, nonFilteredSetter);
		
		canWrite = getCanWrite(nonFilteredField, nonFilteredGetter, nonFilteredSetter);
		
		setAccessible();
	}
	
	private <T extends Member> T checkMember(T member)
	{
		if (member != null && data.memberFilter.accept(member))
			return member;
		else
			return null;
	}
	
	private boolean getCanWrite(Field forcedField, Method forcedGetter, Method forcedSetter)
	{
		if (ReflectionUtils.isReadOnly(forcedField, forcedGetter, forcedSetter))
			return false;
		else if (setter != null && data.memberFilter.accept(setter))
			return true;
		else if (field != null && data.memberFilter.accept(field))
			return !Modifier.isFinal(field.getModifiers());
		else
			return false;
	}
	
	public Field getField()
	{
		return field;
	}
	
	public Method getGetter()
	{
		return getter;
	}
	
	private String createFullName(String simpleName)
	{
		if (getter != null)
			return getter.getDeclaringClass().getName() + "." + simpleName;
		if (field != null)
			return field.getDeclaringClass().getName() + "." + simpleName;
		throw new IllegalStateException();
	}
	
	private Class<?> getObjClass()
	{
		if (useStatic)
			return (Class<?>) obj;
		else
			return obj.getClass();
	}
	
	private Object getObjInstance()
	{
		if (useStatic)
			return null;
		else
			return obj;
	}
	
	private static class RangeData
	{
		long min = Long.MIN_VALUE;
		double minf = Double.NEGATIVE_INFINITY;
		long max = Long.MAX_VALUE;
		double maxf = Double.POSITIVE_INFINITY;
		boolean canBeNull = true;
		Class<? extends Comparator<?>> comparator = null;
		Set<Class<WidgetValidator>> validators = new LinkedHashSet<Class<WidgetValidator>>();
		
		void addFrom(AnnotatedElement element)
		{
			if (element == null)
				return;
			
			if (element.getAnnotation(NotNull.class) != null)
				canBeNull = false;
			
			Range r = element.getAnnotation(Range.class);
			if (r != null)
			{
				min = Math.max(min, r.min());
				minf = Math.max(minf, r.minf());
				max = Math.min(max, r.max());
				maxf = Math.min(maxf, r.maxf());
			}
			
			CompareWith comp = element.getAnnotation(CompareWith.class);
			if (comp != null && comparator == null)
				comparator = comp.value();
			
			ValidateWith validators = element.getAnnotation(ValidateWith.class);
			if (validators != null)
			{
				for (Class<WidgetValidator> vs : validators.value())
					this.validators.add(vs);
			}
		}
		
		WidgetValidator[] instantiateValidators()
		{
			if (validators.size() < 1)
				return null;
			
			List<WidgetValidator> vs = new ArrayList<WidgetValidator>();
			for (Class<WidgetValidator> validatorClass : validators)
			{
				WidgetValidator validator = ReflectionUtils.newInstance(validatorClass);
				
				if (validator == null)
				{
					System.err.println("[JFG] Failed to instanciate " + validatorClass.getName());
					continue;
				}
				
				vs.add(validator);
			}
			
			if (vs.size() < 1)
				return null;
			
			return vs.toArray(new WidgetValidator[vs.size()]);
		}
	}
	
	private AttributeValueRange getRangeData(Field forcedField, Method forcedGetter, Method forcedSetter)
	{
		final RangeData range = new RangeData();
		
		if (type.isPrimitive())
			range.canBeNull = false;
		
		range.addFrom(forcedField);
		range.addFrom(forcedGetter);
		range.addFrom(forcedSetter);
		
		boolean hasNotNull = !range.canBeNull;
		boolean hasMin = (range.min > Long.MIN_VALUE || !Double.isInfinite(range.minf));
		boolean hasMax = (range.max < Long.MAX_VALUE || !Double.isInfinite(range.maxf));
		boolean hasValues = type.isEnum();
		boolean hasComparator = (range.comparator != null);
		if (!hasNotNull && !hasMin && !hasMax && !hasValues && !hasComparator)
			return null;
		
		final Object min;
		if (range.min > Long.MIN_VALUE)
			min = ReflectionUtils.valueOf(Long.valueOf(range.min), type);
		else if (!Double.isInfinite(range.minf))
			min = ReflectionUtils.valueOf(Double.valueOf(range.minf), type);
		else
			min = null;
		
		final Object max;
		if (range.max < Long.MAX_VALUE)
			max = ReflectionUtils.valueOf(Long.valueOf(range.max), type);
		else if (!Double.isInfinite(range.maxf))
			max = ReflectionUtils.valueOf(Double.valueOf(range.maxf), type);
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
		
		final WidgetValidator[] validators = range.instantiateValidators();
		
		return new AttributeValueRange() {
			
			@Override
			public boolean canBeNull()
			{
				return range.canBeNull;
			}
			
			@Override
			public Comparator<?> getComparator()
			{
				if (range.comparator != null)
					return newInstance(range.comparator);
				
				return null;
			}
			
			@Override
			public Object getMax()
			{
				return max;
			}
			
			@Override
			public Object getMin()
			{
				return min;
			}
			
			@Override
			public Collection<Object> getPossibleValues()
			{
				return values;
			}
			
			@Override
			public WidgetValidator[] getValidators()
			{
				return validators;
			}
		};
	}
	
	private void assertValid()
	{
		if (field == null && (getter == null || getter.getReturnType() == void.class))
			throw new IllegalArgumentException();
		if (field != null && getter != null && field.getType() != getter.getReturnType())
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
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public Object getType()
	{
		return type;
	}
	
	@Override
	public AttributeValueRange getValueRange()
	{
		return attributeValueRange;
	}
	
	@Override
	public AttributeGroup asGroup()
	{
		if (type.isPrimitive())
			return null;
		if (data.ignoreForAsGroup(type.getName()))
			return null;
		
		// TODO Support object creation
		Object value = getValue();
		if (value == null)
			return null;
		
		return new ReflectionGroup(getName(), value, data);
	}
	
	@Override
	public AttributeList asList()
	{
		if (!List.class.isAssignableFrom(type))
			return null;
		
		// TODO Support object creation
		Object value = getValue();
		if (value == null)
			return null;
		
		return new ReflectionList(getName(), nonFilteredField, nonFilteredGetter, nonFilteredSetter, value, data);
	}
	
	@Override
	public boolean canWrite()
	{
		return canWrite;
	}
	
	@Override
	public Object getValue()
	{
		if (getter != null)
		{
			return invoke(getObjInstance(), getter);
		}
		else if (field != null)
		{
			return get(getObjInstance(), field, data.specialFieldHandlers);
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void setValue(Object value)
	{
		if (!canWrite())
			throw new ReflectionAttributeException("Field is ready-only");
		if (attributeValueRange != null && !attributeValueRange.canBeNull() && value == null)
			throw new ReflectionAttributeException("Field does not allow null");
		
		if (setter != null)
		{
			invoke(getObjInstance(), setter, value);
		}
		else if (field != null)
		{
			set(getObjInstance(), field, value, data.specialFieldHandlers);
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	@Override
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
	
	@Override
	public void addListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ReflectionAttributeException("Can't add listener");
		
		if (listener == null)
		{
			if (addListener != null)
			{
				listener = wrapListener(addListener.getParameterTypes()[0], new AttributeListener() {
					@Override
					public void onChange()
					{
						notifyChange();
					}
				}, data);
				
				invoke(getObjInstance(), addListener, listener);
			}
			else if (parent != null)
			{
				listener = new AttributeListener() {
					Object oldValue = getValue();
					
					@Override
					public void onChange()
					{
						Object newValue = getValue();
						if ((oldValue == newValue)
								|| (oldValue != null && newValue != null && oldValue.equals(newValue)))
							return;
						oldValue = newValue;
						
						notifyChange();
					}
				};
				
				parent.addListener((AttributeListener) listener);
			}
		}
		
		listeners.add(attributeListener);
	}
	
	@Override
	public void removeListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ReflectionAttributeException("Can't add listener");
		
		listeners.remove(attributeListener);
		
		if (listeners.size() <= 0)
		{
			if (removeListener != null)
				invoke(getObjInstance(), removeListener, listener);
			else
				parent.removeListener((AttributeListener) listener);
			
			listener = null;
		}
	}
	
	@Override
	public String toString()
	{
		return "ObjectReflectionAttribute[" + name + "]@" + Integer.toHexString(hashCode());
	}
	
}
