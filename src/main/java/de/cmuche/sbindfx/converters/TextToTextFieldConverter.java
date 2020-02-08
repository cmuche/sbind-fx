package de.cmuche.sbindfx.converters;

import de.cmuche.sbindfx.SbindConverter;
import javafx.scene.control.TextField;

public class TextToTextFieldConverter implements SbindConverter<String, TextField>
{
  @Override
  public TextField convert(String s)
  {
    return new TextField(s);
  }

  @Override
  public String back(TextField textField)
  {
    return textField.getText();
  }
}
