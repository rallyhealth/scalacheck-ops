package org.scalacheck.ops.time

import java.time.{Clock, Duration, LocalTime}

import org.scalacheck.Gen

trait JavaLocalTimeGenerators extends AbstractTimeGenerators {
  override type InstantType = LocalTime
  override type DurationType = Duration
  override type ParamsType = Clock

  override val defaultParams: Clock = Clock.systemUTC()

  override val defaultRange: Duration = Duration.ofHours(24)

  override protected[time] def now(implicit params: Clock): LocalTime = LocalTime.now(params)

  override def between(start: LocalTime, end: LocalTime)(implicit params: Clock): Gen[LocalTime] = {
    for {
      nanoOfDay <- Gen.choose(start.toNanoOfDay, end.toNanoOfDay)
    } yield LocalTime.ofNanoOfDay(nanoOfDay)
  }

  override protected[time] def addToCeil(instant: LocalTime, duration: Duration)
    (implicit params: Clock): LocalTime = {
    instant plus duration
  }

  override protected[time] def subtractToFloor(instant: LocalTime, duration: Duration)
    (implicit params: Clock): LocalTime = {
    instant minus duration
  }
}

object JavaLocalTimeGenerators extends JavaLocalTimeGenerators {

  final val MAX_NANOS = 999999999
}
