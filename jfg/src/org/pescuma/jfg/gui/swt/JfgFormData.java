/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import static java.lang.Math.*;
import static org.pescuma.jfg.StringUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;
import org.pescuma.jfg.gui.GuiWidgetListener;
import org.pescuma.jfg.gui.SimpleTextTranslator;
import org.pescuma.jfg.gui.TextTranslator;
import org.pescuma.jfg.gui.WidgetFormater;
import org.pescuma.jfg.gui.WidgetValidator;

public final class JfgFormData
{
	// Object/gui syncronization constants 
	public static final int SYNC_GUI = 1;
	public static final int SYNC_GUI_BATCH = 2;
	public static final int SYNC_GUI_FAST = 3;
	public static final int SYNC_GUI_NO_DELAY = 4;
	public static final int DIALOG = 16;
	
	// Layout hint constants
	public static final int HORIZONTAL_FILL = 1;
	public static final int HORIZONTAL_SHRINK = 2;
	public static final int VERTICAL_FILL = 1 << 4;
	public static final int VERTICAL_SHRINK = 2 << 4;
	public static final int HORIZONTAL_HINT_MASK = 0x0F;
	public static final int VERTICAL_HINT_MASK = 0xF0;
	
	public Map<Object, SWTWidgetBuilder> builders = new HashMap<Object, SWTWidgetBuilder>();
	public List<SWTBuilderTypeSelector> builderTypeSelectors = new ArrayList<SWTBuilderTypeSelector>();
	public List<SWTAttributeFilter> attributeFilters = new ArrayList<SWTAttributeFilter>();
	
	public SWTComponentFactory componentFactory = new SWTSimpleComponentFactory();
	
	public SWTLayoutBuilder layout = new OneAttributePerLineFormLayout();
	
	public int maxAttributeSubLevels = 1;
	
	public TextTranslator textTranslator = new SimpleTextTranslator();
	
	public boolean showReadOnly = false;
	
	public boolean updateGuiWhenModelChanges = true;
	
	enum ModelUpdateStrategy
	{
		Never, UpdateOnGuiChange, BufferUpdatesForTimeout, UpdateAfterFieldStoppedChanging, UpdateAfterAllFieldsStoppedChanging,
	}
	
	public ModelUpdateStrategy modelUpdateStrategy = ModelUpdateStrategy.UpdateAfterAllFieldsStoppedChanging;
	
	/** In ms */
	public int modelUpdateTimeout = 1000;
	
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
		builders.put(File.class, new SWTFileBuilder());
		builders.put(Image.class, new SWTImageBuilder());
		builders.put(List.class, new SWTInlineObjectListBuilder());
		builders.put(Date.class, new SWTDateBuilder(true, true));
		builders.put(Calendar.class, new SWTDateBuilder(true, true));
		
