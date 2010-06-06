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

package org.pescuma.jfg.reflect;

import java.util.ArrayList;
import java.util.List;

class TestClass
{
	private long aa;
	private int bb;
	private String cdEf;
	public float gh;
	private Double ij;
	private TestEnum value;
	private TestSub sub = new TestSub();
	
	private final List<TestListener> listeners = new ArrayList<TestListener>();
	
	public long getAa()
	{
		return aa;
	}
	
	public void setAa(long aa)
	{
		this.aa = aa;
		
		notifyOnChange();
	}
	
	public int getBb()
	{
		return bb;
	}
	
	public void setBb(int bb)
	{
		this.bb = bb;
		
		notifyOnChange();
	}
	
	public String getCdEf()
	{
		return cdEf;
	}
	
	public void setCdEf(String cdEf)
	{
		this.cdEf = cdEf;
		
		notifyOnChange();
	}
	
	public Double getIj()
	{
		return ij;
	}
	
	public void setIj(Double ij)
	{
		this.ij = ij;
		
		notifyOnChange();
	}
	
	public TestEnum getValue()
	{
		return value;
	}
	
	public void setValue(TestEnum value)
	{
		this.value = value;
		
		notifyOnChange();
	}
	
	public TestSub getSub()
	{
		return sub;
	}
	private void notifyOnChange()
	{
		for (TestListener listener : listeners)
		{
			listener.changed();
		}
	}
	
	public void addListener(TestListener listener)
	{
		listeners.add(listener);
	}
	public void removeListener(TestListener listener)
	{
		listeners.remove(listener);
	}
}
