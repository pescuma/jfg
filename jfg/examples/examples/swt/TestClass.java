package examples.swt;

class TestClass extends ObjectWithListener
{
	static enum TestEnum
	{
		Left,
		Rigth,
		Top,
		Bottom
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
	
	@Override
	public String toString()
	{
		return "a = " + getA() + "\nb = " + getB() + "\nc = " + getC() + "\nname = " + getName() + "\npassword = " + getPassword()
				+ "\nvalid = " + isValid() + "\nside = " + getSide() + "\nreal = " + getReal() + "\n  b = " + getSub().getB() + "\n  cd = "
				+ getSub().getCd();
		
	}
}
