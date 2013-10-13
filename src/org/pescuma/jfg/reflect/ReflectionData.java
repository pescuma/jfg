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

package org.pescuma.jfg.reflect;

import static org.pescuma.jfg.StringUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.pescuma.jfg.AttributeListenerConverter;
import org.pescuma.jfg.SpecialFieldHandler;

public class ReflectionData
{
	public final Map<Class<?>, AttributeListenerConverter<?>> attributeListenerConverters = new HashMap<Class<?>, AttributeListenerConverter<?>>();
	
	public final List<String> getterTemplates = new ArrayList<String>();
	public final List<String> setterTemplates = new ArrayList<String>();
	public final List<String> addFieldListenerTemplates = new ArrayList<String>();
	public final List<String> removeFieldListenerTemplates = new ArrayList<String>();
	public final List<String> addObjectListenerTemplates = new ArrayList<String>();
	public final List<String> removeObjectListenerTemplates = new ArrayList<String>();
	
	public final List<String> listenerInterfaceMethodREs = new ArrayList<String>();
	
	public final List<String> classPrefixesIgnoredInAsGroup = new ArrayList<String>();
	
	public final List<SpecialFieldHandler> specialFieldHandlers = new ArrayList<SpecialFieldHandler>();
	
	public MemberFilter memberFilter;
	public boolean usePublic = true;
	public boolean useProtected = false;
	public boolean useFriend = false;
	public boolean usePrivate = false;
	public boolean useTransient = false;
	public boolean useVolatile = true;
	
	private Pattern[] getterREs;
	
	public ReflectionData()
	{
		getterTemplates.add("get%Field%");
		getterTemplates.add("is%Field%");
		
		setterTemplates.add("set%Field%");
		
		addFieldListenerTemplates.add("add%Field%Listener");
		addFieldListenerTemplates.add("add%Field%ChangeListener");
		removeFieldListenerTemplates.add("remove%Field%Listener");
		removeFieldListenerTemplates.add("remove%Field%ChangeListener");
		
		addObjectListenerTemplates.add("addListener");
		addObjectListenerTemplates.add("addChangeListener");
		removeObjectListenerTemplates.add("removeListener");
		removeObjectListenerTemplates.add("removeChangeListener");
		
		listenerInterfaceMethodREs.add("Change");
		listenerInterfaceMethodREs.add("^change");
		
		classPrefixesIgnoredInAsGroup.add("java.");
		classPrefixesIgnoredInAsGroup.add("javax.");
		classPrefixesIgnoredInAsGroup.add("sun.");
		
		memberFilter = new MemberFilter() {
			@Override
			public boolean accept(Member member)
			{
				if (member.isSynthetic())
					return false;
				
				int modifiers = member.getModifiers();
				
				if (!useTransient && Modifier.isTransient(modifiers))
					return false;
				if (!useVolatile && Modifier.isVolatile(modifiers))
					return false;
				
				if (usePublic && Modifier.isPublic(modifiers))
					return true;
				if (useProtected && Modifier.isProtected(modifiers))
					return true;
				if (usePrivate && Modifier.isPrivate(modifiers))
					return true;
				if (useFriend && !Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)
						&& !Modifier.isPrivate(modifiers))
					return true;
				
				return false;
			}
		};
		
		specialFieldHandlers.add(new SpecialFieldHandler() {
			@Override
			public boolean handles(Field field)
			{
				return field.getClass().equals(Image.class);
			}
			
			@Override
			public Object getter(Field field, Object obj) throws IllegalArgumentException, IllegalAccessException
			{
				return field.get(obj);
			}
			
			@Override
			public void setter(Field field, Object obj, Object value) throws IllegalArgumentException,
					IllegalAccessException
			{
				Image oldImage = (Image) field.get(obj);
				if (oldImage != null)
					oldImage.dispose();
				
				if (value != null)
					value = new Image(Display.getDefault(), (Image) value, SWT.IMAGE_COPY);
				field.set(obj, value);
			}
		});
	}
	
	public String[] getGetterNames(String fieldName)
	{
		return buildTemplates(getterTemplates, fieldName);
	}
	
	public String[] getSetterNames(String fieldName)
	{
		return buildTemplates(setterTemplates, fieldName);
	}
	
	public Pattern[] getGetterREs()
	{
		if (getterREs == null)
		{
			getterREs = new Pattern[getterTemplates.size()];
			for (int i = 0; i < getterREs.length; i++)
			{
				String templ = getterTemplates.get(i);
				templ = templ.replace("%Field%", "([A-Z_][a-zA-Z0-9_]*)");
				templ = templ.replace("%field%", "([a-z_][a-zA-Z0-9_]*)");
				getterREs[i] = Pattern.compile("^" + templ + "$");
			}
		}
		
		return getterREs;
	}
	
	public String[] getAddFieldListenerNames(String fieldName)
	{
		return buildTemplates(addFieldListenerTemplates, fieldName);
	}
	
	public String[] getRemoveFieldListenerNames(String fieldName)
	{
		return buildTemplates(removeFieldListenerTemplates, fieldName);
	}
	
	public String[] getAddObjectListenerNames()
	{
		return addObjectListenerTemplates.toArray(new String[addObjectListenerTemplates.size()]);
	}
	
	public String[] getRemoveObjectListenerNames()
	{
		return removeObjectListenerTemplates.toArray(new String[removeObjectListenerTemplates.size()]);
	}
	
	public String[] getListenerInterfaceMethodREs()
	{
		return listenerInterfaceMethodREs.toArray(new String[listenerInterfaceMethodREs.size()]);
	}
	
	private String[] buildTemplates(List<String> templates, String fieldName)
	{
		String[] ret = new String[templates.size()];
		for (int i = 0; i < ret.length; i++)
		{
			String templ = templates.get(i);
			templ = templ.replace("%Field%", firstUpper(fieldName));
			templ = templ.replace("%field%", firstLower(fieldName));
			ret[i] = templ;
		}
		return ret;
	}
	
	public boolean ignoreForAsGroup(String fullName)
	{
		for (String prefix : classPrefixesIgnoredInAsGroup)
		{
			if (fullName.startsWith(prefix))
				return true;
		}
		return false;
	}
	
}
