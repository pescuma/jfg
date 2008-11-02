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

import org.pescuma.jfg.reflect.ReflectionGroup;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

public class ReflectionGroupPerformanceTest extends JapexDriverBase
{
	private Object object;
	
	@Override
	public void prepare(TestCase testCase)
	{
		try
		{
			String clsName = testCase.getParam("class");
			System.out.println(clsName);
			Class<?> cls = getClass().getClassLoader().loadClass(clsName);
			object = cls.newInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void run(TestCase testCase)
	{
		ReflectionGroup group = new ReflectionGroup(object);
		group.getAttributes();
	}
}
