package org.scalacheck.ops.time

import java.time.{Clock, Duration, Instant}

import org.scalacheck.Gen

object JavaInstantGenerators extends JavaInstantGenerators
trait JavaInstantGenerators extends AbstractTimeGenerators {
  override type InstantType = Instant
  override type DurationType = Duration
  override type ParamsType = Clock

  override val defaultRange: Duration = Duration.ofDays(365)

  override val defaultParams: Clock = Clock.systemUTC()

  override protected[time] def now(implicit clock: Clock): Instant = Instant.now(clock)

  import JavaLocalTimeGenerators.MAX_NANOS

  override def between(start: Instant, end: Instant)(implicit params: Clock): Gen[Instant] = {
    val startSeconds = start.getEpochSecond
    val endSeconds = end.getEpochSecond
    if (startSeconds == endSeconds) {
      for {
        nanos <- Gen.choose(start.getNano, end.getNano)
      } yield Instant.ofEpochSecond(startSeconds, nanos)
    }
    else {
      for {
        seconds <- Gen.choose(startSeconds, endSeconds)
        nanos <- seconds match {
          case `startSeconds` =>
            Gen.choose(start.getNano, MAX_NANOS)
          case `endSeconds` =>
            Gen.choose(0, end.getNano)
          case _ =>
            Gen.choose(0, MAX_NANOS)
        }
      } yield Instant.ofEpochSecond(seconds, nanos)
    }
  }

  override protected[time] def addToCeil(
    instant: Instant,
    duration: Duration
  )(implicit params: Clock): Instant = {
    try instant plus duration
    catch {
      case ex: ArithmeticException => Instant.MAX
    }
  }

  override protected[time] def subtractToFloor(
    instant: Instant,
    duration: Duration
  )(implicit params: Clock): Instant = {
    try instant minus duration
    catch {
      case ex: ArithmeticException => Instant.MIN
    }
  }
}
