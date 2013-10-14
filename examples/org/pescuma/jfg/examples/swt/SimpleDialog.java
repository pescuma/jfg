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
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormComposite.LayoutEvent;
import org.pescuma.jfg.gui.swt.JfgFormComposite.LayoutListener;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.reflect.ReflectionGroup;

public class SimpleDialog
{
	public static void main(String[] args)
	{
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		
		final ScrolledComposite scroll = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scroll.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Composite contents = new Composite(scroll, 0);
		contents.setLayout(new GridLayout());
		
		// Create the object
		TestClass obj = new TestClass();
		
		// Create the form
		JfgFormData data = new JfgFormData(JfgFormData.DIALOG);
		
		// To allow the expand/collapse the subitems in the subs list, uncomment
		// the following line
		// And maybe add a name field as the first item of TestSub class?
		//
		// data.configure(JfgHelper.attribute(TestClass.class,
		// "subs")).setWidgetData(
		// new ObjectListGuiWidget.Data().allowToCollapse());
		//
		// or
		//
		// data.allowCollapseObjectsInListByDefault = true;
		
		// Depending on your objects depth, you may need this too
		//
		// data.maxAttributeSubLevels = 5;
		
		final JfgFormComposite form = new JfgFormComposite(contents, SWT.NONE, data);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add elements to form
		form.addContentsFrom(new ReflectionGroup(obj));
		
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
		
		// Add an ok button
		Button ok = new Button(shell, SWT.PUSH);
		ok.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				form.copyToModel();
				shell.dispose();
			}
		});
		ok.setText("Ok");
		
		// If you want that the form appears without scroll bars, use this line.
		// The problem with it is that depending on the contents of your objects
		// the form will have a different size.
		form.copyToGUI();
		
		shell.setText("Simple Dialog");
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		
		// Show the object
		System.out.println(obj.toString());
	}
}
