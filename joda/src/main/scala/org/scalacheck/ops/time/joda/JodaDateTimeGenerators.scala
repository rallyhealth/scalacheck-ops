package org.scalacheck.ops.time.joda

import org.joda.time.{Chronology, DateTimeZone, ReadableDuration}
import org.scalacheck.ops.time.AbstractTimeGenerators

private[joda] trait JodaDateTimeGenerators extends AbstractTimeGenerators {
  override type DurationType = ReadableDuration
  override type ParamsType = JodaTimeParams

  def defaultDateTimeZone: DateTimeZone

  def defaultChronology(implicit dateTimeZone: DateTimeZone = defaultDateTimeZone): Chronology

  override val defaultParams: JodaTimeParams =
    new JodaTimeParams(defaultChronology(defaultDateTimeZone), defaultDateTimeZone)

  override protected[time] def duration(millis: Long): ReadableDuration = org.joda.time.Duration.millis(millis)

  override protected[time] def millis(duration: ReadableDuration): Long = duration.getMillis
}
