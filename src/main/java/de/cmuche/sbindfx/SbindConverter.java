package de.cmuche.sbindfx;

public interface SbindConverter<F,T>
{
  T convert(F f);
  F back(T t);
}
