package org.scalacheck.ops.time

import java.time.LocalDateTime

import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.ImplicitJavaTimeGenerators._

import scala.reflect.ClassTag

final class JavaLocalDateTimeGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JavaLocalDateTimeGenerators) {
  override protected val arbInstantType: Arbitrary[LocalDateTime] = implicitly[Arbitrary[LocalDateTime]]
  override protected val clsTagInstantType: ClassTag[LocalDateTime] = implicitly[ClassTag[LocalDateTime]]
  override protected val orderingInstantType: Ordering[LocalDateTime] = Ordering.fromLessThan(_.compareTo(_) < 0)
}
