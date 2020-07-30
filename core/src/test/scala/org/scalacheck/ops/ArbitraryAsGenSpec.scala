package org.scalacheck.ops

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FreeSpec

class ArbitraryAsGenSpec extends FreeSpec {

  private val it = classOf[ArbitraryAsGen].getSimpleName

  s"$it should implicitly convert a Gen to Arbitrary" in {
    def f(arb: Arbitrary[Char]): Unit = ()
    f(Gen.numChar)
  }

  private def implicitGen(implicit gen: Gen[Char]): Unit = ()

  s"$it should convert an implicit Arbitrary to an implicit Gen" in {
    pendingUntilFixed {
      assertCompiles("implicitGen")
    }
  }
}
