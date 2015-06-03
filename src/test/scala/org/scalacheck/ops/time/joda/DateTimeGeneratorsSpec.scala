package org.scalacheck.ops.time.joda

import org.joda.time.DateTime
import org.scalacheck.Arbitrary
import org.scalacheck.ops.time.GenericDateTimeGeneratorsSpec

import scala.reflect.ClassTag

class DateTimeGeneratorsSpec extends GenericDateTimeGeneratorsSpec(DateTimeGenerators, "DateTimeGenerators") {
  override protected val arbDateTimeType: Arbitrary[DateTime] = implicitly
  override protected val clsTagDateTimeType: ClassTag[DateTime] = implicitly
}
