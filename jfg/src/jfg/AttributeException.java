package jfg;

public class AttributeException extends RuntimeException
{
	public AttributeException()
	{
		super();
	}
	
	public AttributeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public AttributeException(String message)
	{
		super(message);
	}
	
	public AttributeException(Throwable cause)
	{
		super(cause);
	}
}
