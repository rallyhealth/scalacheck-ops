package org.scalacheck.ops.time

import java.time.LocalDate

import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.ImplicitJavaTimeGenerators._

import scala.reflect.ClassTag

final class JavaLocalDateGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JavaLocalDateGenerators) {
  override protected val arbInstantType: Arbitrary[LocalDate] = implicitly[Arbitrary[LocalDate]]
  override protected val clsTagInstantType: ClassTag[LocalDate] = implicitly[ClassTag[LocalDate]]
  override protected val orderingInstantType: Ordering[LocalDate] = Ordering.fromLessThan(_.compareTo(_) < 0)
}
