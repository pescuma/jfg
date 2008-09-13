package jfg.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import jfg.AttributeGroup;
import jfg.AttributeListener;

public class MapGroup implements AttributeGroup
{
	private final String name;
	private final Map<?, ?> map;
	private ArrayList<Object> attributes;
	private final Class<?> valueClass;
	
	public MapGroup(Map<?, ?> map)
	{
		this("Map", map, null);
	}
	
	public MapGroup(Map<?, ?> map, Class<?> valueClass)
	{
		this("Map", map, valueClass);
	}
	
	public MapGroup(String name, Map<?, ?> map, Class<?> valueClass)
	{
		this.name = name;
		this.map = map;
		this.valueClass = valueClass;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Collection<Object> getAttributes()
	{
		if (attributes == null)
			loadAttributes();
		
		return attributes;
	}
	
	private void loadAttributes()
	{
		attributes = new ArrayList<Object>();
		
		for (Object key : map.keySet())
			attributes.add(new MapAttribute(map, key, valueClass));
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
