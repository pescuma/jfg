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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.AttributeListenerConverter;
import org.pescuma.jfg.SpecialFieldHandler;
import org.pescuma.jfg.model.ann.ReadOnly;

class ReflectionUtils
{
	public static Method getMethod(MemberFilter memberFilter, Class<?> cls, Class<?> returnType, String[] methodNames,
			Class<?>... paramTypes)
	{
		for (String methodName : methodNames)
		{
			Method method = getMethod(memberFilter, cls, returnType, methodName, paramTypes);
			if (method != null)
				return method;
		}
		return null;
	}
	
	public static Method getMethod(MemberFilter memberFilter, Class<?> cls, Class<?> returnType, String methodName,
			Class<?>... paramTypes)
	{
		Method method = getMethod(memberFilter, cls, methodName, paramTypes);
		if (method != null && method.getReturnType() == returnType)
			return method;
		else
			return null;
	}
	
	public static Method getMethod(MemberFilter memberFilter, Class<?> cls, String[] methodNames,
			Class<?>... paramTypes)
	{
		for (String methodName : methodNames)
		{
			Method method = getMethod(memberFilter, cls, methodName, paramTypes);
			if (method != null)
				return method;
		}
		return null;
	}
	
	public static Method getMethod(MemberFilter memberFilter, Class<?> cls, String methodName, Class<?>... paramTypes)
	{
		if (cls != Object.class)
		{
			Method ret = getMethod(memberFilter, cls.getSuperclass(), methodName, paramTypes);
			if (ret != null)
				return ret;
		}
		
		try
		{
			Method method = cls.getDeclaredMethod(methodName, paramTypes);
			if (memberFilter.accept(method))
				return method;
		}
		catch (SecurityException e)
		{
		}
		catch (NoSuchMethodException e)
		{
		}
		
		return null;
	}
	
	public static Method getListenerMethod(MemberFilter memberFilter, Class<?> cls, String[] addMethodNames,
			String[] removeMethodNames, ReflectionData data)
	{
		for (Method m : cls.getMethods())
		{
			if (!memberFilter.accept(m))
				continue;
			
			if (!contains(addMethodNames, m.getName()))
				continue;
			
			Class<?>[] parameters = m.getParameterTypes();
			if (parameters.length != 1)
				continue;
			
			Class<?> interfaceClass = parameters[0];
			
			if (getMethod(memberFilter, cls, removeMethodNames, interfaceClass) == null)
				continue;
			
			if (data.attributeListenerConverters.get(interfaceClass) != null)
				return m;
			
			if (!interfaceClass.isInterface())
				continue;
			
			if (getListenerInterfaceMethod(interfaceClass, data) != null)
				return m;
		}
		
		return null;
	}
	
	private static boolean contains(String[] options, String test)
	{
		for (String opt : options)
		{
			if (opt.equals(test))
				return true;
		}
		return false;
	}
	
	public static Method getListenerInterfaceMethod(Class<?> interfaceClass, ReflectionData data)
	{
		Method[] methods = interfaceClass.getMethods();
		if (methods.length == 1)
			return methods[0];
		
		Method ret = null;
		for (Method m : methods)
		{
			for (String re : data.getListenerInterfaceMethodREs())
			{
				if (m.getName().matches(re))
				{
					if (ret == null)
						ret = m;
					else
						return null; // Can only be one
				}
			}
		}
		if (ret != null)
			return ret;
		
		for (Method m : methods)
		{
			Class<?>[] params = m.getParameterTypes();
			if (params == null || params.length == 0)
			{
				if (ret == null)
					ret = m;
				else
					return null; // Can only be one
			}
		}
		
		return ret;
	}
	
	public static Field getField(MemberFilter memberFilter, Class<?> cls, String name)
	{
		try
		{
			Field field = cls.getDeclaredField(name);
			if (memberFilter.accept(field))
				return field;
		}
		catch (SecurityException e)
		{
		}
		catch (NoSuchFieldException e)
		{
		}
		
		if (cls != Object.class)
			return getField(memberFilter, cls.getSuperclass(), name);
		
		return null;
	}
	
	public static Object invoke(Object obj, Method m, Object... args)
	{
		try
		{
			return m.invoke(obj, args);
		}
		catch (IllegalArgumentException e)
		{
			throw new ReflectionAttributeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new ReflectionAttributeException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new ReflectionAttributeException(e);
		}
	}
	
	public static void set(Object obj, Field field, Object value, List<SpecialFieldHandler> specialFieldHandlers)
	{
		try
		{
			for (SpecialFieldHandler sfh : specialFieldHandlers)
			{
				if (sfh.handles(field))
				{
					sfh.setter(field, obj, value);
					return;
				}
			}
			field.set(obj, value);
		}
		catch (IllegalArgumentException e)
		{
			throw new ReflectionAttributeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new ReflectionAttributeException(e);
		}
	}
	
	public static Object get(Object obj, Field field, List<SpecialFieldHandler> specialFieldHandlers)
	{
		try
		{
			for (SpecialFieldHandler sfh : specialFieldHandlers)
			{
				if (sfh.handles(field))
					return sfh.getter(field, obj);
			}
			return field.get(obj);
		}
		catch (IllegalArgumentException e)
		{
			throw new ReflectionAttributeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new ReflectionAttributeException(e);
		}
	}
	
