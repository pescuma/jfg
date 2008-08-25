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
	
	public ObjectReflectionAttribute(Object obj, Field field)
	{
		this.obj = obj;
		this.field = field;
		
		if (!Modifier.isFinal(field.getModifiers()))
			setter = getMethod(void.class, "set" + firstUp(field.getName()), field.getType());
		else
			setter = null;
		
		Method m = getMethod(field.getType(), "get" + firstUp(field.getName()));
		if (m == null)
			m = getMethod(field.getType(), "is" + firstUp(field.getName()));
		getter = m;
	}
	
	private Method getMethod(Class<?> returnType, String name, Class<?>... paramTypes)
	{
		Class<?> cls = obj.getClass();
		try
		{
			Method method = cls.getMethod(name, paramTypes);
			if (method.getReturnType() == returnType)
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
	
	private String firstUp(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public String getName()
	{
		return field.getName();
	}
	
	public boolean canWrite()
	{
		return !Modifier.isFinal(field.getModifiers());
	}
	
	public boolean canListen()
	{
		return false;
	}
	
	public Object getType()
	{
		return field.getType();
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
		else
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
		else
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
