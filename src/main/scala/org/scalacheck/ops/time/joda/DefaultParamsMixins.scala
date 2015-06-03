package org.scalacheck.ops.time.joda

import org.joda.time.chrono.ISOChronology
import org.joda.time.{Chronology, DateTimeZone}

trait UTCTimeZoneDefault {
  self: JodaDateTimeGenerators =>

  override def defaultDateTimeZone: DateTimeZone = DateTimeZone.UTC
}

trait GlobalTimeZoneDefault {
  self: JodaDateTimeGenerators =>

  override def defaultDateTimeZone: DateTimeZone = DateTimeZone.getDefault
}

trait ISOChronologyDefault {
  self: JodaDateTimeGenerators =>

  override def defaultChronology(implicit dateTimeZone: DateTimeZone): Chronology =
    ISOChronology.getInstance(dateTimeZone)
}