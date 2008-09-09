package jfg.reflect;

import jfg.AttributeException;

public class ObjectReflectionException extends AttributeException
{
	private static final long serialVersionUID = 4374613611117201575L;
	
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
