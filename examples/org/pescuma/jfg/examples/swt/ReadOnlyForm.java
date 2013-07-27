package org.pescuma.jfg.examples.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.model.ann.Range;
import org.pescuma.jfg.reflect.ReflectionGroup;

public class ReadOnlyForm
{
	static class TestSub
	{
		@Range(min = 1, max = 5)
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
		private File file = new File("C:\\Test.txt");
		private String path = "C:\\Program Files";
		
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
		
		public File getFile()
		{
			return file;
		}
		
		public String getPath()
		{
			return path;
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
		form.addContentsFrom(new ReflectionGroup(obj));
		
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
