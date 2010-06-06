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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeListener;


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
	
	public MapGroup(String name, Map<?, ?> map)
	{
		this(name, map, null);
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
