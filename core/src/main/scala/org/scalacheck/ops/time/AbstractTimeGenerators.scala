package org.scalacheck.ops.time

import org.scalacheck.Gen

import scala.concurrent.duration._

private[time] trait AbstractTimeGenerators extends GenericTimeGenerators {

  override val defaultRange: DurationType = duration(365.days.toMillis)

  protected[time] def now(implicit params: ParamsType = defaultParams): InstantType = datetime(System.currentTimeMillis())

  protected[time] def datetime(millis: Long)(implicit params: ParamsType): InstantType

  protected[time] def duration(millis: Long): DurationType

  protected[time] def millis(duration: DurationType): Long

  protected[time] def millis(datetime: InstantType)(implicit params: ParamsType): Long

  protected[time] def addToCeil(datetime: InstantType, duration: DurationType)(implicit params: ParamsType): InstantType

  protected[time] def subtractToFloor(datetime: InstantType, duration: DurationType)(implicit params: ParamsType): InstantType

  override def before(when: InstantType, maxRange: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    between(subtractToFloor(when, maxRange), when)
  }

  override def beforeNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    before(now)
  }

  override def beforeNowWithin(maxRange: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    before(now, maxRange)
  }

  override def after(when: InstantType, maxRange: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    between(when, addToCeil(when, maxRange))
  }

  override def afterNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    after(now)
  }

  override def afterNowWithin(maxRange: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    after(now, maxRange)
  }

  override def around(when: InstantType, plusOrMinus: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    between(subtractToFloor(when, plusOrMinus), addToCeil(when, plusOrMinus))
  }

  override def aroundNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    around(now, defaultRange)
  }

  override def aroundNowWithin(plusOrMinus: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    around(now, plusOrMinus)
  }

  override def between(start: InstantType, end: InstantType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[InstantType] = {
    for {
      millis <- Gen.choose(millis(start), millis(end))
    } yield datetime(millis)
  }
}
