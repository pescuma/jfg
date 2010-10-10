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

import static org.pescuma.jfg.StringUtils.*;
import static org.pescuma.jfg.reflect.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeListener;

public class ReflectionGroup implements AttributeGroup
{
	private final String name;
	private final Object obj;
	private final boolean useStatic;
	private final ReflectionData data;
	private List<Attribute> attributes;
	private Method addListener;
	private Method removeListener;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	private final MemberFilter memberFilter = new MemberFilter() {
		@Override
		public boolean accept(Member member)
		{
			if (useStatic != Modifier.isStatic(member.getModifiers()))
				return false;
			
			return data.memberFilter.accept(member);
		}
	};
	
	public ReflectionGroup(Object obj)
	{
		this(null, obj, new ReflectionData());
	}
	
	public ReflectionGroup(String name, Object obj)
	{
		this(name, obj, new ReflectionData());
	}
	
	public ReflectionGroup(Object obj, ReflectionData data)
	{
		this(null, obj, data);
	}
	
	public ReflectionGroup(String name, Object obj, ReflectionData data)
	{
		if (obj == null)
			throw new IllegalArgumentException();
		
		this.obj = obj;
		this.data = data;
		useStatic = (obj instanceof Class<?>);
		
		if (name == null)
			this.name = getObjClass().getSimpleName();
		else
			this.name = name;
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
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public Collection<Attribute> getAttributes()
	{
		if (attributes == null)
			loadAttributes();
		
		return attributes;
	}
	
	private class FieldAndName
	{
		Field field;
		Method method;
		String name;
		
		public FieldAndName(Field field)
		{
			this.field = field;
			name = field.getName();
		}
		
		public FieldAndName(Method method, String name)
		{
			this.method = method;
			this.name = name;
		}
	}
	
	private void loadAttributes()
	{
		attributes = new ArrayList<Attribute>();
		
		addListener = getListenerMethod(memberFilter, getObjClass(), data.getAddObjectListenerNames(),
				data.getRemoveObjectListenerNames(), data);
		if (addListener != null)
			removeListener = getMethod(memberFilter, getObjClass(), data.getRemoveObjectListenerNames(),
					addListener.getParameterTypes());
		
		if (addListener != null)
			addListener.setAccessible(true);
		if (removeListener != null)
			removeListener.setAccessible(true);
		
		List<FieldAndName> allCandidates = new LinkedList<FieldAndName>();
		addAttributesFrom(allCandidates, getObjClass());
		
		filterAttributes(allCandidates);
		
		for (FieldAndName fn : allCandidates)
			attributes.add(new ReflectionAttribute(this, obj, fn.name, data));
	}
	
	private void addAttributesFrom(List<FieldAndName> allCandidates, Class<?> cls)
	{
		if (cls == Object.class)
			return;
		
		addAttributesFrom(allCandidates, cls.getSuperclass());
		
		for (Field field : cls.getDeclaredFields())
			allCandidates.add(new FieldAndName(field));
		
		Pattern[] getterREs = data.getGetterREs();
		
		for (Method method : cls.getDeclaredMethods())
		{
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes != null && parameterTypes.length > 0)
				continue;
			
			if (method.getReturnType() == void.class)
				continue;
			
			String methodName = method.getName();
			if (methodName.equals("getClass"))
				continue;
			
			String attrName = null;
			for (int i = 0; i < getterREs.length; i++)
			{
				Matcher matcher = getterREs[i].matcher(methodName);
				if (!matcher.matches())
					continue;
				
				attrName = firstLower(matcher.group(1));
				break;
			}
			if (attrName == null)
				continue;
			
			FieldAndName fn = findAttribute(allCandidates, attrName);
			if (fn != null)
				fn.method = method;
			else
				allCandidates.add(new FieldAndName(method, attrName));
		}
	}
	
	private FieldAndName findAttribute(List<FieldAndName> allCandidates, String attrName)
	{
		for (FieldAndName fn : allCandidates)
		{
			if (fn.name.equals(attrName))
				return fn;
		}
		return null;
	}
	
	private void filterAttributes(List<FieldAndName> allCandidates)
	{
		for (Iterator<FieldAndName> it = allCandidates.iterator(); it.hasNext();)
		{
			FieldAndName fn = it.next();
			
			boolean accept = false;
			
			if (fn.field != null && memberFilter.accept(fn.field))
				accept = true;
			if (fn.method != null && memberFilter.accept(fn.method))
				accept = true;
			
			if (!accept)
				it.remove();
		}
	}
	
	@Override
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
	
	@Override
	public void addListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ReflectionAttributeException("Can't add listener");
		
		if (listener == null)
		{
			Class<?> interfaceClass = addListener.getParameterTypes()[0];
			
			listener = wrapListener(interfaceClass, new AttributeListener() {
				@Override
				public void onChange()
				{
					notifyChange();
				}
			}, data);
			
			invoke(getObjInstance(), addListener, listener);
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
			invoke(getObjInstance(), removeListener, listener);
			listener = null;
		}
	}
}
