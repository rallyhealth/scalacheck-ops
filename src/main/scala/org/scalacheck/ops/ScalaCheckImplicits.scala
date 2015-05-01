package org.scalacheck.ops

import org.scalacheck.Gen

import scala.language.implicitConversions
import scala.reflect.ClassTag

trait ScalaCheckImplicits extends ArbitraryAsGen {

  implicit def genToGenOps[T](gen: Gen[T]): GenOps[T] = new GenOps(gen)

  implicit def genObjectToGenOps(gen: Gen.type): GenOps.type = GenOps

  implicit def genToGenOrThrow[T: ClassTag](generator: Gen[T]): GenOrThrow[T] = new GenOrThrow[T](generator)

}
