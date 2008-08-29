package examples.swt;

import java.util.ArrayList;
import java.util.List;

import jfg.gui.swt.JfgFormComposite;
import jfg.gui.swt.JfgFormData;
import jfg.reflect.ObjectReflectionGroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class SimpleForm
{
	private static interface ChangeListener
	{
		void onChange();
	}
	
	private static class TestClass
	{
		private int a;
		private String name;
		private boolean valid;
		
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
		shell.setLayout(new FillLayout());
		
		final TestClass obj = new TestClass();
		
		JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, new JfgFormData());
		form.setContents(new ObjectReflectionGroup(obj));
		
		Composite buttons = new Composite(form, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		buttons.setLayoutData(gd);
		buttons.setLayout(new RowLayout());
		
		Button set = new Button(buttons, SWT.PUSH);
		set.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				obj.setA(1);
				obj.setName("Name");
				obj.setValid(true);
			}
		});
		set.setText("Set");
		
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
}
