package de.cmuche.sbindfx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SbindControl
{
  String expression();

  String property();
}
