package examples.swt;

import jfg.gui.swt.JfgFormComposite;
import jfg.gui.swt.JfgFormData;
import jfg.reflect.ObjectReflectionGroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ReadOnlyForm
{
	static class TestSub
	{
		private int b = 987;
		private String cd = "CD!!";
		
		public int getB()
		{
			return b;
		}
		
		public String getCd()
		{
			return cd;
		}
	}
	
	static class TestClass
	{
		static enum TestEnum
		{
			Left,
			Rigth,
			Top,
			Bottom
		}
		
		private int a = 1234;
		private String name = "Asdf";
		private boolean valid = true;
		private TestEnum side = TestEnum.Rigth;
		private double real = 1234.56;
		private TestSub sub = new TestSub();
		
		public int getA()
		{
			return a;
		}
		
		public String getName()
		{
			return name;
		}
		
		public boolean isValid()
		{
			return valid;
		}
		
		public TestEnum getSide()
		{
			return side;
		}
		
		public double getReal()
		{
			return real;
		}
		
		public TestSub getSub()
		{
			return sub;
		}
	}
	
	public static void main(String[] args)
	{
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		// Create the object
		TestClass obj = new TestClass();
		
		// Create the form
		JfgFormData data = new JfgFormData();
		data.showReadOnly = true;
		JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, data);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add elements to form
		form.addContentsFrom(new ObjectReflectionGroup(obj));
		
		shell.setText("Read-Only Form");
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
