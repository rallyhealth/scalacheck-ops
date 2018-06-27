package org.scalacheck.ops.time.joda

import org.joda.time.DateTime
import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.GenericDateTimeGeneratorsSpec

import scala.reflect.ClassTag

final class JodaDateTimeGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JodaDateTimeGenerators) {
  override protected val arbInstantType: Arbitrary[DateTime] = implicitly[Arbitrary[DateTime]]
  override protected val clsTagInstantType: ClassTag[DateTime] = implicitly[ClassTag[DateTime]]
  override protected val orderingInstantType: Ordering[DateTime] = Ordering.fromLessThan[DateTime](_.compareTo(_) < 0)
}
