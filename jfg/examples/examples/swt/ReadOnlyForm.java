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
	}
	
	public static void main(String[] args)
	{
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		final TestClass obj = new TestClass();
		
		JfgFormData data = new JfgFormData();
		data.showReadOnly = true;
		JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, data);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		// If you want to change borders:
//		GridLayout layout = new GridLayout(2, false);
//		layout.marginHeight = 0;
//		layout.marginWidth = 0;
//		form.setLayout(layout);
		
		form.setContents(new ObjectReflectionGroup(obj));
		
		shell.setText("Read-Only Form");
		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
