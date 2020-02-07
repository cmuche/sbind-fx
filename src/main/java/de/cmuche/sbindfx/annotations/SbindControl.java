package de.cmuche.sbindfx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SbindControl
{
  String expression();

  String property() default "text";

  Class converter() default Object.class;
}
