package jfg.reflect;

import java.util.HashMap;
import java.util.Map;

import jfg.AttributeListenerConverter;

public class ObjectReflectionData
{
	public final Map<Class<?>, AttributeListenerConverter<?>> attributeListenerConverters = new HashMap<Class<?>, AttributeListenerConverter<?>>();
}
