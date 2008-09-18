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
 * You should have received a copy of the GNU Lesser General Public License along with Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

package jfg.map;

import jfg.AttributeException;

public class MapAttributeException extends AttributeException
{
	private static final long serialVersionUID = 1400127785083121947L;
	
	public MapAttributeException()
	{
		super();
	}
	
	public MapAttributeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public MapAttributeException(String message)
	{
		super(message);
	}
	
	public MapAttributeException(Throwable cause)
	{
		super(cause);
	}
}
