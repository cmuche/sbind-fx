[![Maven Central](https://img.shields.io/maven-central/v/de.cmuche/sbind-fx.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22de.cmuche%22%20AND%20a:%22sbind-fx%22)

# sBind-FX
## Simple JavaFX *bidirectional* Data Binding

```
public class TestController extends SbindController
{
  @SbindData
  public Foo foo;

  @FXML
  @SbindControl(property = "text", expression = "foo.strField")
  public TextField lblFoo;

  @FXML
  @SbindControl(property = "text", expression = "foo.bar.strField")
  public TextField lblBar;

  @FXML
  @SbindControl(property = "value", expression = "foo.dateField", converter = DateConverter.class)
  public DatePicker dapBaz;

  public TestController()
  {
    foo = new Foo();
    Bar bar = new Bar();
    bar.setStrField("Bar String");
    foo.setStrField("Foo String");
    foo.setBar(bar);
    foo.setDateField(new Date());
  }

  public void setFoo(Foo foo)
  {
    this.foo = foo;
    changed("foo");
  }
}
```
