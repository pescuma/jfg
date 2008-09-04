package jfg.gui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jfg.Attribute;
import jfg.gui.SimpleTextTranslator;
import jfg.gui.TextTranslator;

public final class JfgFormData
{
	public Map<Object, SWTWidgetBuilder> builders = new HashMap<Object, SWTWidgetBuilder>();
	public List<SWTBuilderTypeSelector> builderTypeSelectors = new ArrayList<SWTBuilderTypeSelector>();
	public List<SWTAttributeFilter> attributeFilters = new ArrayList<SWTAttributeFilter>();
	
	public Map<String, String> fieldTypes = new HashMap<String, String>();
	public Set<String> fieldsToHide = new HashSet<String>();
	
	public SWTComponentFactory componentFactory = new SWTSimpleComponentFactory();
	
	public int maxGroupAttributeLevels = 1;
	
	public TextTranslator textTranslator = new SimpleTextTranslator();
	
	public boolean showReadOnly = false;
	
	public JfgFormData()
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
	}
}
