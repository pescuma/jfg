/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.map;

import java.util.Map;

import org.pescuma.jfg.AbstractAttributeValueRange;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;
import org.pescuma.jfg.AttributeListener;
import org.pescuma.jfg.AttributeValueRange;

public class MapAttribute implements Attribute
{
	private final Map<?, ?> map;
	private final Object key;
	private final Class<?> type;
	private final Class<?> externalType;
	private final AttributeValueRange attributeValueRange;
	
	public MapAttribute(Map<?, ?> map, Object key, Class<?> type)
	{
		this.map = map;
		this.key = key;
		externalType = type;
		
		Object value = map.get(key);
		
		if (value != null && Map.class.isInstance(value))
			this.type = Map.class;
		
		else if (type != null)
			this.type = type;
		
		else if (value == null)
			this.type = String.class;
		
		else
			this.type = value.getClass();
		
		if (value == null)
			attributeValueRange = null;
		else
			attributeValueRange = new AbstractAttributeValueRange() {
				@Override
				public boolean canBeNull()
				{
					return false;
				}
			};
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
		return attributeValueRange;
	}
	
	public boolean canWrite()
	{
		return (type != Map.class);
	}
	
	public Object getValue()
	{
		return map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(Object obj)
	{
		if (!canWrite())
			throw new MapAttributeException("Can't write");
		if (obj != null && !type.isInstance(obj))
			throw new ClassCastException("Object should be of type " + type.getName());
		
		((Map) map).put(key, obj);
	}
	
	public AttributeGroup asGroup()
	{
		if (type != Map.class)
			return null;
		
		Map<?, ?> value = (Map<?, ?>) getValue();
		if (value == null)
			return null;
		
		return new MapGroup(getName(), value, externalType);
	}
	
	public AttributeList asList()
	{
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
