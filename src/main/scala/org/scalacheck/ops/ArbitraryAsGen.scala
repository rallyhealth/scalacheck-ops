package org.scalacheck.ops

import org.scalacheck.{Arbitrary, Gen}

import scala.language.implicitConversions

trait ArbitraryAsGen {

  /**
   * Converts an org.scalacheck.Arbitrary to a org.scalacheck.Gen so that you can require
   * plain old Gens as parameters and allow the caller to pass arbitrary values.
   *
   * Honestly, I don't know why this isn't the default.
   */
  implicit def arbitraryGen[T](implicit arb: Arbitrary[T]): Gen[T] = arb.arbitrary

  /**
   * Converts a org.scalacheck.Gen to an org.scalacheck.Arbitrary so that you can pass a
   * Gen anywhere that requires implicit Arbitrary as arguments.
   *
   * @note this does not convert implicit Gens, since making a Gen implicit
   *       is not a standard pattern, and it would be easy and more obvious to just do
   *       {{{
   *         implicit val arbThing = Arbitrary(genThing)
   *       }}}
   */
  implicit def genArbitrary[T](gen: Gen[T]): Arbitrary[T] = Arbitrary(gen)
}
