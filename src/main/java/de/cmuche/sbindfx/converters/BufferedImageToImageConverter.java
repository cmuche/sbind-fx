package de.cmuche.sbindfx.converters;

import de.cmuche.sbindfx.SbindConverter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class BufferedImageToImageConverter implements SbindConverter<BufferedImage, Image>
{
  @Override
  public Image convert(BufferedImage bufferedImage)
  {
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }

  @Override
  public BufferedImage back(Image image)
  {
    return SwingFXUtils.fromFXImage(image, null);
  }
}
