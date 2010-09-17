package org.pescuma.jfg.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.examples.swt.TestClass.TestEnum;
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.reflect.ReflectionGroup;

public class SimpleWithFieldSpecForm
{
	public static void main(String[] args)
	{
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		// Create the object
		final TestClass obj = new TestClass();
		
		// Create the form
		JfgFormData data = new JfgFormData();
		data.configure(TestClass.class.getName() + ".password").setType("text");
		data.configure(TestClass.class.getName() + ".name").setType("password");
		data.configure(TestClass.class.getName() + ".file").setType("file_save");
		
		JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, data);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add elements to form
		form.addContentsFrom(new ReflectionGroup(obj));
		
		// Add a button to set some values to the object
		Button set = new Button(shell, SWT.PUSH);
		set.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				obj.setA(1);
				obj.setB(2);
				obj.setC(12);
				obj.setName("Name");
				obj.setPassword("password");
				obj.setValid(true);
				obj.setSide(TestEnum.Top);
				obj.setReal(1234.56);
				obj.getSub().setB(987);
				obj.getSub().setCd("CD!!");
			}
		});
		set.setText("Set");
		
		// And a textbox to show the object fields
		final Text txt = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
		txt.setLayoutData(new GridData(GridData.FILL_BOTH));
		ChangeListener listener = new ChangeListener() {
			public void onChange()
			{
				txt.setText(obj.toString());
			}
		};
		obj.addListener(listener);
		obj.getSub().addListener(listener);
		txt.setText(obj.toString());
		
		shell.setText("Simple Form With Field Spec");
		shell.setSize(300, 520);
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
