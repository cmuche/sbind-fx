package de.cmuche.sbindfx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SbindColumn
{
  String title();

  SbindControl binding();

  boolean sortable() default true;
}
