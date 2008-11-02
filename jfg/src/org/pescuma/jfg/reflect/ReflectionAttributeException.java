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

package org.pescuma.jfg.reflect;

import org.pescuma.jfg.AttributeException;

public class ReflectionAttributeException extends AttributeException
{
	private static final long serialVersionUID = 4374613611117201575L;
	
	public ReflectionAttributeException()
	{
		super();
	}
	
	public ReflectionAttributeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ReflectionAttributeException(String message)
	{
		super(message);
	}
	
	public ReflectionAttributeException(Throwable cause)
	{
		super(cause);
	}
}
