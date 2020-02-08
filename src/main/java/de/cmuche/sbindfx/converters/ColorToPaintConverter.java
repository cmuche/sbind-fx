package de.cmuche.sbindfx.converters;

import de.cmuche.sbindfx.SbindConverter;

import java.awt.*;


public class ColorToPaintConverter implements SbindConverter<Color, javafx.scene.paint.Color>
{
  @Override
  public javafx.scene.paint.Color convert(Color color)
  {
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    int a = color.getAlpha();
    double opacity = a / 255.0 ;
   return javafx.scene.paint.Color.rgb(r, g, b, opacity);
  }

  @Override
  public Color back(javafx.scene.paint.Color color)
  {
    return new Color((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getOpacity());
  }
}
