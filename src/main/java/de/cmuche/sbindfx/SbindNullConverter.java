package de.cmuche.sbindfx;

public class SbindNullConverter implements SbindConverter<Object, Object>
{
  @Override
  public Object convert(Object o)
  {
    return o;
  }

  @Override
  public Object back(Object o)
  {
    return o;
  }
}
