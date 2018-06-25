package org.scalacheck.ops.time.joda

import org.joda.time.{Chronology, DateTimeZone, ReadableDuration}
import org.scalacheck.ops.time.{AbstractTimeGenerators, FromLong}

import scala.concurrent.duration._

private[joda] trait JodaAbstractDateTimeGenerators extends AbstractTimeGenerators with FromLong {
  override type DurationType = ReadableDuration
  override type ParamsType = JodaTimeParams

  override val defaultRange: DurationType = asDuration(365.days.toMillis)

  def defaultDateTimeZone: DateTimeZone

  def defaultChronology(implicit dateTimeZone: DateTimeZone): Chronology

  override val defaultParams: JodaTimeParams = {
    new JodaTimeParams(defaultChronology(defaultDateTimeZone), defaultDateTimeZone)
  }

  override protected[time] def asDuration(millis: Long): ReadableDuration = org.joda.time.Duration.millis(millis)

  override protected[time] def asLong(duration: ReadableDuration): Long = duration.getMillis

}
