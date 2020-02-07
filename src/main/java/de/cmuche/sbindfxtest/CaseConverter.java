package de.cmuche.sbindfxtest;

import de.cmuche.sbindfx.SbindConverter;

public class CaseConverter implements SbindConverter<String, String>
{
  @Override
  public String convert(String s)
  {
    return s.toUpperCase();
  }

  @Override
  public String back(String s)
  {
    return s.toLowerCase();
  }
}
