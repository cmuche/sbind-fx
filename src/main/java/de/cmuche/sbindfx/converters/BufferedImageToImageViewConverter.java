package de.cmuche.sbindfx.converters;

import de.cmuche.sbindfx.SbindConverter;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class BufferedImageToImageViewConverter implements SbindConverter<BufferedImage, ImageView>
{
  private static BufferedImageToImageConverter baseConverter = new BufferedImageToImageConverter();

  @Override
  public ImageView convert(BufferedImage bufferedImage)
  {
    ImageView view = new ImageView();
    view.setImage(baseConverter.convert(bufferedImage));
    return view;
  }

  @Override
  public BufferedImage back(ImageView imageView)
  {
    return baseConverter.back(imageView.getImage());
  }
}
