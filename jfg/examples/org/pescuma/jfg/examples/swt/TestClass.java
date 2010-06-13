package org.pescuma.jfg.examples.swt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pescuma.jfg.model.ann.ReadOnly;

class TestClass extends ObjectWithListener
{
	static enum TestEnum
	{
		Left, Rigth, Top, Bottom
	}
	
	private int a;
	private int b = 1;
	private int c = 10;
	private String name;
	private String password;
	private boolean valid;
	private TestClass.TestEnum side;
	private double real;
	private TestSub sub = new TestSub();
	private File file;
	private String path;
	
	@ReadOnly
	public final List<TestSub> subs = new ArrayList<TestSub>();
	public final List<String> names = new ArrayList<String>();
	
	public TestClass()
	{
		TestSub sub1 = new TestSub();
		sub1.setB(1);
		sub1.setCd("Sub 1");
		subs.add(sub1);
		
		TestSub sub2 = new TestSub();
		sub2.setB(2);
		sub2.setCd("Sub 2");
		subs.add(sub2);
		
		names.add("Abc");
		names.add("Xyz");
	}
	
	public int getA()
	{
		return a;
	}
	
	public void setA(int a)
	{
		this.a = a;
		
		notifyListeners();
	}
	
	public int getB()
	{
		return b;
	}
	
	public void setB(int b)
	{
		this.b = b;
		
		notifyListeners();
	}
	
	public int getC()
	{
		return c;
	}
	
	public void setC(int c)
	{
		this.c = c;
		
		notifyListeners();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
		
		notifyListeners();
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
		
		notifyListeners();
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void setValid(boolean valid)
	{
		this.valid = valid;
		
		notifyListeners();
	}
	
	public TestClass.TestEnum getSide()
	{
		return side;
	}
	
	public void setSide(TestClass.TestEnum side)
	{
		this.side = side;
		
		notifyListeners();
	}
	
	public double getReal()
	{
		return real;
	}
	
	public void setReal(double real)
	{
		this.real = real;
		
		notifyListeners();
	}
	
	public TestSub getSub()
	{
		return sub;
	}
	
	// Right now it will only show inner objects if they are read-only
	//		public void setSub(TestSub sub)
	//		{
	//			this.sub = sub;
	//			
	//			notifyListeners();
	//		}
	
	public File getFile()
	{
		return file;
	}
	
	public void setFile(File file)
	{
		this.file = file;
		
		notifyListeners();
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
		
		notifyListeners();
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("a = " + getA() + "\n");
		builder.append("b = " + getB() + "\n");
		builder.append("c = " + getC() + "\n");
		builder.append("name = " + getName() + "\n");
		builder.append("password = " + getPassword() + "\n");
		builder.append("valid = " + isValid() + "\n");
		builder.append("side = " + getSide() + "\n");
		builder.append("real = " + getReal() + "\n");
		builder.append("  b = " + getSub().getB() + "\n");
		builder.append("  cd = " + getSub().getCd() + "\n");
		builder.append("file = " + toString(getFile()) + "\n");
		builder.append("path = " + getPath() + "\n");
		builder.append("subs: " + subs.size() + "\n");
		for (int i = 0; i < subs.size(); i++)
		{
			TestSub sub = subs.get(i);
			builder.append("  item " + i + ":\n");
			builder.append("    b = " + sub.getB() + "\n");
			builder.append("    cd = " + sub.getCd() + "\n");
		}
		builder.append("names: " + names.size() + "\n");
		for (int i = 0; i < names.size(); i++)
		{
			builder.append("  item " + i + ": " + names.get(i) + "\n");
		}
		return builder.toString();
	}
	
	private String toString(File f)
	{
		if (f == null)
			return null;
		try
		{
			return f.getCanonicalPath();
		}
		catch (IOException e)
		{
			return f.getAbsolutePath();
		}
	}
}
