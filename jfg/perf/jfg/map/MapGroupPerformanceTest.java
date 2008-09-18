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

import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

public class MapGroupPerformanceTest extends JapexDriverBase
{
	private Map<String, Object> map;
	
	@Override
	public void prepare(TestCase testCase)
	{
		map = new LinkedHashMap<String, Object>();
		map.put("abdDe", "aaa");
		map.put("number", Integer.valueOf(1));
		map.put("chk", Boolean.TRUE);
		map.put("byz_asdf", null);
	}
	
	@Override
	public void run(TestCase testCase)
	{
		MapGroup group = new MapGroup(map);
		group.getAttributes();
	}
}
