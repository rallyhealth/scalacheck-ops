package org.scalacheck.ops.time

import org.scalacheck.Gen

import scala.concurrent.duration._

private[time] trait AbstractDateTimeGenerators extends GenericDateTimeGenerators {

  override val defaultRange: DurationType = duration(365.days.toMillis)

  protected[time] def now(implicit params: ParamsType = defaultParams): DateTimeType = datetime(System.currentTimeMillis())

  protected[time] def datetime(millis: Long)(implicit params: ParamsType): DateTimeType

  protected[time] def duration(millis: Long): DurationType

  protected[time] def millis(duration: DurationType): Long

  protected[time] def millis(datetime: DateTimeType)(implicit params: ParamsType): Long

  protected[time] def addToCeil(datetime: DateTimeType, duration: DurationType)(implicit params: ParamsType): DateTimeType

  protected[time] def subtractToFloor(datetime: DateTimeType, duration: DurationType)(implicit params: ParamsType): DateTimeType

  override def before(when: DateTimeType, maxRange: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    between(subtractToFloor(when, maxRange), when)
  }

  override def beforeNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    before(now)
  }

  override def beforeNowWithin(maxRange: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    before(now, maxRange)
  }

  override def after(when: DateTimeType, maxRange: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    between(when, addToCeil(when, maxRange))
  }

  override def afterNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    after(now)
  }

  override def afterNowWithin(maxRange: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    after(now, maxRange)
  }

  override def around(when: DateTimeType, plusOrMinus: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    between(subtractToFloor(when, plusOrMinus), addToCeil(when, plusOrMinus))
  }

  override def aroundNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    around(now, defaultRange)
  }

  override def aroundNowWithin(plusOrMinus: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    around(now, plusOrMinus)
  }

  override def between(start: DateTimeType, end: DateTimeType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType] = {
    for {
      millis <- Gen.choose(millis(start), millis(end))
    } yield datetime(millis)
  }
}
