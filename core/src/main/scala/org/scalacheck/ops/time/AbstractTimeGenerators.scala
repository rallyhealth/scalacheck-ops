package org.scalacheck.ops.time

import org.scalacheck.Gen

private[time] trait AbstractTimeGenerators extends GenericTimeGenerators {

  protected[time] def now(implicit params: ParamsType): InstantType

  protected[time] def addToCeil(instant: InstantType, duration: DurationType)(implicit params: ParamsType): InstantType

  protected[time] def subtractToFloor(instant: InstantType, duration: DurationType)(implicit params: ParamsType): InstantType

  override def before(when: InstantType, maxRange: DurationType = defaultRange)
    (implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    between(subtractToFloor(when, maxRange), when)
  }

  override def beforeNow(implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    before(now)
  }

  override def beforeNowWithin(maxRange: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    before(now, maxRange)
  }

  override def after(when: InstantType, maxRange: DurationType = defaultRange)
    (implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    between(when, addToCeil(when, maxRange))
  }

  override def afterNow(implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    after(now)
  }

  override def afterNowWithin(maxRange: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    after(now, maxRange)
  }

  override def around(when: InstantType, plusOrMinus: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    between(subtractToFloor(when, plusOrMinus), addToCeil(when, plusOrMinus))
  }

  override def aroundNow(implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    around(now, defaultRange)
  }

  override def aroundNowWithin(plusOrMinus: DurationType)
    (implicit params: ParamsType = defaultParams): Gen[InstantType] = {
    around(now, plusOrMinus)
  }

}
