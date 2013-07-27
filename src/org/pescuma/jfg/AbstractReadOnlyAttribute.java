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

package org.pescuma.jfg;

public abstract class AbstractReadOnlyAttribute implements Attribute
{
	public AttributeValueRange getValueRange()
	{
		return null;
	}
	
	public boolean canWrite()
	{
		return false;
	}
	
	public void setValue(Object obj)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean canListen()
	{
		return false;
	}
	
	public void addListener(AttributeListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	public void removeListener(AttributeListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	public AttributeGroup asGroup()
	{
		return null;
	}
	
	public AttributeList asList()
	{
		return null;
	}
}
