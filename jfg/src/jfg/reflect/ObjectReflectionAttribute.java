package jfg.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import jfg.Attribute;
import jfg.AttributeListener;
import jfg.AttributeListenerConverter;
import jfg.AttributeValueRange;

public class ObjectReflectionAttribute implements Attribute
{
	private final ObjectReflectionData data;
	private final Object obj;
	private final Field field;
	private final Method setter;
	private final Method getter;
	private final Method addListener;
	private final Method removeListener;
	private Object listener;
	private final String name;
	private Class<?> type;
	
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	public ObjectReflectionAttribute(Object obj, Field field, ObjectReflectionData data)
	{
		this.data = data;
		this.obj = obj;
		this.field = field;
		name = field.getName();
		type = field.getType();
		
		Method m = null;
		for (String getterName : getGetterNames())
		{
			m = getMethod(getterName);
			if (m != null)
				break;
		}
		getter = m;
		
		if (!Modifier.isFinal(field.getModifiers()))
			setter = getMethod(void.class, getSetterName(), type);
		else
			setter = null;
		
		addListener = getListenerMethod(getAddListenerMethodName(), getRemoveListenerMethodName());
		if (addListener != null)
			removeListener = getMethod(getRemoveListenerMethodName(), addListener.getParameterTypes());
		else
			removeListener = null;
	}
	
	public ObjectReflectionAttribute(Object obj, String name, ObjectReflectionData data)
	{
		this.data = data;
		this.obj = obj;
		field = null;
		this.name = name;
		
		Method m = null;
		for (String getterName : getGetterNames())
		{
			m = getMethod(getterName);
			if (m != null)
				break;
		}
		if (m == null || m.getReturnType() == void.class)
			throw new IllegalArgumentException();
		
		type = m.getReturnType();
		getter = m;
		setter = getMethod(void.class, getSetterName(), type);
		
		addListener = getListenerMethod(getAddListenerMethodName(), getRemoveListenerMethodName());
		if (addListener != null)
			removeListener = getMethod(getRemoveListenerMethodName(), addListener.getParameterTypes());
		else
			removeListener = null;
	}
	
	private String[] getGetterNames()
	{
		return new String[] { "get" + firstUp(name), "is" + firstUp(name) };
	}
	
	private String getSetterName()
	{
		return "set" + firstUp(name);
	}
	
	private String getAddListenerMethodName()
	{
		return "add" + firstUp(name) + "Listener";
	}
	
	private String getRemoveListenerMethodName()
	{
		return "remove" + firstUp(name) + "Listener";
	}
	
	private Method getListenerMethod(String addMethodName, String removeMethodName)
	{
		for (Method m : obj.getClass().getMethods())
		{
			if (!addMethodName.equals(m.getName()))
				continue;
			
			Class<?>[] parameters = m.getParameterTypes();
			if (parameters.length != 1)
				continue;
			
			Class<?> interfaceClass = parameters[0];
			
			if (getMethod(removeMethodName, interfaceClass) == null)
				continue;
			
			if (data.attributeListenerConverters.get(interfaceClass) != null)
				return m;
			
			if (!interfaceClass.isInterface())
				continue;
			
			if (getListenerInterfaceMethod(interfaceClass) != null)
				return m;
		}
		
		return null;
	}
	
	private Method getListenerInterfaceMethod(Class<?> interfaceClass)
	{
		Method[] methods = interfaceClass.getMethods();
		if (methods.length == 1)
			return methods[0];
		
		Method ret = null;
		String[] namePieces = { "Change", "^change" };
		for (Method m : methods)
		{
			for (String piece : namePieces)
			{
				if (m.getName().matches(piece))
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
	
	private Method getMethod(Class<?> returnType, String methodName, Class<?>... paramTypes)
	{
		Method method = getMethod(methodName, paramTypes);
		if (method != null && method.getReturnType() == returnType)
			return method;
		else
			return null;
	}
	
	private Method getMethod(String methodName, Class<?>... paramTypes)
	{
		Class<?> cls = obj.getClass();
		try
		{
			return cls.getMethod(methodName, paramTypes);
		}
		catch (SecurityException e)
		{
		}
		catch (NoSuchMethodException e)
		{
		}
		return null;
	}
	
	private String firstUp(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean canWrite()
	{
		return setter != null || (field != null && !Modifier.isFinal(field.getModifiers()));
	}
	
	public boolean canListen()
	{
		return addListener != null && removeListener != null;
	}
	
	public Object getType()
	{
		return type;
	}
	
	public Object getValue()
	{
		if (getter != null)
		{
			return invoke(getter);
		}
		else if (field != null)
		{
			try
			{
				return field.get(obj);
			}
			catch (IllegalArgumentException e)
			{
				throw new ObjectReflectionException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new ObjectReflectionException(e);
			}
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
			invoke(setter, value);
		}
		else if (field != null)
		{
			try
			{
				field.set(obj, value);
			}
			catch (IllegalArgumentException e)
			{
				throw new ObjectReflectionException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new ObjectReflectionException(e);
			}
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	public AttributeValueRange getValueRange()
	{
		return null;
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
			});
			
			invoke(addListener, listener);
		}
		
		listeners.add(attributeListener);
	}
	
	private void notifyChange()
	{
		for (AttributeListener al : listeners)
		{
			al.onChange();
		}
	}
	
	public void removeListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ObjectReflectionException("Can't add listener");
		
		listeners.remove(attributeListener);
		
		if (listeners.size() <= 0)
		{
			invoke(removeListener, listener);
			listener = null;
		}
	}
	
	private Object invoke(Method m, Object... args)
	{
		try
		{
			return m.invoke(obj, args);
		}
		catch (IllegalArgumentException e)
		{
			throw new ObjectReflectionException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new ObjectReflectionException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new ObjectReflectionException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T wrapListener(Class<T> interfaceClass, AttributeListener attributeListener)
	{
		AttributeListenerConverter<T> converter = (AttributeListenerConverter<T>) data.attributeListenerConverters.get(interfaceClass);
		if (converter != null)
			return converter.wrapListener(attributeListener);
		
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, new CallbackInvocationHandler(
				getListenerInterfaceMethod(interfaceClass).getName(), attributeListener));
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
		
		private Object getDefaultValue(Class<?> returnType)
		{
			if (returnType == byte.class || returnType == Byte.class)
				return new Byte((byte) 0);
			if (returnType == short.class || returnType == Short.class)
				return new Short((short) 0);
			if (returnType == int.class || returnType == Integer.class)
				return new Integer(0);
			if (returnType == long.class || returnType == Long.class)
				return new Long(0);
			if (returnType == float.class || returnType == Float.class)
				return new Float(0);
			if (returnType == double.class || returnType == Double.class)
				return new Double(0);
			if (returnType == boolean.class || returnType == Boolean.class)
				return Boolean.FALSE;
			if (returnType == char.class || returnType == Character.class)
				return new Character('\0');
			if (returnType == String.class)
				return "";
			return null;
		}
		protected Integer proxyHashCode(Object proxy)
		{
			return new Integer(System.identityHashCode(proxy));
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
}
