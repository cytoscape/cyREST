package org.cytoscape.rest.internal.task;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;

@BindingAnnotation @Target({ FIELD }) @Retention(RUNTIME)
public @interface LogLocation {}