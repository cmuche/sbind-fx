package de.cmuche.sbindfx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SbindControls
{
  SbindControl[] value();
}
