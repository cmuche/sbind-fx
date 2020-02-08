package de.cmuche.sbindfxtest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
@AllArgsConstructor
public class Baz
{
  private String fieldOne;
  private String fieldTwo;
  private String fieldThree;
  private BufferedImage image;
}
