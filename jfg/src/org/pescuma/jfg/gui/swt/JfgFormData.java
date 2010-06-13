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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.pescuma.jfg.Attribute;
import org.pescuma.jfg.AttributeGroup;
import org.pescuma.jfg.AttributeList;
import org.pescuma.jfg.gui.SimpleTextTranslator;
import org.pescuma.jfg.gui.TextTranslator;

public final class JfgFormData
{
	// Some presets
	public static final int SYNC_GUI = 1;
	public static final int SYNC_GUI_BATCH = 2;
	public static final int SYNC_GUI_FAST = 3;
	public static final int SYNC_GUI_NO_DELAY = 4;
	public static final int DIALOG = 16;
	
	public Map<Object, SWTWidgetBuilder> builders = new HashMap<Object, SWTWidgetBuilder>();
	public List<SWTBuilderTypeSelector> builderTypeSelectors = new ArrayList<SWTBuilderTypeSelector>();
	public List<SWTAttributeFilter> attributeFilters = new ArrayList<SWTAttributeFilter>();
	
	public Map<String, String> fieldTypes = new HashMap<String, String>();
	public Set<String> fieldsToHide = new HashSet<String>();
	
	public SWTComponentFactory componentFactory = new SWTSimpleComponentFactory();
	
	public SWTLayoutBuilder layout = new SWTSimpleFormBuilder();
	
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
		builders.put(List.class, new SWTObjectListBuilder());
		
		builders.put("text", new SWTTextBuilder());
		builders.put("text_area", new SWTTextAreaBuilder());
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
		builders.put("list_objects", new SWTObjectListBuilder());
		builders.put("group", new SWTGroupBuilder());
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
			public Object getTypeFor(Attribute attrib)
			{
				return fieldTypes.get(attrib.getName());
			}
		});
		
		builderTypeSelectors.add(new SWTBuilderTypeSelector() {
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
			public boolean hideAttribute(Attribute attrib)
			{
				if (showReadOnly || attrib.canWrite())
					return false;
				
				SWTWidgetBuilder builder = getBuilderFor(attrib);
				
				if (builder instanceof SWTGroupBuilder)
				{
					AttributeGroup group = attrib.asGroup();
					if (group == null)
						return true;
					
					for (Attribute ga : group.getAttributes())
						if (!hideAttribute(ga))
							return false;
				}
				else if (builder instanceof SWTObjectListBuilder)
				{
					AttributeList list = attrib.asList();
					if (list.size() < 1)
						return true;
					
					Attribute item = list.get(0);
					AttributeGroup group = item.asGroup();
					if (group == null)
						return !item.canWrite();
					
					for (Attribute ga : group.getAttributes())
						if (!hideAttribute(ga))
							return false;
				}
				
				return true;
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
		return builders.get(getBuilderTypeOf(attrib));
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
