package org.scalacheck.ops

import org.scalacheck.{Arbitrary, Gen}

import scala.language.implicitConversions

trait ArbitraryAsGen {

  // $COVERAGE-OFF$ This implicit is never picked up as verified by the unit tests
  @deprecated("This doesn't work and will be removed in the next major version", "2.3.0")
  implicit def arbitraryGen[T](implicit arb: Arbitrary[T]): Gen[T] = arb.arbitrary
  // $COVERAGE-ON$

  /** Converts a [[Gen]] to an [[Arbitrary]] so that you can pass a [[Gen]] anywhere
    * that requires an [[Arbitrary]].
    *
    * This seems like a good default:
    *
    * {{{
    *   implicit val arbThing: Arbitrary[T] = genThing
    * }}}
    *
    * @note this does not implicitly convert implicit [[Gen]]s into [[Arbitrary]],
    *       (1) this would undermine the purpose of having a separate [[Arbitrary]] type
    *       (2) making a [[Gen]] implicit is not a standard pattern anyway
    */
  implicit def genArbitrary[T](gen: Gen[T]): Arbitrary[T] = Arbitrary(gen)
}
