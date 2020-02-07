package de.cmuche.sbindfx;

import javafx.beans.property.Property;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SbindProperty
{
  private Object control;
  private String expression;
  private String property;
  private Property fxProperty;
  private SbindConverter converter;
}
