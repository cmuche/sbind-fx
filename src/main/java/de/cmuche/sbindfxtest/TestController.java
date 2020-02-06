package de.cmuche.sbindfxtest;

import de.cmuche.sbindfx.SbindControl;
import de.cmuche.sbindfx.SbindController;
import de.cmuche.sbindfx.SbindData;
import javafx.scene.control.Label;

public class TestController extends SbindController
{
  @SbindData
  public Foo foo;

  @SbindControl(property = "text", expression = "foo.strField")
  public Label lblFoo;

  public TestController()
  {
    foo = new Foo();
    Bar bar = new Bar();
    bar.setStrField("Bar String");
    foo.setStrField("Foo String");
    foo.setBar(bar);

    lblFoo = new Label();
  }
}
