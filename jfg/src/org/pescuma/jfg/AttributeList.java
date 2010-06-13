/*
 * Copyright 2010 Ricardo Pescuma Domenecci
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

public interface AttributeList
{
	String getName();
	Object getElementType();

	int size();
	Attribute get(int index);
	int indexOf(Attribute attrib);
	
	boolean canWrite();
	Attribute createNewElement();
	void add(int index, Attribute item);
	void remove(int index);
	
	boolean canListen();
	void addListener(AttributeListener listener);
	void removeListener(AttributeListener listener);
}
