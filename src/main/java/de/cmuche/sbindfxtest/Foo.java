package de.cmuche.sbindfxtest;

import lombok.Data;

import java.util.Date;

@Data
public class Foo
{
  private String strField;
  private Date dateField;
  private Bar bar;
}
