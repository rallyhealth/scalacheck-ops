package org.scalacheck.ops.time

import org.scalacheck.Gen

/**
 * A good set of default operations for any time library.
 */
trait GenericTimeGenerators {
  type InstantType
  type DurationType
  type ParamsType

  def defaultParams: ParamsType

  def defaultRange: DurationType

  def before(when: InstantType, maxRange: DurationType = defaultRange)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]

  def beforeNow(implicit params: ParamsType = defaultParams): Gen[InstantType]

  def beforeNowWithin(maxRange: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]

  def after(when: InstantType, maxRange: DurationType = defaultRange)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]

  def afterNow(implicit params: ParamsType = defaultParams): Gen[InstantType]

  def afterNowWithin(maxRange: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]

  def around(when: InstantType, plusOrMinus: DurationType = defaultRange)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]

  def aroundNow(implicit params: ParamsType = defaultParams): Gen[InstantType]

  def aroundNowWithin(plusOrMinus: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]

  def between(start: InstantType, end: InstantType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType]
}
