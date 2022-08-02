package org.scalacheck.ops.time.joda

import org.joda.time.chrono.ISOChronology
import org.joda.time.{Chronology, LocalDate, Period, ReadablePeriod}
import org.scalacheck.Gen
import org.scalacheck.ops.time.AbstractTimeGenerators

sealed trait JodaLocalDateGenerators extends AbstractTimeGenerators {
  override type InstantType = LocalDate
  override type DurationType = ReadablePeriod
  override type ParamsType = Chronology

  override def defaultParams: Chronology = ISOChronology.getInstanceUTC

  override val defaultRange: ReadablePeriod = Period.years(1)

  override protected[time] def now(implicit params: Chronology): LocalDate = LocalDate.now(params)

  override protected[time] def addToCeil(
    instant: LocalDate,
    duration: ReadablePeriod
  )(implicit
    params: Chronology
  ): LocalDate = {
    instant.plus(duration)
  }

  override protected[time] def subtractToFloor(
    instant: LocalDate,
    duration: ReadablePeriod
  )(implicit
    params: Chronology
  ): LocalDate = {
    instant.minus(duration)
  }

  override def between(
    start: LocalDate,
    end: LocalDate
  )(implicit
    params: Chronology
  ): Gen[LocalDate] = {
    val startYear = start.getYear
    val startMonthOfYear = start.getMonthOfYear
    val endYear = end.getYear
    val endMonthOfYear = end.getMonthOfYear
    for {
      year <- Gen.choose(startYear, endYear)

      monthOfYear <- {
        val minMonth =
          if (year == startYear) startMonthOfYear
          else params.monthOfYear.getMinimumValue
        val maxMonth =
          if (year == endYear) endMonthOfYear
          else params.monthOfYear.getMaximumValue
        Gen.choose(minMonth, maxMonth)
      }

      dayOfMonth <- {
        val firstOfSelectedMonth = new LocalDate(year, monthOfYear, params.dayOfMonth.getMinimumValue, params)
        val minDay =
          if (year == startYear && monthOfYear == startMonthOfYear) start.getDayOfMonth
          else firstOfSelectedMonth.getDayOfMonth
        val maxDay =
          if (year == endYear && monthOfYear == endMonthOfYear) end.getDayOfMonth
          else firstOfSelectedMonth.dayOfMonth.getMaximumValue // last day of selected month
        Gen.choose(minDay, maxDay)
      }
    } yield new LocalDate(year, monthOfYear, dayOfMonth, params)
  }
}

object JodaLocalDateGenerators extends JodaLocalDateGenerators
