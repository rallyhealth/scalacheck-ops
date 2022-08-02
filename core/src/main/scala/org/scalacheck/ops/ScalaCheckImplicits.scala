package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalacheck.ops.time.{ImplicitJavaTimeGenerators, JavaTimeImplicits}

import scala.language.implicitConversions
import scala.reflect.ClassTag

// TODO: Move everything to the package object to prevent inheritance here. It is bad for binary compatibility.
@deprecated("Don't extend ScalaCheckImplicits. Import from org.scalacheck.ops._ instead.", "2.7.0")
trait ScalaCheckImplicits
  extends ArbitraryAsGen
  with JavaTimeImplicits
  with ImplicitJavaTimeGenerators
  with ShrinkLargeTuples {

  implicit def genToGenOps[T](gen: Gen[T]): GenOps[T] = new GenOps(gen)

  implicit def genObjectToGenOps(gen: Gen.type): GenOps.type = GenOps

  // No longer an implicit, but leaving this here for binary compatibility
  @deprecated("Use implicit conversion to GenFromConfig instead.", "2.7.0")
  def genToGenOrThrow[T : ClassTag](generator: Gen[T]): GenOrThrow[T] = new GenOrThrow[T](generator)

}
