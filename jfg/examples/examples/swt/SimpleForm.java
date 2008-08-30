package examples.swt;

import java.util.ArrayList;
import java.util.List;

import jfg.gui.swt.JfgFormComposite;
import jfg.gui.swt.JfgFormData;
import jfg.reflect.ObjectReflectionGroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import examples.swt.SimpleForm.TestClass.TestEnum;

public class SimpleForm
{
	static interface ChangeListener
	{
		void onChange();
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
		
		private int a;
		private String name;
		private boolean valid;
		private TestEnum side;
		
		private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
		
		public int getA()
		{
			return a;
		}
		
		public void setA(int a)
		{
			this.a = a;
			
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
		
		public boolean isValid()
		{
			return valid;
		}
		
		public void setValid(boolean valid)
		{
			this.valid = valid;
			
			notifyListeners();
		}
		
		public TestEnum getSide()
		{
			return side;
		}
		
		public void setSide(TestEnum side)
		{
			this.side = side;
			
			notifyListeners();
		}
		
		private void notifyListeners()
		{
			for (ChangeListener l : listeners)
				l.onChange();
		}
		
		public boolean addListener(ChangeListener e)
		{
			return listeners.add(e);
		}
		
		public boolean removeListener(ChangeListener o)
		{
			return listeners.remove(o);
		}
	}
	
	public static void main(String[] args)
	{
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		final TestClass obj = new TestClass();
		
		JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, new JfgFormData());
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		form.setContents(new ObjectReflectionGroup(obj));
		
		Button set = new Button(shell, SWT.PUSH);
		set.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				obj.setA(1);
				obj.setName("Name");
				obj.setValid(true);
				obj.setSide(TestEnum.Top);
			}
		});
		set.setText("Set");
		
		final Text txt = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
		txt.setLayoutData(new GridData(GridData.FILL_BOTH));
		obj.addListener(new ChangeListener() {
			public void onChange()
			{
				showObj(txt, obj);
			}
		});
		showObj(txt, obj);
		
		shell.setText("Simple Form");
		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	protected static void showObj(Text txt, TestClass obj)
	{
		txt.setText("a = " + obj.getA() + "\nname = " + obj.getName() + "\nvalid = " + obj.isValid() + "\nside = " + obj.getSide());
	}
}
