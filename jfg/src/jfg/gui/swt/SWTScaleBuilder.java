package jfg.gui.swt;

import static java.lang.Math.*;
import static jfg.gui.swt.TypeUtils.*;
import jfg.Attribute;
import jfg.AttributeValueRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

public class SWTScaleBuilder implements SWTWidgetBuilder
{
	public boolean accept(Attribute attrib)
	{
		AttributeValueRange range = attrib.getValueRange();
		if (range == null || range.getMin() == null || range.getMax() == null)
			return false;
		
		Object type = attrib.getType();
		return typeIsNumber(type) || typeIsReal(type) || "scale".equals(type);
	}
	
	public boolean wantNameLabel()
	{
		return true;
	}
	
	public SWTAttribute build(final Composite parent, final Attribute attrib, final JfgFormData data)
	{
		return new AbstractSWTAttribute(parent, attrib, data) {
			
			private Scale scale;
			private Color background;
			
			private Object getType()
			{
				Object type = attrib.getType();
				if ("scale".equals(type))
					type = long.class;
				return type;
			}
			
			@Override
			public void init(SWTCopyManager aManager)
			{
				super.init(aManager);
				
				scale = data.componentFactory.createScale(parent, SWT.NONE);
				scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				scale.addListener(SWT.Selection, getModifyListener());
				scale.addListener(SWT.Dispose, getDisposeListener());
				setLimits();
				
				addAttributeListener();
				
				background = scale.getBackground();
			}
			
			private void setLimits()
			{
				Object type = getType();
				
				Number minObj = (Number) attrib.getValueRange().getMin();
				Number maxObj = (Number) attrib.getValueRange().getMax();
				
				if (typeIsNumber(type))
				{
					long min = minObj.longValue();
					long max = maxObj.longValue();
					
					// It does not make sense beeing > 100
					int diff = (int) min(100, max - min);
					if (diff <= 0)
						throw new IllegalArgumentException();
					
					scale.setMaximum(diff);
					scale.setPageIncrement(max(1, diff / 5));
				}
				else
				{
					// Is real
					double min = minObj.doubleValue();
					double max = maxObj.doubleValue();
					
					double diff = max - min;
					if (diff <= 0)
						throw new IllegalArgumentException();
					
					scale.setMaximum(100);
					scale.setPageIncrement(20);
				}
			}
			
			@Override
			protected void guiToAttribute()
			{
				attrib.setValue(convertToObject(scale.getSelection()));
			}
			
			private Object convertToObject(int selection)
			{
				Object type = getType();
				
				Number minObj = (Number) attrib.getValueRange().getMin();
				Number maxObj = (Number) attrib.getValueRange().getMax();
				
				if (typeIsNumber(type))
				{
					long min = minObj.longValue();
					long max = maxObj.longValue();
					
					long diff = max - min;
					if (diff <= 100)
						return TypeUtils.valueOf(min + selection, type);
					
					double factor = min(100, diff) / 100.0;
					return TypeUtils.valueOf(min + (long) (selection * factor), type);
				}
				else
				{
					// Is real
					double min = minObj.doubleValue();
					double max = maxObj.doubleValue();
					
					double diff = max - min;
					double factor = diff / 100.0;
					
					return TypeUtils.valueOf(min + selection * factor, type);
				}
			}
			
			@Override
			protected void attibuteToGUI()
			{
				scale.setSelection(convertToInt(attrib.getValue()));
			}
			
			private int convertToInt(Object value)
			{
				Object type = getType();
				
				Number minObj = (Number) attrib.getValueRange().getMin();
				Number maxObj = (Number) attrib.getValueRange().getMax();
				
				if (typeIsNumber(type))
				{
					long min = minObj.longValue();
					long max = maxObj.longValue();
					long val = (value == null ? min : ((Number) value).longValue());
					
					long diff = max - min;
					if (diff <= 100)
						return (int) (val - min);
					
					double factor = min(100, diff) / 100.0;
					return (int) round((val - min) / factor);
				}
				else
				{
					// Is real
					double min = minObj.doubleValue();
					double max = maxObj.doubleValue();
					double val = (value == null ? min : ((Number) value).doubleValue());
					
					double diff = max - min;
					double factor = diff / 100.0;
					
					return (int) round((val - min) / factor);
				}
			}
			
			@Override
			protected void markField()
			{
				scale.setBackground(data.createBackgroundColor(scale, background));
			}
			
			@Override
			protected void unmarkField()
			{
				scale.setBackground(background);
			}
			
		};
	}
	
}
