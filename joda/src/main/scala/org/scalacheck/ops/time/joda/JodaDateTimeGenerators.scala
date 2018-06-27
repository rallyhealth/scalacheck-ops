package org.scalacheck.ops.time.joda

import org.joda.time.DateTime
import org.scalacheck.ops.time.joda.ChronologyOps._

sealed trait JodaDateTimeGenerators extends JodaAbstractDateTimeGenerators
  with UTCTimeZoneDefault
  with ISOChronologyDefault {
  override type InstantType = DateTime
  override type ParamsType = JodaTimeParams

  override protected[time] def asInstant(millis: Long)(implicit params: JodaTimeParams): DateTime = {
    new DateTime(millis, params.chronology)
  }

  override protected[time] def asLong(instant: DateTime)(implicit params: JodaTimeParams): Long = instant.getMillis

  override protected[time] def addToCeil(instant: InstantType, duration: DurationType)
    (implicit params: ParamsType): InstantType = {
    try instant plus duration
    catch {
      case tooLarge: ArithmeticException => params.chronology.maxDateTime
    }
  }

  override protected[time] def subtractToFloor(instant: InstantType, duration: DurationType)
    (implicit params: ParamsType): InstantType = {
    try instant minus duration
    catch {
      case tooSmall: ArithmeticException => params.chronology.minDateTime
    }
  }

}

object JodaDateTimeGenerators extends JodaDateTimeGenerators
