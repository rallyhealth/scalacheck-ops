package org.scalacheck.ops.time

import org.scalacheck.Gen

import scala.language.implicitConversions

object JavaTimeImplicits extends JavaTimeImplicits
trait JavaTimeImplicits {

  implicit def toJavaTimeGenOps(gen: Gen.type): JavaTimeGenOps.type = JavaTimeGenOps
}

object JavaTimeGenOps {

  def instant: JavaInstantGenerators = JavaInstantGenerators
}
