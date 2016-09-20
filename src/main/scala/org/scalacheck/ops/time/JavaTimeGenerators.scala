package org.scalacheck.ops.time

import java.time.temporal._
import java.time.{Duration, Instant}

object JavaTimeGenerators extends JavaTimeGenerators
trait JavaTimeGenerators extends AbstractDateTimeGenerators {
  override type DateTimeType = Instant
  override type DurationType = TemporalAmount
  override type ParamsType = JavaTimeParams

  override protected[time] def datetime(millis: Long)
    (implicit params: JavaTimeParams): Instant = Instant.ofEpochMilli(millis)

  override protected[time] def duration(millis: Long): TemporalAmount = Duration.ofMillis(millis)

  override protected[time] def millis(duration: TemporalAmount): Long = {
    // some durations don't support MILLIS, but all support NANOS
    duration.get(ChronoUnit.NANOS) / 1000
  }

  override protected[time] def millis(datetime: Instant)
    (implicit params: JavaTimeParams): Long = datetime.toEpochMilli

  override protected[time] def addToCeil(
    datetime: Instant,
    duration: TemporalAmount
  )(implicit params: JavaTimeParams): Instant = {
    try datetime plus duration
    catch {
      case ex: ArithmeticException => Instant.MAX
    }
  }

  override protected[time] def subtractToFloor(
    datetime: Instant,
    duration: TemporalAmount
  )(implicit params: JavaTimeParams): Instant = {
    try datetime minus duration
    catch {
      case ex: ArithmeticException => Instant.MIN
    }
  }

  override def defaultParams: JavaTimeParams = JavaTimeParams.isoUTC
}
