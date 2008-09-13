package jfg.map;

import java.util.Map;

import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.AttributeListener;
import jfg.AttributeValueRange;

public class MapAttribute implements Attribute
{
	private final Map<?, ?> map;
	private final Object key;
	private final Class<?> type;
	
	public MapAttribute(Map<?, ?> map, Object key, Class<?> type)
	{
		this.map = map;
		this.key = key;
		
		if (type != null)
			this.type = type;
		else
		{
			Object value = map.get(key);
			if (value == null)
				this.type = String.class;
			else
				this.type = value.getClass();
		}
	}
	
	public String getName()
	{
		if (key == null)
			return "map.null";
		return "map." + key.toString();
	}
	
	public Object getType()
	{
		return type;
	}
	
	public AttributeValueRange getValueRange()
	{
		return null;
	}
	
	public boolean canWrite()
	{
		return true;
	}
	
	public Object getValue()
	{
		return map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(Object obj)
	{
		if (obj != null && !type.isInstance(obj))
			throw new ClassCastException("Object should be of type " + type.getName());
		
		((Map) map).put(key, obj);
	}
	
	public AttributeGroup asGroup()
	{
		// TODO Handle inner maps as groups
		return null;
	}
	
	public boolean canListen()
	{
		return false;
	}
	
	public void addListener(AttributeListener listener)
	{
		throw new MapAttributeException("Can't add listener");
	}
	
	public void removeListener(AttributeListener listener)
	{
		throw new MapAttributeException("Can't remove listener");
	}
	
}