	public static Object getDefaultValue(Class<?> returnType)
	{
		if (returnType == byte.class || returnType == Byte.class)
			return Byte.valueOf((byte) 0);
		if (returnType == short.class || returnType == Short.class)
			return Short.valueOf((short) 0);
		if (returnType == int.class || returnType == Integer.class)
			return Integer.valueOf(0);
		if (returnType == long.class || returnType == Long.class)
			return Long.valueOf(0);
		if (returnType == float.class || returnType == Float.class)
			return Float.valueOf(0);
		if (returnType == double.class || returnType == Double.class)
			return Double.valueOf(0);
		if (returnType == boolean.class || returnType == Boolean.class)
			return Boolean.FALSE;
		if (returnType == char.class || returnType == Character.class)
			return Character.valueOf('\0');
		if (returnType == String.class)
			return "";
		return null;
	}
	
	public static Object valueOf(Number value, Object type)
	{
		if (value == null)
			return null;
		
		if (type == byte.class || type == Byte.class)
			return Byte.valueOf(value.byteValue());
		else if (type == short.class || type == Short.class)
			return Short.valueOf(value.shortValue());
		else if (type == int.class || type == Integer.class)
			return Integer.valueOf(value.intValue());
		else if (type == long.class || type == Long.class)
			return Long.valueOf(value.longValue());
		else if (type == float.class || type == Float.class)
			return Float.valueOf(value.floatValue());
		else if (type == double.class || type == Double.class)
			return Double.valueOf(value.doubleValue());
		else
			throw new IllegalArgumentException();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T wrapListener(Class<T> interfaceClass, AttributeListener attributeListener, ReflectionData data)
	{
		AttributeListenerConverter<T> converter = (AttributeListenerConverter<T>) data.attributeListenerConverters.get(interfaceClass);
		if (converter != null)
			return converter.wrapListener(attributeListener);
		
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass },
				new CallbackInvocationHandler(getListenerInterfaceMethod(interfaceClass, data).getName(),
						attributeListener));
	}
	
	private static class CallbackInvocationHandler implements InvocationHandler
	{
		// preloaded Method objects for the methods in java.lang.Object
		private static Method hashCodeMethod;
		private static Method equalsMethod;
		private static Method toStringMethod;
		static
		{
			try
			{
				hashCodeMethod = Object.class.getMethod("hashCode");
				equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
				toStringMethod = Object.class.getMethod("toString");
			}
			catch (NoSuchMethodException e)
			{
				throw new NoSuchMethodError(e.getMessage());
			}
		}
		
		private final AttributeListener listener;
		private final String methodName;
		
		public CallbackInvocationHandler(String methodName, AttributeListener listener)
		{
			this.listener = listener;
			this.methodName = methodName;
		}
		
		public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
		{
			Class<?> declaringClass = m.getDeclaringClass();
			
			if (declaringClass == Object.class)
			{
				if (m.equals(hashCodeMethod))
				{
					return proxyHashCode(proxy);
				}
				else if (m.equals(equalsMethod))
				{
					return proxyEquals(proxy, args[0]);
				}
				else if (m.equals(toStringMethod))
				{
					return proxyToString(proxy);
				}
				else
				{
					throw new InternalError("unexpected Object method dispatched: " + m);
				}
			}
			else
			{
				if (methodName == null || methodName.equals(m.getName()))
					listener.onChange();
				
				return getDefaultValue(m.getReturnType());
			}
		}
		
		protected Integer proxyHashCode(Object proxy)
		{
			return Integer.valueOf(System.identityHashCode(proxy));
		}
		
		protected Boolean proxyEquals(Object proxy, Object other)
		{
			return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
		}
		
		protected String proxyToString(Object proxy)
		{
			return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
		}
	}
	
	public static <T> T newInstance(Class<T> cls)
	{
		try
		{
			Constructor<T> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (InvocationTargetException e)
		{
		}
		catch (SecurityException e)
		{
		}
		catch (NoSuchMethodException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (InstantiationException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		return null;
	}
	
	public static Set<Class<?>> getListElementTypes(Field field)
	{
		return getListElementTypes(field.getGenericType());
	}
	
	public static Set<Class<?>> getListElementTypes(Method method)
	{
		return getListElementTypes(method.getGenericReturnType());
	}
	
	private static Set<Class<?>> getListElementTypes(Type reflectionType)
	{
		Set<Class<?>> ret = new HashSet<Class<?>>();
		
		if (!(reflectionType instanceof ParameterizedType))
			return ret;
		
		ParameterizedType pt = (ParameterizedType) reflectionType;
		if (pt.getActualTypeArguments().length != 1)
			return ret;
		
		Type argType = pt.getActualTypeArguments()[0];
		if (argType instanceof WildcardType)
		{
			WildcardType wt = (WildcardType) argType;
			for (Type type : wt.getUpperBounds())
				addListElementType(ret, type);
		}
		else
		{
			addListElementType(ret, argType);
		}
		
		return ret;
	}
	
	public static Set<Class<?>> getListElementTypes(List<?> list)
	{
		Set<Class<?>> ret = new HashSet<Class<?>>();
		
		Type superclassType = list.getClass().getGenericSuperclass();
		if (!(superclassType instanceof ParameterizedType))
			return ret;
		
		ParameterizedType pt = (ParameterizedType) superclassType;
		if (pt.getActualTypeArguments().length != 1)
			return ret;
		
		Type argType = pt.getActualTypeArguments()[0];
		addListElementType(ret, argType);
		
		return ret;
	}
	
	private static void addListElementType(Set<Class<?>> elementTypes, Type type)
	{
		if (type instanceof Class<?>)
		{
			Class<?> cls = (Class<?>) type;
			if (cls != Object.class)
				elementTypes.add(cls);
		}
	}
	
	public static boolean isReadOnly(AccessibleObject... objs)
	{
		for (AccessibleObject obj : objs)
		{
			if (obj != null && obj.isAnnotationPresent(ReadOnly.class))
				return true;
		}
		return false;
	}
}
