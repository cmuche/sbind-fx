package de.cmuche.sbindfx;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
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
  private Map<Control, SbindConverter> dataConverters;

  @SneakyThrows
  public void initialize()
  {
    dataSources = new HashMap<>();
    dataControls = new HashMap<>();
    dataConverters = new HashMap<>();

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
        SbindConverter converter = generateConverter(ann.converter());

        SbindProperty prop = new SbindProperty(control, ann.expression(), ann.property(), fxProp, converter);

        dataControls.put(f.getName(), prop);
        dataConverters.put(control, converter);
      }
    }

    changed();
  }

  @SneakyThrows
  private SbindConverter generateConverter(Class converterClass)
  {
    if (converterClass == Object.class)
      return new SbindNullConverter();

    Object converter = converterClass.getConstructor().newInstance();
    return (SbindConverter) converter;
  }

  protected void changed()
  {
    for (Map.Entry<String, Object> f : dataSources.entrySet())
      changed(f.getKey());
  }

  protected void changed(String field)
  {
    for (Map.Entry<String, SbindProperty> d : dataControls.entrySet())
    {
      SbindProperty prop = d.getValue();
      String expBase = splitExpression(prop.getExpression())[0];

      if (!expBase.equals(field))
        continue;

      Object val = getDataValue(prop.getExpression());
      prop.getFxProperty().setValue(dataConverters.get(prop.getControl()).convert(val));
    }
  }

  private void valueChanged(Control control, Object newValue)
  {
    SbindProperty sProp = dataControls.values().stream().filter(x -> x.getControl() == control).findFirst().orElse(null);
    Object convertedValue = dataConverters.get(control).back(newValue);
    setDataValue(sProp.getExpression(), convertedValue);
  }

  @SneakyThrows
  private void bindControlProperty(Control control, String propertyName, Property property)
  {
    String methodName = (propertyName + "property").toLowerCase();
    Method[] methods = control.getClass().getMethods();
    Method m = Arrays.asList(methods).stream()
      .filter(x -> x.getName().toLowerCase().equals(methodName))
      .findFirst().orElse(null);
    Property controlProp = ((Property) m.invoke(control));
    controlProp.bindBidirectional(property);
    controlProp.addListener((observable, oldValue, newValue) -> valueChanged(control, newValue));
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

  @SneakyThrows
  private void setObjectField(Object o, String field, Object data)
  {
    String mName = ("set" + field).toLowerCase();
    Method method = Arrays.asList(o.getClass().getDeclaredMethods()).stream()
      .filter(x -> x.getName().toLowerCase().equals(mName) && x.getParameterCount() == 1)
      .findFirst().orElse(null);
    method.invoke(o, data);
  }

  private Object getDataValue(String expression)
  {
    String[] exParts = splitExpression(expression);
    Object currentObj = dataSources.get(exParts[0]);
    for (int i = 1; i < exParts.length; i++)
    {
      currentObj = getObjectField(currentObj, exParts[i]);
      if (currentObj == null)
        return null;
    }
    return currentObj;
  }

  private String[] splitExpression(String expression)
  {
    return expression.split("\\.");
  }

  public void setDataValue(String expression, Object data)
  {
    String[] exParts = splitExpression(expression);
    Object currentObj = dataSources.get(exParts[0]);
    for (int i = 1; i < exParts.length - 1; i++)
      currentObj = getObjectField(currentObj, exParts[i]);

    setObjectField(currentObj, exParts[exParts.length - 1], data);
  }
}
