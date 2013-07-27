package org.pescuma.jfg.examples.swt;

import org.pescuma.jfg.model.ann.Range;

class TestSub extends ObjectWithListener
{
	@Range(min = 1, max = 5)
	private int b;
	private String cd;
	
	public int getB()
	{
		return b;
	}
	
	public void setB(int b)
	{
		this.b = b;
		
		notifyListeners();
	}
	
	public String getCd()
	{
		return cd;
	}
	
	public void setCd(String cd)
	{
		this.cd = cd;
		notifyListeners();
	}
}
