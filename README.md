[![Maven Central](https://img.shields.io/maven-central/v/de.cmuche/sbind-fx.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22de.cmuche%22%20AND%20a:%22sbind-fx%22)

# sBind-FX
## Simple JavaFX *bidirectional* Data Binding

### Create Controller
```
public class MyController extends SbindController
{
}
```

Add the controller class to the ```.fxml``` file
```
fx:controller="MyController"
```

### Define Data Sources
```
@SbindData
public Foo foo;

@SbindData
public Bar bar;
```

### Define Bindings
Important: Fields used in the expressions must have public getters/setters!
#### JavaFX Controls
```
@FXML
@SbindControl(expression = "foo.strField")
public TextField txtFoo;

@FXML
@SbindControl(expression = "foo.bar.strField")
public TextField txtBar;

@FXML
@SbindControl(property = "value", expression = "foo.dateField", converter = DateConverter.class)
public DatePicker dapBaz;
```

#### JavaFX Tables
```
@FXML
@SbindTable(expression = "foo.baz", columns = {
  @SbindColumn(title = "Column One", bindings = {@SbindControl(expression = "fieldOne")}),
  @SbindColumn(title = "Column Two", bindings = {@SbindControl(expression = "fieldTwo")}),
  @SbindColumn(title = "Column Three", bindings = {@SbindControl(expression = "fieldThree")}),
  @SbindColumn(title = "Image Column", bindings = {@SbindControl(expression = "image", converter = BufferedImageToImageViewConverter.class)})
})
public TableView tblTable;
```

### Converters
You can implement custom converters by using the ```SbindConverter``` interface:

```
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
```