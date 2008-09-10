package jfg.reflect;

import jfg.AttributeException;

public class ReflectionException extends AttributeException
{
	private static final long serialVersionUID = 4374613611117201575L;
	
	public ReflectionException()
	{
		super();
	}
	
	public ReflectionException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ReflectionException(String message)
	{
		super(message);
	}
	
	public ReflectionException(Throwable cause)
	{
		super(cause);
	}
}
