package de.cmuche.sbindfxtest;

import de.cmuche.sbindfx.SbindControl;
import de.cmuche.sbindfx.SbindController;
import de.cmuche.sbindfx.SbindData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class TestController extends SbindController
{
  @SbindData
  public Foo foo;

  @FXML
  @SbindControl(property = "text", expression = "foo.strField", converter = CaseConverter.class)
  public TextField lblFoo;

  @FXML
  @SbindControl(property = "text", expression = "foo.bar.strField")
  public TextField lblBar;

  /*@FXML
  @SbindControl(property = "text", expression = "foo.bar.strField")
  public DatePicker dapBaz;*/

  public TestController()
  {
    foo = new Foo();
    Bar bar = new Bar();
    bar.setStrField("Bar String");
    foo.setStrField("Foo String");
    foo.setBar(bar);

  }

  @FXML
  private void click(ActionEvent event) throws Exception
  {
    System.out.println(foo);
  }
}
