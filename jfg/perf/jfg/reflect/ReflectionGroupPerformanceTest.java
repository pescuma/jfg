package jfg.reflect;

import jfg.reflect.ObjectReflectionGroup;

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
		ObjectReflectionGroup group = new ObjectReflectionGroup(object);
		group.getAttributes();
	}
}
