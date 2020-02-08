package de.cmuche.sbindfxtest;

import lombok.Data;

import java.awt.*;
import java.util.Date;
import java.util.List;

@Data
public class Foo
{
  private String strField;
  private Date dateField;
  private Bar bar;
  private List<Baz> baz;
  private List<String> listField;
  private Color color;
}
