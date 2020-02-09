package de.cmuche.sbindfx.converters;

import de.cmuche.sbindfx.SbindConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToLocalDateConverter implements SbindConverter<Date, LocalDate>
{
  @Override
  public LocalDate convert(Date date)
  {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    return localDateTime.toLocalDate();
  }

  @Override
  public Date back(LocalDate localDate)
  {
    return java.util.Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }
}
