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
	AttributeList asList();
}
