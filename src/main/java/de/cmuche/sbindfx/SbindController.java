package de.cmuche.sbindfx;

import de.cmuche.sbindfx.annotations.SbindColumn;
import de.cmuche.sbindfx.annotations.SbindControl;
import de.cmuche.sbindfx.annotations.SbindData;
import de.cmuche.sbindfx.annotations.SbindTable;
import de.cmuche.sbindfxtest.ListConverter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public abstract class SbindController
{
  private Map<String, Object> dataSources;
  private Set<SbindProperty> dataControls;
  private Map<Pair<Object, String>, SbindConverter> dataConverters;

  @SneakyThrows
  public void initialize()
  {
    dataSources = new HashMap<>();
    dataControls = new HashSet<>();
    dataConverters = new HashMap<>();

    //DATA
    for (Field f : getClass().getDeclaredFields())
    {
      for (Annotation ann : f.getDeclaredAnnotations())
      {
        if (ann instanceof SbindData)
          if (f.getDeclaredAnnotation(SbindData.class) != null)
            dataSources.put(f.getName(), f.get(this));
      }

      //CONTROLS
      for (Annotation ann : f.getDeclaredAnnotations())
      {
        if (ann instanceof SbindControl)
        {
          SbindControl annCtl = (SbindControl) ann;
          Control control = (Control) f.get(this);
          Property fxProp = new SimpleObjectProperty();
          bindControlProperty(control, annCtl.property(), fxProp);
          SbindConverter converter = generateConverter(annCtl.converter());

          SbindProperty prop = new SbindProperty(control, annCtl.expression(), annCtl.property(), fxProp, converter);

          dataControls.add(prop);
          dataConverters.put(Pair.of(control, annCtl.property()), converter);
        }
      }

      //TABLES
      for (Annotation ann : f.getDeclaredAnnotations())
      {
        if (ann instanceof SbindTable)
          initializeTable((TableView) f.get(this), (SbindTable) ann);
      }
    }

    changed();
  }

  private void initializeTable(TableView tableView, SbindTable ann)
  {
    Property fxProp = new SimpleObjectProperty();
    bindControlProperty(tableView, "items", fxProp);
    SbindProperty prop = new SbindProperty(tableView, ann.expression(), "items", fxProp, new ListConverter());
    dataControls.add(prop);
    dataConverters.put(Pair.of(tableView,"items"), new ListConverter());

    for (SbindColumn colAnn : ann.columns())
    {
      TableColumn col = new TableColumn(colAnn.title());
      tableView.getColumns().add(col);

      for (SbindControl bindAnn : colAnn.bindings())
      {
        col.setCellFactory(new Callback<TableColumn, TableCell>()
        {
          @Override
          public TableCell call(TableColumn param)
          {
            int columnIndex = param.getTableView().getColumns().indexOf(param);
            Object dataObject = param.getTableView().getItems().get(columnIndex);
            Object dataValue = getDataValue(bindAnn.expression(), getObjectField(dataObject, splitExpression(bindAnn.expression())[0]));

            TableCell<Object, Object> tc = new TableCell<>();
            SbindConverter converter = generateConverter(bindAnn.converter());
            tc.setText(converter.convert(dataValue).toString());
            return tc;
          }
        });
      }
    }
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
    for (SbindProperty prop : dataControls)
    {
      String expBase = splitExpression(prop.getExpression())[0];

      if (!expBase.equals(field))
        continue;

      Object val = getDataValue(prop.getExpression(), dataSources.get(splitExpression(prop.getExpression())[0]));
      prop.getFxProperty().setValue(dataConverters.get(Pair.of(prop.getControl(), prop.getProperty())).convert(val));
    }
  }

  private void valueChanged(Object control, String propName, Object newValue)
  {
    SbindProperty sProp = dataControls.stream().filter(x -> x.getControl() == control).findFirst().orElse(null);
    Object convertedValue = dataConverters.get(Pair.of(control, propName)).back(newValue);
    setDataValue(sProp.getExpression(), convertedValue);
  }

  @SneakyThrows
  private void bindControlProperty(Object control, String propertyName, Property property)
  {
    String methodName = (propertyName + "property").toLowerCase();
    Method[] methods = control.getClass().getMethods();
    Method m = Arrays.asList(methods).stream()
      .filter(x -> x.getName().toLowerCase().equals(methodName))
      .findFirst().orElse(null);
    Property controlProp = ((Property) m.invoke(control));
    controlProp.bindBidirectional(property);
    controlProp.addListener((observable, oldValue, newValue) -> valueChanged(control, propertyName, newValue));
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

  private Object getDataValue(String expression, Object o)
  {
    String[] exParts = splitExpression(expression);
    Object currentObj = o;
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
