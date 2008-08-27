package jfg;

public interface Attribute
{
	String getName();
	Object getType();
	AttributeValueRange getValueRange();
	
	boolean canWrite();
	Object getValue();
	void setValue(Object obj);
	
	boolean canListen();
	void addListener(AttributeListener listener);
	void removeListener(AttributeListener listener);
	
	AttributeGroup asGroup();
}
