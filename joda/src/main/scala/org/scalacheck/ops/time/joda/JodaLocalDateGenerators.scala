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
  )(implicit params: Chronology): LocalDate = {
    instant plus duration
  }

  override protected[time] def subtractToFloor(
    instant: LocalDate,
    duration: ReadablePeriod
  )(implicit params: Chronology): LocalDate = {
    instant minus duration
  }

  override def between(start: LocalDate, end: LocalDate)(implicit params: Chronology): Gen[LocalDate] = {
    val startYear = start.getYear
    val startMonthOfYear = start.getMonthOfYear
    for {
      year <- Gen.choose(startYear, end.getYear)
      monthOfYear <- {
        if (year == startYear) Gen.choose(start.getMonthOfYear, end.getMonthOfYear)
        else Gen.choose(params.monthOfYear.getMinimumValue, params.monthOfYear.getMaximumValue)
      }
      dayOfMonth <- {
        if (year == startYear && monthOfYear == startMonthOfYear) Gen.choose(startMonthOfYear, end.getDayOfMonth)
        else Gen.choose(params.dayOfMonth.getMinimumValue, params.dayOfMonth.getMaximumValue)
      }
    } yield new LocalDate(year, monthOfYear, dayOfMonth, params)
  }
}

object JodaLocalDateGenerators extends JodaLocalDateGenerators
