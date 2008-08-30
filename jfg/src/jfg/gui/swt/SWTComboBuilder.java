package jfg.gui.swt;

import java.util.Collection;
import java.util.Iterator;

import jfg.Attribute;
import jfg.AttributeListener;
import jfg.AttributeValueRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SWTComboBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return false;
		Collection<Object> values = range.getPossibleValues();
		return values != null && values.size() > 0;
	}
	
	public boolean wantNameLabel()
	{
		return true;
	}
	
	public SWTAttribute build(final Composite parent, final Attribute attrib, final JfgFormData data)
	{
		
		return new SWTAttribute() {
			
			private boolean ignoreToGUI;
			private boolean ignoreToAttribute;
			private AttributeListener attributeListener;
			private Combo combo;
			private Text text;
			private Collection<Object> values = attrib.getValueRange().getPossibleValues();
			private boolean canBeNull = attrib.getValueRange().canBeNull();
			
			public void init()
			{
				if (attrib.canWrite())
				{
					combo = data.componentFactory.createCombo(parent, SWT.READ_ONLY);
					combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					fill();
				}
				else
				{
					text = data.componentFactory.createText(parent, SWT.READ_ONLY);
					text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				}
				
				if (attrib.canListen())
				{
					attributeListener = new AttributeListener() {
						public void onChange()
						{
							if (ignoreToGUI)
								return;
							
							copyToGUI();
						}
					};
					
					attrib.addListener(attributeListener);
				}
				
				if (attrib.canWrite())
				{
					combo.addListener(SWT.Modify, new Listener() {
						public void handleEvent(Event event)
						{
							if (ignoreToAttribute)
								return;
							
							copyToAttribute();
						}
					});
				}
				
				combo.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event)
					{
						if (attrib.canListen())
							attrib.removeListener(attributeListener);
					}
				});
				
				copyToGUI();
			}
			
			private void fill()
			{
				if (canBeNull)
					combo.add(data.textTranslator.translate("<None>"));
				
				for (Iterator<Object> it = values.iterator(); it.hasNext();)
				{
					Object object = it.next();
					combo.add(convertToString(object, attrib.getType()));
				}
			}
			
			public void copyToAttribute()
			{
				if (!attrib.canWrite())
					return;
				
				ignoreToGUI = true;
				
				attrib.setValue(getValue(combo.getSelectionIndex()));
				
				ignoreToGUI = false;
			}
			
			private Object getValue(int index)
			{
				int i = 0;
				
				if (canBeNull)
				{
					if (i == index)
						return null;
					i++;
				}
				
				for (Iterator<Object> it = values.iterator(); it.hasNext();)
				{
					Object object = it.next();
					if (i == index)
						return object;
					i++;
				}
				
				throw new IllegalArgumentException();
			}
			
			public void copyToGUI()
			{
				ignoreToAttribute = true;
				
				if (attrib.canWrite())
					combo.select(getIndex(attrib.getValue()));
				else
					text.setText(convertToString(attrib.getValue(), attrib.getType()));
				
				ignoreToAttribute = false;
			}
			
			private int getIndex(Object value)
			{
				int i = 0;
				
				if (canBeNull)
				{
					if (value == null)
						return i;
					i++;
				}
				
				for (Iterator<Object> it = values.iterator(); it.hasNext();)
				{
					Object object = it.next();
					if (object == value)
						return i;
					i++;
				}
				
				throw new IllegalArgumentException();
			}
			
			private String convertToString(Object value, Object type)
			{
				if (value == null)
					return data.textTranslator.translate("null");
				
				if (type instanceof Class)
				{
					Class<?> cls = (Class<?>) type;
					if (cls.isEnum())
						return data.textTranslator.enumElement(cls.getName() + "." + ((Enum<?>) value).name());
				}
				
				return value.toString();
			}
		};
	}
	
}
