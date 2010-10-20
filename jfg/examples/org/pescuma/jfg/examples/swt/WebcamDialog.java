package org.pescuma.jfg.examples.swt;

import static org.pescuma.jfg.gui.swt.JfgHelper.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.reflect.ReflectionGroup;
import org.pescuma.jfg.validators.NotEmptyValidator;

public class WebcamDialog
{
	@SuppressWarnings("unused")
	private static class TestClass
	{
		public String name;
		public Image img1;
		public Image img2;
		public Image img3;
		public Image img4;
		public Image img5;
		public int value;
	}
	
	public static void main(String[] args)
	{
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		// Create the object
		TestClass obj = new TestClass();
		
		// Create the form
		JfgFormData data = new JfgFormData(JfgFormData.SYNC_GUI);
		data.showReadOnly = true;
		data.configure(attribute(TestClass.class, "name")).setType("text_area");
		data.configure(attribute(TestClass.class, "img2")).validateWith(new NotEmptyValidator());
		
		final JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, data);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add elements to form
		form.addContentsFrom(new ReflectionGroup(obj));
		
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
		
		shell.setText("Webcam Dialog");
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
