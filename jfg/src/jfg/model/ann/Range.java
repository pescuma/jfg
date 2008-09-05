package jfg.model.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD })
public @interface Range
{
	long min() default Long.MIN_VALUE;
	double minf() default Double.NEGATIVE_INFINITY;
	
	long max() default Long.MAX_VALUE;
	double maxf() default Double.POSITIVE_INFINITY;
}
