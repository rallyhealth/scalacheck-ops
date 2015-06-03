package org.scalacheck.ops.time

import org.scalacheck.Gen

/**
 * A good set of default operations
 */
trait GenericDateTimeGenerators {
  type DateTimeType
  type DurationType
  type ParamsType

  def defaultParams: ParamsType

  def defaultRange: DurationType

  def before(when: DateTimeType, maxRange: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def beforeNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def beforeNowWithin(maxRange: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def after(when: DateTimeType, maxRange: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def afterNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def afterNowWithin(maxRange: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def around(when: DateTimeType, plusOrMinus: DurationType = defaultRange)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def aroundNow(implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def aroundNowWithin(plusOrMinus: DurationType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]

  def between(start: DateTimeType, end: DateTimeType)
    (implicit dateTimeParams: ParamsType = defaultParams): Gen[DateTimeType]
}
