package examples.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jfg.AbstractAttribute;
import jfg.AbstractAttributeValueRange;
import jfg.AbstractListenerAttribute;
import jfg.AbstractReadOnlyAttribute;
import jfg.AttributeListener;
import jfg.AttributeValueRange;
import jfg.gui.swt.JfgFormComposite;
import jfg.gui.swt.JfgFormData;
import jfg.reflect.ReflectionAttribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import examples.swt.TestClass.TestEnum;

public class CustomForm
{
	public static void main(String[] args)
	{
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		
		// Create the object
		final TestClass obj = new TestClass();
		obj.setA(1234);
		
		// Create the form
		JfgFormData data = new JfgFormData(JfgFormData.SYNC_GUI_BATCH);
		data.showReadOnly = true;
		JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE, data);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// A read-only attribute
		form.add(new AbstractReadOnlyAttribute() {
			public String getName()
			{
				return "gui.a";
			}
			public Object getType()
			{
				return int.class;
			}
			public Object getValue()
			{
				return obj.getA();
			}
		});
		
		// A simple attribute
		form.add(new AbstractAttribute() {
			public String getName()
			{
				return "gui.name";
			}
			public Object getType()
			{
				return String.class;
			}
			public Object getValue()
			{
				return obj.getName();
			}
			public void setValue(Object value)
			{
				obj.setName((String) value);
			}
		});
		
		// A password attribute
		form.addPassword(new AbstractAttribute() {
			public String getName()
			{
				return "gui.password";
			}
			public Object getType()
			{
				return "password";
			}
			public Object getValue()
			{
				return obj.getPassword();
			}
			public void setValue(Object value)
			{
				obj.setPassword((String) value);
			}
			@Override
			public AttributeValueRange getValueRange()
			{
				return new AbstractAttributeValueRange() {
					@Override
					public Object getMax()
					{
						return Integer.valueOf(8);
					}
				};
			}
		});
		
		// An attribute with listener
		form.add(new AbstractListenerAttribute() {
			private Map<AttributeListener, ChangeListener> listeners = new HashMap<AttributeListener, ChangeListener>();
			
			public String getName()
			{
				return "value";
			}
			public Object getType()
			{
				return double.class;
			}
			public Object getValue()
			{
				return obj.getReal();
			}
			public void setValue(Object value)
			{
				obj.setReal((Double) value);
			}
			public void addListener(final AttributeListener alistener)
			{
				ChangeListener listener = new ChangeListener() {
					double old = obj.getReal();
					
					public void onChange()
					{
						double v = obj.getReal();
						if (old == v)
							return;
						old = v;
						
						alistener.onChange();
					}
				};
				if (obj.addListener(listener))
					listeners.put(alistener, listener);
			}
			public void removeListener(AttributeListener alistener)
			{
				ChangeListener listener = listeners.remove(alistener);
				if (listener != null)
					obj.removeListener(listener);
			}
		});
		
		// An attribute with a list of options
		// form.addCombo also works here
		form.add(new AbstractAttribute() {
			public String getName()
			{
				return "b";
			}
			public Object getType()
			{
				return int.class;
			}
			public Object getValue()
			{
				return obj.getB();
			}
			public void setValue(Object value)
			{
				obj.setB((Integer) value);
			}
			@Override
			public AttributeValueRange getValueRange()
			{
				return new AbstractAttributeValueRange() {
					private List<Object> values = new ArrayList<Object>();
					{
						values.add(1);
						values.add(2);
						values.add(3);
					}
					
					@Override
					public Collection<Object> getPossibleValues()
					{
						return values;
					}
					
					@Override
					public boolean canBeNull()
					{
						return false;
					}
				};
			}
		});
		
		// An attribute with a list of options
		// form.addScale also works here
		form.add(new AbstractAttribute() {
			public String getName()
			{
				return "c";
			}
			public Object getType()
			{
				return int.class;
			}
			public Object getValue()
			{
				return obj.getC();
			}
			public void setValue(Object value)
			{
				obj.setC((Integer) value);
			}
			@Override
			public AttributeValueRange getValueRange()
			{
				return new AbstractAttributeValueRange() {
					@Override
					public Object getMax()
					{
						return 15;
					}
					@Override
					public Object getMin()
					{
						return 5;
					}
				};
			}
		});
		
		// Adding a field by reflection
		form.add(new ReflectionAttribute(obj, "side"));
		
		// Add a button to set some values to the object
		Button set = new Button(shell, SWT.PUSH);
		set.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				obj.setA(1);
				obj.setB(2);
				obj.setC(10);
				obj.setName("Name");
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
		
		shell.setText("Simple Form");
		shell.setSize(300, 500);
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
