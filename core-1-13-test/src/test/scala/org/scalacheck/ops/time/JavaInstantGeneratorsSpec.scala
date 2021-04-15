package org.scalacheck.ops.time

import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.ImplicitJavaTimeGenerators._

import java.time.Instant
import scala.reflect.ClassTag

final class JavaInstantGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JavaInstantGenerators) {
  override protected val arbInstantType: Arbitrary[Instant] = implicitly[Arbitrary[Instant]]
  override protected val clsTagInstantType: ClassTag[Instant] = implicitly[ClassTag[Instant]]
  override protected val orderingInstantType: Ordering[Instant] = implicitly[Ordering[Instant]]
}
