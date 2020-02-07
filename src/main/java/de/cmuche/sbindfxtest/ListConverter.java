package de.cmuche.sbindfxtest;

import de.cmuche.sbindfx.SbindConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListConverter implements SbindConverter<Collection, ObservableList>
{

  @Override
  public ObservableList convert(Collection collection)
  {
    return FXCollections.observableList((List) collection.stream().collect(Collectors.toList()));
  }

  @Override
  public Collection back(ObservableList observableList)
  {
    return (Collection) observableList.stream().collect(Collectors.toList());
  }
}
