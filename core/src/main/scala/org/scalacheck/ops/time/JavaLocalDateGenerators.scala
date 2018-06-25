package org.scalacheck.ops.time

import java.time.temporal.TemporalAmount
import java.time._

import org.scalacheck.Gen

sealed trait JavaLocalDateGenerators extends AbstractTimeGenerators {
  override type InstantType = LocalDate
  override type DurationType = TemporalAmount
  override type ParamsType = Clock

  override def defaultParams: Clock = Clock.systemUTC()

  override val defaultRange: TemporalAmount = Period.ofYears(1)

  override protected[time] def now(implicit params: Clock): LocalDate = LocalDate.now()

  override def between(start: LocalDate, end: LocalDate)
    (implicit params: Clock): Gen[LocalDate] = {
    for {
      epochDay <- Gen.choose(start.toEpochDay, end.toEpochDay)
    } yield LocalDate.ofEpochDay(epochDay)
  }

  override protected[time] def addToCeil(instant: LocalDate, duration: TemporalAmount)
    (implicit params: Clock): LocalDate = {
    try instant plus duration
    catch {
      case dte: DateTimeException if dte.getMessage startsWith "Invalid value for Year" => LocalDate.MAX
    }
  }

  override protected[time] def subtractToFloor(instant: LocalDate, duration: TemporalAmount)
    (implicit params: Clock): LocalDate = {
    try instant minus duration
    catch {
      case dte: DateTimeException if dte.getMessage startsWith "Invalid value for Year" => LocalDate.MIN
    }
  }
}

object JavaLocalDateGenerators extends JavaLocalDateGenerators
