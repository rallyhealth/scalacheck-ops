package org.scalacheck.ops.time

import java.time.LocalTime

import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.ImplicitJavaTimeGenerators._

import scala.reflect.ClassTag

final class JavaLocalTimeGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JavaLocalTimeGenerators) {
  override protected val arbInstantType: Arbitrary[LocalTime] = implicitly[Arbitrary[LocalTime]]
  override protected val clsTagInstantType: ClassTag[LocalTime] = implicitly[ClassTag[LocalTime]]
  override protected val orderingInstantType: Ordering[LocalTime] = Ordering.fromLessThan(_.compareTo(_) < 0)
}
