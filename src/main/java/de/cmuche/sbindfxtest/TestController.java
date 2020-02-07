package de.cmuche.sbindfxtest;

import de.cmuche.sbindfx.SbindController;
import de.cmuche.sbindfx.annotations.SbindColumn;
import de.cmuche.sbindfx.annotations.SbindControl;
import de.cmuche.sbindfx.annotations.SbindData;
import de.cmuche.sbindfx.annotations.SbindTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestController extends SbindController
{
  @SbindData
  public Foo foo;

  public Color color = Color.RED;

  @FXML
  @SbindControl(property = "text", expression = "foo.strField", converter = CaseConverter.class)
  public TextField lblFoo;

  @FXML
  @SbindControl(property = "text", expression = "foo.bar.strField")
  public TextField lblBar;

  @FXML
  @SbindControl(property = "value", expression = "foo.dateField", converter = DateConverter.class)
  public DatePicker dapBaz;

  @FXML
  @SbindControl(property = "items", expression = "foo.listField", converter = ListConverter.class)
  public ListView lstList;

  @FXML
  @SbindTable(expression = "foo.baz", columns = {
    @SbindColumn(title = "Column One", bindings = {@SbindControl(property = "text", expression = "fieldOne")}),
    @SbindColumn(title = "Column Two", bindings = {@SbindControl(property = "text", expression = "fieldTwo")}),
    @SbindColumn(title = "Column Three", bindings = {@SbindControl(property = "text", expression = "fieldThree")})
  })
  public TableView tblTable;

  public TestController()
  {
    foo = new Foo();
    Bar bar = new Bar();
    bar.setStrField("Bar String");
    foo.setStrField("Foo String");
    foo.setBar(bar);
    foo.setDateField(new Date());

    List<String> list = new ArrayList<>();
    list.add("one");
    list.add("two");
    list.add("three");
    foo.setListField(list);

    foo.setBaz(new Baz("one", "two", "three"));
  }

  @FXML
  private void click(ActionEvent event) throws Exception
  {
    System.out.println(foo);
  }

  public void setFoo(Foo foo)
  {
    this.foo = foo;
    changed("foo");
  }
}
