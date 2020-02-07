package de.cmuche.sbindfx;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SbindProperty
{
  private Control control;
  private String expression;
  private String property;
  private Property fxProperty;
  private SbindConverter converter;
}
