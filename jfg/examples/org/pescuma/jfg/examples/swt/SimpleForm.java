package org.pescuma.jfg.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pescuma.jfg.examples.swt.TestClass.TestEnum;
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.gui.swt.JfgFormComposite.LayoutEvent;
import org.pescuma.jfg.gui.swt.JfgFormComposite.LayoutListener;
import org.pescuma.jfg.reflect.ReflectionGroup;

public class SimpleForm
{
	public static void main(String[] args)
	{
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		
		final ScrolledComposite scroll = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scroll.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Composite contents = new Composite(scroll, 0);
		contents.setLayout(new GridLayout());
		
		// Create the object
		final TestClass obj = new TestClass();
		
		// Create the form
		JfgFormComposite form = new JfgFormComposite(contents, SWT.NONE, new JfgFormData());
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		// If you want to change borders:
//		GridLayout layout = new GridLayout(2, false);
//		layout.marginHeight = 0;
//		layout.marginWidth = 0;
//		form.setLayout(layout);
		
		// Add elements to form
		form.addContentsFrom(new ReflectionGroup(obj));
		
		// Add a button to set some values to the object
		Button set = new Button(contents, SWT.PUSH);
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
				
				TestSub sub = new TestSub();
				sub.setB(2);
				sub.setCd("suuuuub");
				obj.subs.add(sub);
				
				obj.names.add("N1");
				obj.names.add("N 2");
			}
		});
		set.setText("Set");
		
		// And a textbox to show the object fields
		final Text txt = new Text(contents, SWT.BORDER | SWT.V_SCROLL);
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
		
		// Setup scroll
		scroll.setContent(contents);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		scroll.setMinSize(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		form.addLayoutListener(new LayoutListener() {
			@Override
			public void layoutChanged(LayoutEvent e)
			{
				contents.layout();
				scroll.setMinSize(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		
		// If you want that the form appears without scroll bars, use this line.
		// The problem with it is that depending on the contents of your objects the form will have 
		// a different size.
		form.copyToGUI();
		
		shell.setText("Simple Form");
		shell.setSize(300, 600);
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
