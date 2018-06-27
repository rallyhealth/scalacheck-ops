package org.scalacheck.ops.time

import java.time._
import java.time.temporal.TemporalAmount

import org.scalacheck.Gen

sealed trait JavaLocalDateTimeGenerators extends AbstractTimeGenerators {
  override type InstantType = LocalDateTime
  override type DurationType = TemporalAmount
  override type ParamsType = Clock

  override val defaultParams: Clock = Clock.systemUTC()

  lazy val defaultZoneOffset: ZoneOffset = defaultParams.getZone.getRules.getOffset(defaultParams.instant())

  override val defaultRange: TemporalAmount = Period.ofYears(1)

  override protected[time] def now(implicit params: Clock): LocalDateTime = LocalDateTime.now(params)

  import JavaLocalTimeGenerators.MAX_NANOS

  override def between(start: LocalDateTime, end: LocalDateTime)(implicit params: Clock): Gen[LocalDateTime] = {
    @inline def secondsOf(instant: LocalDateTime): Long = instant.toEpochSecond(params.getZone.getRules.getOffset(instant))
    val startSeconds = secondsOf(start)
    val endSeconds = secondsOf(end)
    if (startSeconds == endSeconds) {
      for {
        nanos <- Gen.chooseNum(start.getNano, end.getNano)
      } yield LocalDateTime.ofEpochSecond(startSeconds, nanos, defaultZoneOffset)
    } else {
      for {
        seconds <- Gen.chooseNum(startSeconds, endSeconds)
        nanos <- seconds match {
          case `startSeconds` =>
            Gen.chooseNum(start.getNano, MAX_NANOS)
          case `endSeconds` =>
            Gen.chooseNum(0, end.getNano)
          case _ =>
            Gen.chooseNum(0, MAX_NANOS)
        }
      } yield {
        LocalDateTime.ofEpochSecond(seconds, nanos, defaultZoneOffset)
      }
    }
  }

  override protected[time] def addToCeil(
    instant: LocalDateTime,
    duration: TemporalAmount
  )(implicit params: Clock): LocalDateTime = {
    try instant plus duration
    catch {
      case dte: DateTimeException if dte.getMessage startsWith "Invalid value for Year" => LocalDateTime.MAX
    }
  }

  override protected[time] def subtractToFloor(
    instant: LocalDateTime,
    duration: TemporalAmount
  )(implicit params: Clock): LocalDateTime = {
    try instant minus duration
    catch {
      case dte: DateTimeException if dte.getMessage startsWith "Invalid value for Year" => LocalDateTime.MIN
    }
  }
}

object JavaLocalDateTimeGenerators extends JavaLocalDateTimeGenerators
