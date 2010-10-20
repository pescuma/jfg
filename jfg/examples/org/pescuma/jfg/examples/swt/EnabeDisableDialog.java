package org.pescuma.jfg.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pescuma.jfg.gui.AbstractGuiWidgetListener;
import org.pescuma.jfg.gui.GuiWidget;
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.reflect.ReflectionGroup;

public class EnabeDisableDialog
{
	public static void main(String[] args)
	{
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		// Create the object
		TestClass obj = new TestClass();
		
		// Create the form
		final JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, new JfgFormData(JfgFormData.DIALOG));
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add elements to form
		form.addContentsFrom(new ReflectionGroup(obj));
		
		form.getWidgets().findChild(TestClass.class.getName() + ".a").addListener(new AbstractGuiWidgetListener() {
			@Override
			public void onWidgetCreated(GuiWidget widget)
			{
				onWidgetUpdated(widget);
			}
			
			@Override
			public void onWidgetUpdated(GuiWidget widget)
			{
				int a = (Integer) widget.getValue();
				form.getWidgets().findChild(TestClass.class.getName() + ".b").setEnabled(a > 0);
				form.getWidgets().findChild(TestClass.class.getName() + ".b").setValue(a + 1);
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
		
		shell.setText("Enable Disable Dialog");
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
