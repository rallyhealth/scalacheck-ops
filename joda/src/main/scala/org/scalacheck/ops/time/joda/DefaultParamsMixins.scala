package org.scalacheck.ops.time.joda

import org.joda.time.chrono.ISOChronology
import org.joda.time.{Chronology, DateTimeZone}

trait UTCTimeZoneDefault {
  self: JodaAbstractDateTimeGenerators =>

  override def defaultDateTimeZone: DateTimeZone = DateTimeZone.UTC
}

trait GlobalTimeZoneDefault {
  self: JodaAbstractDateTimeGenerators =>

  override def defaultDateTimeZone: DateTimeZone = DateTimeZone.getDefault
}

trait ISOChronologyDefault {
  self: JodaAbstractDateTimeGenerators =>

  override def defaultChronology(implicit dateTimeZone: DateTimeZone): Chronology =
    ISOChronology.getInstance(dateTimeZone)
}