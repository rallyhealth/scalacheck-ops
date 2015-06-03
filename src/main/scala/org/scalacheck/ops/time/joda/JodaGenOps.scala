package org.scalacheck.ops.time.joda

import org.scalacheck.Gen

import scala.language.implicitConversions

object JodaGenOps {

  def datetimeLocal: LocalDateTimeGenerators = LocalDateTimeGenerators

  def datetime: DateTimeGenerators = DateTimeGenerators
}

trait ImplicitJodaGenOps {

  implicit def toGenDateTimeOps(gen: Gen.type): JodaGenOps.type = JodaGenOps
}
