package de.cmuche.sbindfx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class SbindController
{
  private Map<String, Object> dataSources;
  private Map<String, SbindProperty> dataControls;

  @SneakyThrows
  public void initialize()
  {
    dataSources = new HashMap<>();
    dataControls = new HashMap<>();

    for (Field f : getClass().getDeclaredFields())
    {
      if (f.getDeclaredAnnotation(SbindData.class) != null)
        dataSources.put(f.getName(), f.get(this));

      if (f.getDeclaredAnnotation(SbindControl.class) != null)
      {
        SbindControl ann = f.getDeclaredAnnotation(SbindControl.class);
        Control control = (Control) f.get(this);
        Property fxProp = new SimpleObjectProperty();
        bindControlProperty(control, ann.property(), fxProp);
        SbindProperty prop = new SbindProperty(control, ann.property(), ann.expression(), fxProp);
        dataControls.put(f.getName(), prop);
      }
    }
  }

  @SneakyThrows
  private void bindControlProperty(Control control, String propertyName, Property property)
  {
    String methodName = (propertyName + "property").toLowerCase();
    Method[] methods = control.getClass().getMethods();
    Method m = Arrays.asList(methods).stream().filter(x -> x.getName().toLowerCase().equals(methodName)).findFirst().orElse(null);
    Property controlProp = ((Property) m.invoke(control));
    controlProp.bindBidirectional(property);
  }

  @SneakyThrows
  private Object getObjectField(Object o, String field)
  {
    String mName = ("get" + field).toLowerCase();
    Method method = Arrays.asList(o.getClass().getDeclaredMethods()).stream()
      .filter(x -> x.getName().toLowerCase().equals(mName) && x.getParameterCount() == 0)
      .findFirst().orElse(null);
    return method.invoke(o);
  }

  public Object getDataValue(String expression)
  {
    String[] exParts = expression.split("\\.");
    Object currentObj = dataSources.get(exParts[0]);
    for (int i = 1; i < exParts.length; i++)
      currentObj = getObjectField(currentObj, exParts[i]);
    return currentObj;
  }
}
