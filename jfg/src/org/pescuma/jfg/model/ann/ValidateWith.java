package org.pescuma.jfg.model.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pescuma.jfg.gui.WidgetValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface ValidateWith
{
	Class<WidgetValidator>[] value();
}