		builders.put("text", new SWTTextBuilder());
		builders.put("text_area", new SWTTextAreaBuilder());
		builders.put("text_with_options", new SWTTextWithOptionsBuilder());
		builders.put("number", new SWTNumberBuilder());
		builders.put("real", new SWTRealBuilder());
		builders.put("checkbox", new SWTCheckboxBuilder());
		builders.put("combo", new SWTComboBuilder());
		builders.put("password", new SWTPasswordBuilder());
		builders.put("scale", new SWTScaleBuilder());
		builders.put("file", new SWTFileBuilder());
		builders.put("file_open", new SWTFileBuilder());
		builders.put("file_save", new SWTFileSaveBuilder());
		builders.put("directory", new SWTDirectoryBuilder());
		builders.put("image", new SWTImageBuilder());
		builders.put("webcam", new SWTImageBuilder());
		builders.put("inline_obj_list", new SWTInlineObjectListBuilder());
		builders.put("object_combo", new SWTObjectComboBuilder());
		builders.put("group", new SWTGroupBuilder());
		builders.put("date", new SWTDateBuilder(true, false));
		builders.put("time", new SWTDateBuilder(false, true));
		builders.put("datetime", new SWTDateBuilder(true, true));
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			@Override
			public Object getTypeFor(Attribute attrib)
			{
				FieldConfig config = fieldsConfig.get(attrib.getName());
				if (config == null)
					return null;
				return config.type;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			@Override
			public Object getTypeFor(Attribute attrib)
			{
				String name = getSimpleName(attrib);
				
				if (attrib.getType() == String.class && (matches(name, "password") || matches(name, "passwd")))
					return "password";
				
				if (attrib.getType() == String.class && (matches(name, "filename") || matches(name, "file")))
					return "file";
				
				if ((attrib.getType() == String.class || attrib.getType() == File.class)
						&& (matches(name, "folder") || matches(name, "path") || matches(name, "directory") || matches(
								name, "dir")))
					return "directory";
				
				return null;
			}
			
			private boolean matches(String name, String str)
			{
				if (name.toLowerCase().equals(str))
					return true;
				if (name.startsWith(str))
					return true;
				int index = name.indexOf(firstUpper(str));
				if (index >= 0)
				{
					index += str.length();
					if (index >= name.length())
						return true;
					
					String c = name.substring(index, index + 1);
					if (Character.isUpperCase(c.charAt(0)))
						return true;
				}
				return false;
			}
			
			private String getSimpleName(Attribute attrib)
			{
				String name = attrib.getName();
				if (name == null)
					return "";
				int index = name.lastIndexOf('.');
				if (index >= 0 && index < name.length() - 1)
					name = name.substring(index + 1);
				return name;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			private SWTComboBuilder comboBuilder = new SWTComboBuilder();
			
			@Override
			public Object getTypeFor(Attribute attrib)
			{
				if (comboBuilder.accept(attrib))
					return "combo";
				
				return null;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			private SWTScaleBuilder scaleBuilder = new SWTScaleBuilder();
			
			@Override
			public Object getTypeFor(Attribute attrib)
			{
				if (scaleBuilder.accept(attrib))
					return "scale";
				
				return null;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			@Override
			public Object getTypeFor(Attribute attrib)
			{
				Object type = attrib.getType();
				if (builders.get(type) != null)
					return null;
				
				if (!(type instanceof Class<?>))
					return null;
				
				Class<?> cls = (Class<?>) type;
				if (!cls.isEnum())
					return null;
				
				return Enum.class;
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			private SWTGroupBuilder groupBuilder = new SWTGroupBuilder();
			
			@Override
			public Object getTypeFor(Attribute attrib)
			{
				if (builders.containsKey(attrib.getType()))
					return null;
				
				if (groupBuilder.accept(attrib))
					return "group";
				
				return null;
			}
		});
		
		attributeFilters.add(new SWTAttributeFilter() {
			@Override
			public Boolean ignoreAttribute(Attribute attrib)
			{
				FieldConfig config = fieldsConfig.get(attrib.getName());
				if (config == null || config.ignored == null)
					return null;
				return !config.ignored;
			}
		});
		
		attributeFilters.add(new SWTAttributeFilter() {
			@Override
			public Boolean ignoreAttribute(Attribute attrib)
			{
				// Check for groups and show then if they exist
				
				SWTWidgetBuilder builder = getBuilderFor(attrib);
				
				AttributeGroup group = null;
				
				if (builder instanceof SWTGroupBuilder)
				{
					group = attrib.asGroup();
					if (group == null)
						return Boolean.TRUE;
				}
				else if (builder instanceof SWTInlineObjectListBuilder)
				{
					AttributeList list = attrib.asList();
					
					// Have to create a new element to inspect
					Attribute item = list.createNewEmptyElement();
					item.setValue(list.createNewElementInstance());
					
					group = item.asGroup();
					if (group == null)
						return null;
				}
				else
				{
					return null;
				}
				
				for (Attribute ga : group.getAttributes())
					if (!JfgFormData.this.ignoreAttribute(ga))
						return Boolean.FALSE;
				
				// Hide because no internal attribute is shown
				return Boolean.TRUE;
			}
		});
		
		attributeFilters.add(new SWTAttributeFilter() {
			@Override
			public Boolean ignoreAttribute(Attribute attrib)
			{
				if (!showReadOnly && !attrib.canWrite())
					return Boolean.TRUE;
				
				return null;
			}
		});
		
		switch (style)
		{
			case SYNC_GUI:
				modelUpdateStrategy = ModelUpdateStrategy.UpdateAfterFieldStoppedChanging;
				modelUpdateTimeout = 1000;
				updateGuiWhenModelChanges = true;
				markFieldsWhithUncommitedChanges = true;
				break;
			case SYNC_GUI_BATCH:
				modelUpdateStrategy = ModelUpdateStrategy.UpdateAfterAllFieldsStoppedChanging;
				modelUpdateTimeout = 1000;
				updateGuiWhenModelChanges = true;
				markFieldsWhithUncommitedChanges = true;
				break;
			case SYNC_GUI_FAST:
				modelUpdateStrategy = ModelUpdateStrategy.BufferUpdatesForTimeout;
				modelUpdateTimeout = 300;
				updateGuiWhenModelChanges = true;
				markFieldsWhithUncommitedChanges = false;
				break;
			case SYNC_GUI_NO_DELAY:
				modelUpdateStrategy = ModelUpdateStrategy.UpdateOnGuiChange;
				updateGuiWhenModelChanges = true;
				markFieldsWhithUncommitedChanges = false;
				break;
			case DIALOG:
				modelUpdateStrategy = ModelUpdateStrategy.Never;
				updateGuiWhenModelChanges = false;
				markFieldsWhithUncommitedChanges = false;
				break;
		}
	}
	
	public SWTWidgetBuilder getBuilderFor(Attribute attrib)
	{
		Object type = getBuilderTypeOf(attrib);
		
		if (type instanceof SWTWidgetBuilder)
			return (SWTWidgetBuilder) type;
		else
			return builders.get(type);
	}
	
	private Object getBuilderTypeOf(Attribute attrib)
	{
		Object type = null;
		for (SWTBuilderTypeSelector selector : builderTypeSelectors)
		{
			type = selector.getTypeFor(attrib);
			if (type != null)
				return type;
		}
		return attrib.getType();
	}
	
	public Color createUncommitedBackgroundColor(Widget ctrl, Color background)
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
	
	public Color createInvalidBackgroundColor(Widget ctrl, Color background)
	{
		int r = background.getRed();
		int g = background.getGreen();
		int b = background.getBlue();
		
		int add = Math.max(0, 25 - Math.min(g, b));
		
		return new Color(ctrl.getDisplay(), r + add, g + add - 25, b + add - 25);
	}
	
	public Color createUncommitedInvalidBackgroundColor(Widget ctrl, Color background)
	{
		Color uncommitedColor = createUncommitedBackgroundColor(ctrl, background);
		Color invalidColor = createInvalidBackgroundColor(ctrl, background);
		return new Color(ctrl.getDisplay(), (uncommitedColor.getRed() + invalidColor.getRed()) / 2,
				(uncommitedColor.getGreen() + invalidColor.getGreen()) / 2,
				(uncommitedColor.getBlue() + invalidColor.getBlue()) / 2);
	}
	
	public SWTLayoutBuilder createLayoutFor(String attributeName, Composite root, Runnable layoutListener)
	{
		SWTLayoutBuilder ret = null;
		
		FieldConfig config = fieldsConfig.get(attributeName);
		if (config != null)
			ret = config.layout;
		
		if (ret == null)
			ret = layout;
		
		ret = ret.clone();
		ret.init(root, layoutListener, this);
		return ret;
	}
	
	public boolean ignoreAttribute(Attribute attrib)
	{
		for (SWTAttributeFilter filter : attributeFilters)
		{
			Boolean ignore = filter.ignoreAttribute(attrib);
			if (ignore != null)
				return ignore;
		}
		
		return false;
	}
	
	public static class FieldConfig
	{
		Boolean ignored;
		Object type;
		SWTWidgetBuilder builder;
		Boolean showLabel;
		int layoutHint = 0;
		int heightHint = SWT.DEFAULT;
		SWTLayoutBuilder layout;
		boolean showNameAsShadowText = false;
		String shadowText;
		WidgetFormater formater;
		Object widgetData;
		WidgetValidator[] validators;
		final List<GuiWidgetListener> listeners = new ArrayList<GuiWidgetListener>();
		
		public FieldConfig ignore()
		{
			this.ignored = false;
			return this;
		}
		
		public FieldConfig setType(Object type)
		{
			this.type = type;
			return this;
		}
		
		public FieldConfig setBuilder(SWTWidgetBuilder builder)
		{
			this.builder = builder;
			return this;
		}
		
		/**
		 * @param layoutHint JfgFormData.HORIZONTAL_* or JfgFormData.VERTICAL_*
		 */
		public FieldConfig setLayoutHint(int layoutHint)
		{
			this.layoutHint = layoutHint;
			return this;
		}
		
		public FieldConfig setHeightHint(int heightHint)
		{
			this.heightHint = heightHint;
			return this;
		}
		
		public FieldConfig setLayout(SWTLayoutBuilder layout)
		{
			this.layout = layout;
			return this;
		}
		
		public FieldConfig formateWith(WidgetFormater formater)
		{
			this.formater = formater;
			return this;
		}
		
		public FieldConfig setShadowText(String shadowText)
		{
			this.shadowText = shadowText;
			return this;
		}
		
		public FieldConfig showLabel(boolean showLabel)
		{
			this.showLabel = showLabel;
			return this;
		}
		
		public FieldConfig showNameAsShadowText()
		{
			this.showNameAsShadowText = true;
			this.showLabel = false;
			return this;
		}
		
		public FieldConfig setWidgetData(Object widgetData)
		{
			this.widgetData = widgetData;
			return this;
		}
		
		public FieldConfig validateWith(WidgetValidator... validators)
		{
			this.validators = validators;
			return this;
		}
		
		public FieldConfig addListener(GuiWidgetListener listener)
		{
			listeners.add(listener);
			return this;
		}
	}
	
	public final Map<String, FieldConfig> fieldsConfig = new HashMap<String, FieldConfig>();
	
	public FieldConfig configure(String fieldName)
	{
		FieldConfig config = fieldsConfig.get(fieldName);
		if (config == null)
		{
			config = new FieldConfig();
			fieldsConfig.put(fieldName, config);
		}
		return config;
	}
}
