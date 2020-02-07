package de.cmuche.sbindfxtest;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Foo
{
  private String strField;
  private Date dateField;
  private Bar bar;
  private Baz baz;
  private List<String> listField;
}
