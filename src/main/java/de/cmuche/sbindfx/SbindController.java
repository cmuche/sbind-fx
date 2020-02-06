package de.cmuche.sbindfx;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class SbindController
{
  private Map<String, Object> dataSources;
  private Map<String, SbindControl> dataControls;

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
        dataControls.put(f.getName(), f.getDeclaredAnnotation(SbindControl.class));
    }
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
