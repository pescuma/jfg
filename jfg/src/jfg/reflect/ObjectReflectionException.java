package jfg.reflect;

import jfg.AttributeException;

public class ObjectReflectionException extends AttributeException
{
	public ObjectReflectionException()
	{
		super();
	}
	
	public ObjectReflectionException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ObjectReflectionException(String message)
	{
		super(message);
	}
	
	public ObjectReflectionException(Throwable cause)
	{
		super(cause);
	}
}
