package jfg.reflect;

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
