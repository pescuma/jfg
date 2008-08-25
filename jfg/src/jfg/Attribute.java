package jfg;

public interface Attribute
{
	String getName();
	
	boolean canWrite();
	boolean canListen();
	
	Object getType();
	
	Object getValue();
	void setValue(Object obj);
	
	void addListener(AttributeListener listener);
	void removeListener(AttributeListener listener);
	
	AttributeValueRange getValueRange();
}
