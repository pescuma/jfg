package jfg.map;

import jfg.AttributeException;

public class MapAttributeException extends AttributeException
{
	private static final long serialVersionUID = 1400127785083121947L;
	
	public MapAttributeException()
	{
		super();
	}
	
	public MapAttributeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public MapAttributeException(String message)
	{
		super(message);
	}
	
	public MapAttributeException(Throwable cause)
	{
		super(cause);
	}
}
