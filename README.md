# sBind-FX
## Simple JavaFX Data Binding

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
```