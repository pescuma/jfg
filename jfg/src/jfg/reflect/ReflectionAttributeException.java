package jfg.reflect;

import jfg.AttributeException;

public class ReflectionAttributeException extends AttributeException
{
	private static final long serialVersionUID = 4374613611117201575L;
	
	public ReflectionAttributeException()
	{
		super();
	}
	
	public ReflectionAttributeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ReflectionAttributeException(String message)
	{
		super(message);
	}
	
	public ReflectionAttributeException(Throwable cause)
	{
		super(cause);
	}
}
