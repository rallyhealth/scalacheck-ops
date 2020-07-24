package org.scalacheck.ops.time

import org.scalacheck.Gen

import scala.language.implicitConversions

object JavaTimeImplicits extends JavaTimeImplicits
trait JavaTimeImplicits {

  implicit def toJavaTimeGenOps(gen: Gen.type): JavaTimeGenOps.type = JavaTimeGenOps
}

object JavaTimeGenOps {

  @deprecated("Use .javaInstant instead.", "2.0.0")
  def instant: JavaInstantGenerators = JavaInstantGenerators

  def javaInstant: JavaInstantGenerators = JavaInstantGenerators

  def javaLocalDate: JavaLocalDateGenerators = JavaLocalDateGenerators

  def javaLocalDateTime: JavaLocalDateTimeGenerators = JavaLocalDateTimeGenerators
}
