package org.scalacheck.ops.time

import java.time.Instant

import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.ImplicitJavaTimeGenerators._

import scala.reflect.ClassTag

class JavaInstantGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JavaTimeGenerators, "JavaInstantGenerators") {
  override protected val arbDateTimeType: Arbitrary[Instant] = implicitly[Arbitrary[Instant]]
  override protected val clsTagDateTimeType: ClassTag[Instant] = implicitly[ClassTag[Instant]]
}
