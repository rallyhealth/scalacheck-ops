package org.scalacheck.ops.time.joda

import org.joda.time.{Chronology, DateTime, LocalDateTime}

import scala.language.implicitConversions

class ChronologyOps(val chronology: Chronology) extends AnyVal {

  def maxMillis: Long = {
    val maxYear = chronology.year.getMaximumValue
    val maxMonthOfYear = chronology.monthOfYear.getMaximumValue
    val maxDayOfMonth = chronology.dayOfMonth.getMaximumValue
    val maxMillisOfDay = chronology.millisOfDay.getMaximumValue
    chronology.getDateTimeMillis(maxYear, maxMonthOfYear, maxDayOfMonth, maxMillisOfDay)
  }

  def maxLocalDateTime: LocalDateTime = new LocalDateTime(maxMillis, chronology)

  def maxDateTime: DateTime = new DateTime(maxMillis, chronology)

  def minMillis: Long = 0

  def minLocalDateTime: LocalDateTime = new LocalDateTime(minMillis, chronology)

  def minDateTime: DateTime = new DateTime(minMillis, chronology)
}

trait ImplicitChronologyOps {

  implicit def fromChronology(chronology: Chronology): ChronologyOps = new ChronologyOps(chronology)
}

object ChronologyOps extends ImplicitChronologyOps
