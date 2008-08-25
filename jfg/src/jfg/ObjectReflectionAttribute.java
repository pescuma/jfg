package jfg;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ObjectReflectionAttribute implements Attribute
{
	private final Object obj;
	private final Field field;
	private final Method setter;
	private final Method getter;
	private final String name;
	private Class<?> type;
	
	public ObjectReflectionAttribute(Object obj, Field field)
	{
		this.obj = obj;
		this.field = field;
		name = field.getName();
		type = field.getType();
		
		if (!Modifier.isFinal(field.getModifiers()))
			setter = getMethod(void.class, "set" + firstUp(name), type);
		else
			setter = null;
		
		Method m = getMethod(type, "get" + firstUp(name));
		if (m == null)
			m = getMethod(type, "is" + firstUp(name));
		getter = m;
	}
	
	public ObjectReflectionAttribute(Object obj, String name)
	{
		this.obj = obj;
		field = null;
		this.name = name;
		
		Method m = getMethod("get" + firstUp(name));
		if (m == null)
			m = getMethod("is" + firstUp(name));
		if (m == null || m.getReturnType() == void.class)
			throw new IllegalArgumentException();
		
		type = m.getReturnType();
		getter = m;
		setter = getMethod(void.class, "set" + firstUp(name), type);
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
		return false;
	}
	
	public Object getType()
	{
		return type;
	}
	
	public Object getValue()
	{
		if (getter != null)
		{
			try
			{
				return getter.invoke(obj, (Object[]) null);
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
			try
			{
				setter.invoke(obj, value);
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
	
	public void addListener(AttributeListener listener)
	{
		if (!canListen())
			throw new ObjectReflectionException("Can't add listener");
		
	}
	
	public void removeListener(AttributeListener listener)
	{
		if (!canListen())
			throw new ObjectReflectionException("Can't add listener");
	}
	
	public AttributeValueRange getValueRange()
	{
		return null;
	}
}
