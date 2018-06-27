package org.scalacheck.ops.time.joda

import org.scalacheck.Gen

import scala.language.implicitConversions

object JodaGenOps {

  def jodaLocalDate: JodaLocalDateGenerators = JodaLocalDateGenerators

  @deprecated("Use .jodaLocalDateTime instead.", "2.0.0")
  def datetimeLocal: JodaLocalDateTimeGenerators = JodaLocalDateTimeGenerators

  def jodaLocalDateTime: JodaLocalDateTimeGenerators = JodaLocalDateTimeGenerators

  @deprecated("Use .jodaDateTime instead.", "2.0.0")
  def datetime: JodaDateTimeGenerators = JodaDateTimeGenerators

  def jodaDateTime: JodaDateTimeGenerators = JodaDateTimeGenerators
}

trait ImplicitJodaGenOps {

  implicit def toGenDateTimeOps(gen: Gen.type): JodaGenOps.type = JodaGenOps
}
