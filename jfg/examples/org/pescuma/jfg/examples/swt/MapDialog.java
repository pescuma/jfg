package org.pescuma.jfg.examples.swt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pescuma.jfg.gui.swt.JfgFormComposite;
import org.pescuma.jfg.gui.swt.JfgFormData;
import org.pescuma.jfg.map.MapGroup;

public class MapDialog
{
	public static void main(String[] args)
	{
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		// Create the object
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("Text", "abcd");
		map.put("Number", Integer.valueOf(0));
		map.put("Checkbox", Boolean.TRUE);
		
		Map<String, Object> map2 = new LinkedHashMap<String, Object>();
		map2.put("Text 2", "abcd");
		map2.put("Real 2", Float.valueOf(1));
		map2.put("Checkbox 2", Boolean.FALSE);
		map.put("Inner Map", map2);
		
		// Create the form
		final JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, new JfgFormData(JfgFormData.DIALOG));
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add elements to form
		form.addContentsFrom(new MapGroup(map));
		
		// Add an ok button
		Button ok = new Button(shell, SWT.PUSH);
		ok.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				form.copyToModel();
				shell.dispose();
			}
		});
		ok.setText("Ok");
		
		shell.setText("Map Dialog");
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		
		// Show the object
		printMap(map, "");
	}
	
	@SuppressWarnings("unchecked")
	private static void printMap(Map<String, Object> map, String prefix)
	{
		for (Entry<String, Object> entry : map.entrySet())
		{
			Object value = entry.getValue();
			if (value instanceof Map)
				printMap((Map<String, Object>) value, prefix + "\t");
			else
				System.out.println(prefix + entry.getKey() + " = " + value);
		}
	}
}
