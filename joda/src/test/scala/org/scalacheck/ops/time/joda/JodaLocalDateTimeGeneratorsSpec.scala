package org.scalacheck.ops.time.joda

import org.joda.time.LocalDateTime
import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.GenericDateTimeGeneratorsSpec

import scala.reflect.ClassTag

final class JodaLocalDateTimeGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JodaLocalDateTimeGenerators) {
  override protected val arbInstantType: Arbitrary[LocalDateTime] = implicitly[Arbitrary[LocalDateTime]]
  override protected val clsTagInstantType: ClassTag[LocalDateTime] = implicitly[ClassTag[LocalDateTime]]
  override protected val orderingInstantType: Ordering[LocalDateTime] = Ordering.fromLessThan(_.compareTo(_) < 0)
}
