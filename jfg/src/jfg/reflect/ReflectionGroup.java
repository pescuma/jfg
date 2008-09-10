package jfg.reflect;

import static jfg.reflect.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.AttributeListener;

public class ReflectionGroup implements AttributeGroup
{
	private final String name;
	private final Object obj;
	private final boolean useStatic;
	private final ReflectionData data;
	private ArrayList<Object> attributes;
	private Method addListener;
	private Method removeListener;
	
	private Object listener;
	private final List<AttributeListener> listeners = new ArrayList<AttributeListener>();
	
	private final MemberFilter memberFilter = new MemberFilter() {
		public boolean accept(Member member)
		{
			if (useStatic != Modifier.isStatic(member.getModifiers()))
				return false;
			
			return data.memberFilter.accept(member);
		}
	};
	
	public ReflectionGroup(Object obj)
	{
		this(null, obj, new ReflectionData());
	}
	
	public ReflectionGroup(String name, Object obj)
	{
		this(name, obj, new ReflectionData());
	}
	
	public ReflectionGroup(Object obj, ReflectionData data)
	{
		this(null, obj, data);
	}
	
	public ReflectionGroup(String name, Object obj, ReflectionData data)
	{
		this.obj = obj;
		this.data = data;
		useStatic = (obj instanceof Class<?>);
		
		if (name == null)
			this.name = getObjClass().getSimpleName();
		else
			this.name = name;
	}
	
	private Class<?> getObjClass()
	{
		if (useStatic)
			return (Class<?>) obj;
		else
			return obj.getClass();
	}
	
	private Object getObjInstance()
	{
		if (useStatic)
			return null;
		else
			return obj;
	}
	
	public String getName()
	{
		return name;
	}
	public Collection<Object> getAttributes()
	{
		if (attributes == null)
			loadAttributes();
		
		return attributes;
	}
	
	private void loadAttributes()
	{
		attributes = new ArrayList<Object>();
		
		addListener = getListenerMethod(memberFilter, getObjClass(), data.getAddObjectListenerNames(), data.getRemoveObjectListenerNames(),
				data);
		if (addListener != null)
			removeListener = getMethod(memberFilter, getObjClass(), data.getRemoveObjectListenerNames(), addListener.getParameterTypes());
		
		if (addListener != null)
			addListener.setAccessible(true);
		if (removeListener != null)
			removeListener.setAccessible(true);
		
		addAttributesFrom(getObjClass());
	}
	
	private void addAttributesFrom(Class<?> cls)
	{
		if (cls == Object.class)
			return;
		
		addAttributesFrom(cls.getSuperclass());
		
		for (Field field : cls.getDeclaredFields())
		{
			Method getter = getMethod(memberFilter, cls, data.getGetterNames(field.getName()));
			if (!memberFilter.accept(field) && getter == null)
				continue;
			
			attributes.add(new ReflectionAttribute(this, obj, field, data));
		}
		
		Pattern[] getterREs = new Pattern[data.getterTemplates.size()];
		for (int i = 0; i < getterREs.length; i++)
		{
			String templ = data.getterTemplates.get(i);
			templ = templ.replaceAll("%Field%", "([A-Z_][a-zA-Z0-9_])");
			templ = templ.replaceAll("%field%", "([a-z_][a-zA-Z0-9_])");
			getterREs[i] = Pattern.compile("^" + templ + "$");
		}
		
		for (Method method : cls.getDeclaredMethods())
		{
			if (!memberFilter.accept(method))
				continue;
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes != null && parameterTypes.length > 0)
				continue;
			
			if (method.getReturnType() == void.class)
				continue;
			
			String methodName = method.getName();
			if (methodName.equals("getClass"))
				continue;
			
			String attrName = null;
			for (int i = 0; i < getterREs.length; i++)
			{
				Matcher matcher = getterREs[i].matcher(methodName);
				if (!matcher.matches())
					continue;
				
				attrName = firstLower(matcher.group(1));
				break;
			}
			if (attrName == null)
				continue;
			
			ReflectionAttribute attrib = new ReflectionAttribute(this, obj, attrName, data);
			if (hasAttribute(attrib.getName()))
				continue;
			
			attributes.add(attrib);
		}
	}
	
	private boolean hasAttribute(String attrName)
	{
		for (Object attribute : attributes)
		{
			if (!(attribute instanceof Attribute))
				continue;
			
			Attribute attr = (Attribute) attribute;
			if (attr.getName().equals(attrName))
				return true;
		}
		return false;
	}
	
	private String firstLower(String str)
	{
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	public boolean canListen()
	{
		if (attributes == null)
			loadAttributes();
		
		return addListener != null && removeListener != null;
	}
	
	private void notifyChange()
	{
		for (AttributeListener al : listeners)
		{
			al.onChange();
		}
	}
	
	public void addListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ReflectionException("Can't add listener");
		
		if (listener == null)
		{
			Class<?> interfaceClass = addListener.getParameterTypes()[0];
			
			listener = wrapListener(interfaceClass, new AttributeListener() {
				public void onChange()
				{
					notifyChange();
				}
			}, data);
			
			invoke(getObjInstance(), addListener, listener);
		}
		
		listeners.add(attributeListener);
	}
	
	public void removeListener(AttributeListener attributeListener)
	{
		if (!canListen())
			throw new ReflectionException("Can't add listener");
		
		listeners.remove(attributeListener);
		
		if (listeners.size() <= 0)
		{
			invoke(getObjInstance(), removeListener, listener);
			listener = null;
		}
	}
}
