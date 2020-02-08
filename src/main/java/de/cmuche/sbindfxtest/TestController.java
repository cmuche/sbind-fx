package de.cmuche.sbindfxtest;

import de.cmuche.sbindfx.SbindController;
import de.cmuche.sbindfx.annotations.SbindColumn;
import de.cmuche.sbindfx.annotations.SbindControl;
import de.cmuche.sbindfx.annotations.SbindData;
import de.cmuche.sbindfx.annotations.SbindTable;
import de.cmuche.sbindfx.converters.BufferedImageToImageViewConverter;
import de.cmuche.sbindfx.converters.CollectionToObservableListConverter;
import de.cmuche.sbindfx.converters.ColorToPaintConverter;
import de.cmuche.sbindfx.converters.DateToLocalDateConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestController extends SbindController
{
  @SbindData
  @Getter
  @Setter
  public String str = "String";

  @SbindData
  @Getter
  @Setter
  public Foo foo;

  @FXML
  @SbindControl(property = "value", expression = "foo.color", converter = ColorToPaintConverter.class)
  public ColorPicker copColor;

  @FXML
  @SbindControl(expression = "str")
  public TextField lblFoo;

  @FXML
  @SbindControl(expression = "foo.bar.strField")
  public TextField lblBar;

  @FXML
  @SbindControl(property = "value", expression = "foo.dateField", converter = DateToLocalDateConverter.class)
  public DatePicker dapBaz;

  @FXML
  @SbindControl(property = "items", expression = "foo.listField", converter = CollectionToObservableListConverter.class)
  public ListView lstList;

  @FXML
  @SbindTable(expression = "foo.baz", columns = {
    @SbindColumn(title = "Column One", binding = @SbindControl(expression = "fieldOne")),
    @SbindColumn(title = "Column Two", binding = @SbindControl(expression = "fieldTwo")),
    @SbindColumn(title = "Column Three", binding = @SbindControl(expression = "fieldThree")),
    @SbindColumn(title = "Image Column", binding = @SbindControl(expression = "image", converter = BufferedImageToImageViewConverter.class))
  })
  public TableView tblTable;

  @SneakyThrows
  public TestController()
  {
    foo = new Foo();
    Bar bar = new Bar();
    bar.setStrField("Bar String");
    foo.setStrField("Foo String");
    foo.setBar(bar);
    foo.setDateField(new Date());
    foo.setColor(Color.CYAN);

    List<String> list = new ArrayList<>();
    list.add("one");
    list.add("two");
    list.add("three");
    foo.setListField(list);

    BufferedImage image = ImageIO.read(new File("test.png"));

    List<Baz> bList = new ArrayList<>();
    bList.add(new Baz("one", "two", "three", image));
    bList.add(new Baz("four", "five", "six", image));
    bList.add(new Baz("seven", "eight", "nine", image));
    foo.setBaz(bList);
  }

  @FXML
  private void click(ActionEvent event) throws Exception
  {
    foo.getBaz().remove(tblTable.getSelectionModel().getSelectedItem());
    changed(null);
    System.out.println(foo);
  }

  public void setFoo(Foo foo)
  {
    this.foo = foo;
    changed("foo");
  }
}
