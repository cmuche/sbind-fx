package de.cmuche.sbindfx.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SbindControls.class)
public @interface SbindControl
{
  String expression();

  String property() default "text";

  String selected() default "";

  Class converter() default Object.class;
}
