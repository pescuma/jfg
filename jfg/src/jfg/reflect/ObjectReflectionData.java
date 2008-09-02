package jfg.reflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jfg.AttributeListenerConverter;

public final class ObjectReflectionData implements Cloneable
{
	public final Map<Class<?>, AttributeListenerConverter<?>> attributeListenerConverters;
	
	public final List<String> getterTemplates;
	public final List<String> setterTemplates;
	public final List<String> addFieldListenerTemplates;
	public final List<String> removeFieldListenerTemplates;
	public final List<String> addObjectListenerTemplates;
	public final List<String> removeObjectListenerTemplates;
	
	public final List<String> listenerInterfaceMethodREs;
	
	public final List<String> classPrefixesIgnoredInAsGroup;
	
	public ObjectReflectionData()
	{
		attributeListenerConverters = new HashMap<Class<?>, AttributeListenerConverter<?>>();
		getterTemplates = new ArrayList<String>();
		setterTemplates = new ArrayList<String>();
		addFieldListenerTemplates = new ArrayList<String>();
		removeFieldListenerTemplates = new ArrayList<String>();
		addObjectListenerTemplates = new ArrayList<String>();
		removeObjectListenerTemplates = new ArrayList<String>();
		listenerInterfaceMethodREs = new ArrayList<String>();
		classPrefixesIgnoredInAsGroup = new ArrayList<String>();
		
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
	}
	
	public ObjectReflectionData(ObjectReflectionData other)
	{
		attributeListenerConverters = new HashMap<Class<?>, AttributeListenerConverter<?>>(other.attributeListenerConverters);
		getterTemplates = new ArrayList<String>(other.getterTemplates);
		setterTemplates = new ArrayList<String>(other.setterTemplates);
		addFieldListenerTemplates = new ArrayList<String>(other.addFieldListenerTemplates);
		removeFieldListenerTemplates = new ArrayList<String>(other.removeFieldListenerTemplates);
		addObjectListenerTemplates = new ArrayList<String>(other.addObjectListenerTemplates);
		removeObjectListenerTemplates = new ArrayList<String>(other.removeObjectListenerTemplates);
		listenerInterfaceMethodREs = new ArrayList<String>(other.listenerInterfaceMethodREs);
		classPrefixesIgnoredInAsGroup = new ArrayList<String>(other.classPrefixesIgnoredInAsGroup);
	}
	
	public String[] getGetterNames(String fieldName)
	{
		return buildTemplates(getterTemplates, fieldName);
	}
	
	public String[] getSetterNames(String fieldName)
	{
		return buildTemplates(setterTemplates, fieldName);
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
			templ = templ.replaceAll("%Field%", firstUp(fieldName));
			templ = templ.replaceAll("%field%", firstLower(fieldName));
			ret[i] = templ;
		}
		return ret;
	}
	
	private String firstUp(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	private String firstLower(String str)
	{
		return str.substring(0, 1).toLowerCase() + str.substring(1);
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
	
	@Override
	public ObjectReflectionData clone()
	{
		return new ObjectReflectionData(this);
	}
	
}
