package org.scalacheck.ops.time.joda

import org.joda.time.LocalDateTime
import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.GenericDateTimeGeneratorsSpec

import scala.reflect.ClassTag

class LocalDateTimeGeneratorsSpec extends GenericDateTimeGeneratorsSpec(LocalDateTimeGenerators, "LocalDateTimeGenerators") {
  override protected val arbDateTimeType: Arbitrary[LocalDateTime] = implicitly
  override protected val clsTagDateTimeType: ClassTag[LocalDateTime] = implicitly
}
