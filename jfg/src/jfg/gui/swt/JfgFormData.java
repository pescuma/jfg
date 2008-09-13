package jfg.gui.swt;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jfg.Attribute;
import jfg.gui.SimpleTextTranslator;
import jfg.gui.TextTranslator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;

public final class JfgFormData
{
	public static final int SYNC_GUI = 1;
	public static final int SYNC_GUI_BATCH = 2;
	public static final int SYNC_GUI_FAST = 3;
	public static final int DIALOG = 4;
	
	public Map<Object, SWTWidgetBuilder> builders = new HashMap<Object, SWTWidgetBuilder>();
	public List<SWTBuilderTypeSelector> builderTypeSelectors = new ArrayList<SWTBuilderTypeSelector>();
	public List<SWTAttributeFilter> attributeFilters = new ArrayList<SWTAttributeFilter>();
	
	public Map<String, String> fieldTypes = new HashMap<String, String>();
	public Set<String> fieldsToHide = new HashSet<String>();
	
	public SWTComponentFactory componentFactory = new SWTSimpleComponentFactory();
	
	public int maxGroupAttributeLevels = 1;
	
	public TextTranslator textTranslator = new SimpleTextTranslator();
	
	public boolean showReadOnly = false;
	
	public boolean updateGuiWhenModelChanges = true;
	
	/** In ms. Use -1 to never, 0 to update when inside the event handler, >0 to time and group change events */
	public int timeToUpdateModelWhenGuiChanges = 1000;
	
	public boolean updateModelInBatch = true;
	
	public boolean markFieldsWhithUncommitedChanges = true;
	
	public JfgFormData()
	{
		this(-1);
	}
	
	public JfgFormData(int style)
	{
		builders.put(String.class, new SWTTextBuilder());
		
		builders.put(byte.class, new SWTNumberBuilder());
		builders.put(Byte.class, new SWTNumberBuilder());
		builders.put(short.class, new SWTNumberBuilder());
		builders.put(Short.class, new SWTNumberBuilder());
		builders.put(int.class, new SWTNumberBuilder());
		builders.put(Integer.class, new SWTNumberBuilder());
		builders.put(long.class, new SWTNumberBuilder());
		builders.put(Long.class, new SWTNumberBuilder());
		
		builders.put(float.class, new SWTRealBuilder());
		builders.put(Float.class, new SWTRealBuilder());
		builders.put(double.class, new SWTRealBuilder());
		builders.put(Double.class, new SWTRealBuilder());
		
		builders.put(boolean.class, new SWTCheckboxBuilder());
		builders.put(Boolean.class, new SWTCheckboxBuilder());
		
		builders.put(Enum.class, new SWTComboBuilder());
		
		builders.put("text", new SWTTextBuilder());
		builders.put("number", new SWTNumberBuilder());
		builders.put("real", new SWTRealBuilder());
		builders.put("checkbox", new SWTCheckboxBuilder());
		builders.put("combo", new SWTComboBuilder());
		builders.put("password", new SWTPasswordBuilder());
		builders.put("scale", new SWTScaleBuilder());
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			public Object getTypeFor(Attribute attrib)
			{
				return fieldTypes.get(attrib.getName());
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			public Object getTypeFor(Attribute attrib)
			{
				String name = getSimpleName(attrib).toLowerCase();
				
				if (name.indexOf("password") >= 0 || name.indexOf("passwd") >= 0)
					return "password";
				
				return null;
			}
			
			private String getSimpleName(Attribute attrib)
			{
				String name = attrib.getName();
				int index = name.lastIndexOf('.');
				if (index >= 0 && index < name.length() - 1)
					name = name.substring(index + 1);
				return name;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			private SWTComboBuilder comboBuilder = new SWTComboBuilder();
			
			public Object getTypeFor(Attribute attrib)
			{
				if (comboBuilder.accept(attrib))
					return "combo";
				
				return null;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			private SWTScaleBuilder scaleBuilder = new SWTScaleBuilder();
			
			public Object getTypeFor(Attribute attrib)
			{
				if (scaleBuilder.accept(attrib))
					return "scale";
				
				return null;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			public Object getTypeFor(Attribute attrib)
			{
				Object type = attrib.getType();
				if (builders.get(type) != null)
					return null;
				
				if (!(type instanceof Class))
					return null;
				
				Class<?> cls = (Class<?>) type;
				if (!cls.isEnum())
					return null;
				
				return Enum.class;
			}
		});
		
		attributeFilters.add(new SWTAttributeFilter() {
			public boolean hideAttribute(Attribute attrib)
			{
				if (!showReadOnly && !attrib.canWrite())
					return true;
				return false;
			}
		});
		
		attributeFilters.add(new SWTAttributeFilter() {
			public boolean hideAttribute(Attribute attrib)
			{
				return fieldsToHide.contains(attrib.getName());
			}
		});
		
		switch (style)
		{
			case SYNC_GUI:
				updateGuiWhenModelChanges = true;
				timeToUpdateModelWhenGuiChanges = 300;
				updateModelInBatch = false;
				markFieldsWhithUncommitedChanges = false;
				break;
			case SYNC_GUI_BATCH:
				updateGuiWhenModelChanges = true;
				timeToUpdateModelWhenGuiChanges = 1000;
				updateModelInBatch = true;
				markFieldsWhithUncommitedChanges = true;
				break;
			case SYNC_GUI_FAST:
				updateGuiWhenModelChanges = true;
				timeToUpdateModelWhenGuiChanges = 0;
				markFieldsWhithUncommitedChanges = false;
				break;
			case DIALOG:
				updateGuiWhenModelChanges = false;
				timeToUpdateModelWhenGuiChanges = -1;
				markFieldsWhithUncommitedChanges = false;
				break;
		}
	}
	
	public Color createBackgroundColor(Widget ctrl, Color background)
	{
		int r = background.getRed();
		int g = background.getGreen();
		int b = background.getBlue();
		if (b > 125)
			b -= 40;
		else
			b += 40;
		b = max(0, min(255, b));
		return new Color(ctrl.getDisplay(), r, g, b);
	}
}
