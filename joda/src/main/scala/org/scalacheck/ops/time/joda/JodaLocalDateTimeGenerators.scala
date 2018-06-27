package org.scalacheck.ops.time.joda

import org.joda.time.{LocalDateTime, ReadableDuration}
import org.scalacheck.ops.time.joda.ChronologyOps._

sealed trait JodaLocalDateTimeGenerators extends JodaAbstractDateTimeGenerators
  with UTCTimeZoneDefault
  with ISOChronologyDefault {
  override type InstantType = LocalDateTime

  override protected[time] def asInstant(millis: Long)(implicit params: JodaTimeParams): LocalDateTime =
    new LocalDateTime(millis, params.chronology)

  override protected[time] def addToCeil(instant: LocalDateTime, duration: ReadableDuration)
    (implicit params: JodaTimeParams): LocalDateTime = {
    try instant plus duration
    catch {
      case tooLarge: ArithmeticException => params.chronology.maxLocalDateTime
    }
  }

  override protected[time] def subtractToFloor(instant: LocalDateTime, duration: ReadableDuration)
    (implicit params: JodaTimeParams): LocalDateTime = {
    try instant minus duration
    catch {
      case tooSmall: ArithmeticException => params.chronology.minLocalDateTime
    }
  }

  override protected[time] def asLong(instant: LocalDateTime)(implicit params: JodaTimeParams): Long = {
    instant.toDateTime(params.dateTimeZone).getMillis
  }
}

object JodaLocalDateTimeGenerators extends JodaLocalDateTimeGenerators
