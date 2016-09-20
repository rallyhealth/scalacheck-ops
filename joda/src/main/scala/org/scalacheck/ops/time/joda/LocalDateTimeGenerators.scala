package org.scalacheck.ops.time.joda

import org.joda.time.{LocalDateTime, ReadableDuration}
import org.scalacheck.ops.time.joda.ChronologyOps._

sealed trait LocalDateTimeGenerators extends JodaDateTimeGenerators with UTCTimeZoneDefault with ISOChronologyDefault {
  override type InstantType = LocalDateTime

  override protected[time] def datetime(millis: Long)(implicit params: JodaTimeParams): LocalDateTime =
    new LocalDateTime(millis, params.chronology)

  override protected[time] def addToCeil(datetime: LocalDateTime, duration: ReadableDuration)
    (implicit params: JodaTimeParams): LocalDateTime = {
    try datetime plus duration
    catch {
      case tooLarge: ArithmeticException => params.chronology.maxLocalDateTime
    }
  }

  override protected[time] def subtractToFloor(datetime: LocalDateTime, duration: ReadableDuration)
    (implicit params: JodaTimeParams): LocalDateTime = {
    try datetime minus duration
    catch {
      case tooSmall: ArithmeticException => params.chronology.minLocalDateTime
    }
  }

  override protected[time] def millis(datetime: LocalDateTime)(implicit params: JodaTimeParams): Long =
    datetime.toDateTime(params.dateTimeZone).getMillis

}

object LocalDateTimeGenerators extends LocalDateTimeGenerators
