package jfg.gui.swt;

import java.util.Collection;

import jfg.Attribute;
import jfg.AttributeValueRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SWTComboBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null)
			return false;
		
		Collection<?> values = range.getPossibleValues();
		return values != null && values.size() > 0;
	}
	
	public boolean wantNameLabel()
	{
		return true;
	}
	
	public SWTAttribute build(Composite parent, Attribute attrib, JfgFormData data)
	{
		return new AbstractSWTAttribute(parent, attrib, data) {
			
			private Combo combo;
			private Text text;
			private Color background;
			
			@Override
			public void init(SWTCopyManager aManager)
			{
				super.init(aManager);
				
				if (attrib.canWrite())
				{
					combo = data.componentFactory.createCombo(parent, SWT.READ_ONLY);
					fill();
					combo.addListener(SWT.Modify, getModifyListener());
					combo.addListener(SWT.Dispose, getDisposeListener());
					
					background = combo.getBackground();
				}
				else
				{
					text = data.componentFactory.createText(parent, SWT.READ_ONLY);
					text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					text.addListener(SWT.Dispose, getDisposeListener());
					
					background = text.getBackground();
				}
				
				addAttributeListener();
			}
			
			private void fill()
			{
				if (canBeNull())
					combo.add(data.textTranslator.translate("<None>"));
				
				for (Object object : getPossibleValues())
					combo.add(convertToString(object, attrib.getType()));
			}
			
			@Override
			protected void guiToAttribute()
			{
				attrib.setValue(getValue(combo.getSelectionIndex()));
			}
			
			private Object getValue(int index)
			{
				int i = 0;
				
				if (canBeNull())
				{
					if (i == index)
						return null;
					i++;
				}
				
				for (Object object : getPossibleValues())
				{
					if (i == index)
						return object;
					i++;
				}
				
				throw new IllegalArgumentException();
			}
			
			@Override
			protected void attibuteToGUI()
			{
				if (attrib.canWrite())
					combo.select(getIndex(attrib.getValue()));
				else
					text.setText(convertToString(attrib.getValue(), attrib.getType()));
			}
			
			private int getIndex(Object value)
			{
				int i = 0;
				
				if (canBeNull())
				{
					if (value == null)
						return i;
					i++;
				}
				
				for (Object object : getPossibleValues())
				{
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
			
			private Collection<?> getPossibleValues()
			{
				return attrib.getValueRange().getPossibleValues();
			}
			
			private boolean canBeNull()
			{
				return attrib.getValueRange().canBeNull();
			}
			
			@Override
			protected void markField()
			{
				if (attrib.canWrite())
				{
					combo.setBackground(data.createBackgroundColor(combo, background));
				}
				else
				{
					text.setBackground(data.createBackgroundColor(text, background));
				}
			}
			
			@Override
			protected void unmarkField()
			{
				if (attrib.canWrite())
				{
					combo.setBackground(background);
				}
				else
				{
					text.setBackground(background);
				}
			}
			
		};
	}
}
