package de.cmuche.sbindfx;

import de.cmuche.sbindfx.annotations.*;
import de.cmuche.sbindfx.converters.CollectionToObservableListConverter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class SbindController
{
  private Set<SbindProperty> dataControls;
  private Map<Pair<Object, String>, SbindConverter> dataConverters;
  private Set<Pair<TableView, SbindTable>> dataTables;
  private Map<TableView, Map<Object, Object>> tableOriginalObjects;

  private static final SbindConverter NULL_CONVERTER = new SbindNullConverter();
  private static final String LIST_ITEMS_PROPERTY = "items";

  public void initialize()
  {
    dataControls = new HashSet<>();
    dataConverters = new HashMap<>();
    dataTables = new HashSet<>();
    tableOriginalObjects = new HashMap<>();

    for (Field f : getClass().getDeclaredFields())
    {
      //CONTROLS
      doForEachAnnotationWithType(f, SbindControl.class, ann ->
      {
        SbindControl annCtl = (SbindControl) ann;
        Object control = getFieldInstance(f, this);
        Property fxProp = new SimpleObjectProperty();
        bindControlProperty(control, annCtl.property(), fxProp, true);
        SbindConverter converter = generateConverter(annCtl.converter());

        SbindProperty prop = new SbindProperty(control, annCtl.expression(), annCtl.property(), fxProp, converter);

        dataControls.add(prop);
        dataConverters.put(Pair.of(control, annCtl.property()), converter);
      });

      //TABLES
      doForEachAnnotationWithType(f, SbindTable.class, ann ->
      {
        TableView tableView = (TableView) getFieldInstance(f, this);
        SbindTable bindAnn = (SbindTable) ann;
        initializeTable(tableView, bindAnn);
        dataTables.add(Pair.of(tableView, bindAnn));
      });
    }

    changed(null);
  }

  @SneakyThrows
  private Object getFieldInstance(Field f, Object o)
  {
    return f.get(this);
  }

  @SneakyThrows
  private void doForEachAnnotationWithType(Field field, Class annotaionClass, Consumer consumer)
  {
    Set<Annotation> annos = Arrays.asList(field.getDeclaredAnnotations()).stream().filter(annotaionClass::isInstance).collect(Collectors.toSet());
    if (annotaionClass == SbindControl.class)
    {
      Arrays.asList(field.getDeclaredAnnotations()).stream().filter(SbindControls.class::isInstance).forEach(x -> annos.addAll(Arrays.asList(((SbindControls) x).value())));
    }

    annos.forEach(consumer);
  }

  private void initializeTable(TableView tableView, SbindTable ann)
  {
    if (tableOriginalObjects.containsKey(tableView))
      tableOriginalObjects.get(tableView).clear();
    else
      tableOriginalObjects.put(tableView, new HashMap<>());

    Property fxProp = new SimpleObjectProperty();
    bindControlProperty(tableView, LIST_ITEMS_PROPERTY, fxProp, true);
    CollectionToObservableListConverter converter = new CollectionToObservableListConverter();
    SbindProperty prop = new SbindProperty(tableView, ann.expression(), LIST_ITEMS_PROPERTY, fxProp, converter);
    dataControls.add(prop);
    dataConverters.put(Pair.of(tableView, LIST_ITEMS_PROPERTY), converter);

    for (SbindColumn colAnn : ann.columns())
    {
      TableColumn col = new TableColumn(colAnn.title());
      tableView.getColumns().add(col);

      SbindControl bindAnn = colAnn.binding();
      SbindConverter bindConverter = generateConverter(bindAnn.converter());

      col.setCellValueFactory(param ->
      {
        Object rowValue = ((TableColumn.CellDataFeatures) param).getValue();
        Object dataValue = traverseExpressionGet(rowValue, bindAnn.expression());

        Object cellValue = bindConverter.convert(dataValue);
        SimpleObjectProperty bindProp = new SimpleObjectProperty(cellValue);
        SimpleObjectProperty rawProp = new SimpleObjectProperty(dataValue);

        if (colAnn.sortable())
          tableOriginalObjects.get(tableView).put(cellValue, dataValue);

        rawProp.addListener((observable, oldValue, newValue) -> valueChangedTable(rowValue, bindAnn.expression(), newValue));

        if (cellValue != null)
          bindControlProperty(cellValue, bindAnn.property(), rawProp, false);

        return bindProp;
      });

      col.setSortable(colAnn.sortable());
      if (colAnn.sortable())
        col.setComparator((o1, o2) ->
        {
          Comparable b1 = (Comparable) tableOriginalObjects.get(tableView).get(o1);
          Comparable b2 = (Comparable) tableOriginalObjects.get(tableView).get(o2);
          return ObjectUtils.compare(b1, b2);
        });

    }
  }

  @SneakyThrows
  private SbindConverter generateConverter(Class converterClass)
  {
    if (converterClass == Object.class)
      return NULL_CONVERTER;

    Object converter = converterClass.getConstructor().newInstance();
    return (SbindConverter) converter;
  }

  protected void changed(String field)
  {
    for (SbindProperty prop : dataControls)
    {
      String expBase = splitExpression(prop.getExpression())[0];

      if (field != null && !expBase.equals(field))
        continue;

      Object val = traverseExpressionGet(null, prop.getExpression());

      SbindConverter converter = dataConverters.get(Pair.of(prop.getControl(), prop.getProperty()));
      prop.getFxProperty().setValue(val == null ? null : converter.convert(val));
    }

    dataTables.stream().forEach(x -> x.getLeft().refresh());
  }

  private void valueChanged(Object control, String propName, Object newValue)
  {
    SbindProperty sProp = dataControls.stream().filter(x -> x.getControl() == control && x.getProperty().equals(propName)).findFirst().orElse(null);
    Object convertedValue = dataConverters.get(Pair.of(control, propName)).back(newValue);
    traverseExpressionSet(null, sProp.getExpression(), convertedValue);
  }

  private void valueChangedTable(Object baseObject, String columnExpression, Object newValue)
  {
    traverseExpressionSet(baseObject, columnExpression, newValue);
  }

  @SneakyThrows
  private void bindControlProperty(Object control, String propertyName, Property property, boolean addListener)
  {
    String methodName = (propertyName + "property");
    Method[] methods = control.getClass().getMethods();
    Method m = Arrays.asList(methods).stream()
      .filter(x -> x.getName().equalsIgnoreCase(methodName))
      .findFirst().orElse(null);

    if (m == null)
      return;

    Property controlProp = ((Property) m.invoke(control));
    controlProp.bindBidirectional(property);

    if (addListener)
      controlProp.addListener((observable, oldValue, newValue) -> valueChanged(control, propertyName, newValue));
  }

  private String[] splitExpression(String expression)
  {
    return expression.split("\\.");
  }

  @SneakyThrows
  private Object traverseExpressionGet(Object baseObject, String expression)
  {
    String[] exParts = splitExpression(expression);
    Object currentObj = (baseObject == null) ? this : baseObject;

    for (int i = 0; i < exParts.length; i++)
    {
      if (currentObj == null)
        return null;

      currentObj = fieldGet(currentObj, exParts[i]);
    }

    return currentObj;
  }

  @SneakyThrows
  private void traverseExpressionSet(Object baseObject, String expression, Object value)
  {
    String[] exParts = splitExpression(expression);
    Object currentObj = (baseObject == null) ? this : baseObject;

    for (int i = 0; i < exParts.length; i++)
    {
      if (currentObj == null)
        return;

      if (i == exParts.length - 1)
        fieldSet(currentObj, exParts[i], value);
      else
        currentObj = fieldGet(currentObj, exParts[i]);
    }
  }

  @SneakyThrows
  private Object fieldGet(Object o, String name)
  {
    try
    {
      Field f = o.getClass().getField(name);
      return f.get(o);
    }
    catch (Exception ex)
    {
      return getGetterMethod(o, name).invoke(o);
    }
  }

  @SneakyThrows
  private void fieldSet(Object o, String name, Object value)
  {
    try
    {
      Field f = o.getClass().getField(name);
      f.set(o, value);
    }
    catch (Exception ex)
    {
      getSetterMethod(o, name).invoke(o, value);
    }
  }

  private Method getGetterMethod(Object o, String field) throws Exception
  {
    String mName = ("get" + field);
    String mNameAlt = ("is" + field);
    Method method = Arrays.asList(o.getClass().getDeclaredMethods()).stream()
      .filter(x -> (x.getName().equalsIgnoreCase(mName) || (x.getReturnType() == boolean.class && x.getName().equalsIgnoreCase(mNameAlt))) && x.getParameterCount() == 0)
      .findFirst().orElse(null);

    if (method == null)
      throw new Exception("No Getter found: " + mName);

    return method;
  }

  private Method getSetterMethod(Object o, String field) throws Exception
  {
    String mName = ("set" + field);
    Method method = Arrays.asList(o.getClass().getDeclaredMethods()).stream()
      .filter(x -> x.getName().equalsIgnoreCase(mName) && x.getParameterCount() == 1)
      .findFirst().orElse(null);

    if (method == null)
      throw new Exception("No Setter found: " + mName);

    return method;
  }
}
