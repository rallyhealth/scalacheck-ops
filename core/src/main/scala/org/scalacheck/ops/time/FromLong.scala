package org.scalacheck.ops.time

import org.scalacheck.Gen

private[time] trait FromLong {
  self: AbstractTimeGenerators =>

  protected[time] def now(implicit params: ParamsType): InstantType = asInstant(System.currentTimeMillis())

  protected[time] def asInstant(millis: Long)(implicit params: ParamsType): InstantType

  protected[time] def asDuration(millis: Long): DurationType

  protected[time] def asLong(duration: DurationType): Long

  protected[time] def asLong(datetime: InstantType)(implicit params: ParamsType): Long

  override def between(start: InstantType, end: InstantType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    for {
      scalar <- Gen.choose(asLong(start), asLong(end))
    } yield asInstant(scalar)
  }
}
