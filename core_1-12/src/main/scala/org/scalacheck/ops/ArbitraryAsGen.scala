package org.scalacheck.ops

import org.scalacheck.{Arbitrary, Gen}

import scala.language.implicitConversions

trait ArbitraryAsGen {

  // $COVERAGE-OFF$ This implicit is never picked up as verified by the unit tests
  @deprecated("This doesn't work and will be removed in the next major version", "2.3.0")
  implicit def arbitraryGen[T](implicit arb: Arbitrary[T]): Gen[T] = arb.arbitrary
  // $COVERAGE-ON$

  /**
   * Converts a [[Gen]] to an [[Arbitrary]] so that you can pass a [[Gen]] anywhere
   * that requires implicit [[Arbitrary]] as arguments.
   *
   * @note this does not convert implicit [[Gen]]s, since making a [[Gen]] implicit
   *       is not a standard pattern, and it would be easy and more obvious to just do
   *       {{{
   *         implicit val arbThing = Arbitrary(genThing)
   *       }}}
   */
  implicit def genArbitrary[T](gen: Gen[T]): Arbitrary[T] = Arbitrary(gen)
}
