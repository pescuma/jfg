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
import jfg.reflect.ObjectReflectionAttribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import examples.swt.CustomForm.TestClass.TestEnum;

public class CustomForm
{
	static interface ChangeListener
	{
		void onChange();
	}
	
	static class ObjectWithListener
	{
		private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
		
		protected void notifyListeners()
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
	
	static class TestSub extends ObjectWithListener
	{
		private int b;
		private String cd;
		
		public int getB()
		{
			return b;
		}
		
		public void setB(int b)
		{
			this.b = b;
			
			notifyListeners();
		}
		
		public String getCd()
		{
			return cd;
		}
		
		public void setCd(String cd)
		{
			this.cd = cd;
			notifyListeners();
		}
	}
	
	static class TestClass extends ObjectWithListener
	{
		static enum TestEnum
		{
			Left,
			Rigth,
			Top,
			Bottom
		}
		
		private int a = 2;
		private int b = 1;
		private String name;
		private boolean valid;
		private TestEnum side;
		private double real;
		private TestSub sub = new TestSub();
		
		public int getA()
		{
			return a;
		}
		
		public void setA(int a)
		{
			this.a = a;
			
			notifyListeners();
		}
		
		public int getB()
		{
			return b;
		}
		
		public void setB(int b)
		{
			this.b = b;
			
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
		
		public double getReal()
		{
			return real;
		}
		
		public void setReal(double real)
		{
			this.real = real;
			
			notifyListeners();
		}
		
		public TestSub getSub()
		{
			return sub;
		}
		
		// Right now it will only show inner objects if they are read-only
//		public void setSub(TestSub sub)
//		{
//			this.sub = sub;
//			
//			notifyListeners();
//		}
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
					public void onChange()
					{
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
		// TODO: Accept add here instead of addCombo
		form.addCombo(new AbstractAttribute() {
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
		
		// Adding a field by reflection
		form.add(new ObjectReflectionAttribute(obj, "side"));
		
		Button set = new Button(shell, SWT.PUSH);
		set.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				obj.setA(1);
				obj.setName("Name");
				obj.setValid(true);
				obj.setSide(TestEnum.Top);
				obj.setReal(1234.56);
				obj.getSub().setB(987);
				obj.getSub().setCd("CD!!");
			}
		});
		set.setText("Set");
		
		final Text txt = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
		
		txt.setLayoutData(new GridData(GridData.FILL_BOTH));
		ChangeListener listener = new ChangeListener() {
			public void onChange()
			{
				showObj(txt, obj);
			}
		};
		obj.addListener(listener);
		obj.getSub().addListener(listener);
		showObj(txt, obj);
		
		shell.setText("Simple Form");
		shell.setSize(300, 400);
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
		txt.setText("a = " + obj.getA() + "\nb = " + obj.getB() + "\nname = " + obj.getName() + "\nvalid = " + obj.isValid() + "\nside = "
				+ obj.getSide() + "\nreal = " + obj.getReal() + "\n  b = " + obj.getSub().getB() + "\n  cd = " + obj.getSub().getCd());
	}
	
}
