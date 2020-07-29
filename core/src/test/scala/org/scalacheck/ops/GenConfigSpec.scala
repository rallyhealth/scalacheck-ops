package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalactic.anyvals.PosZInt
import org.scalatest.FreeSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks._

class GenConfigSpec extends FreeSpec {

  private val it = classOf[GenConfig]

  s"$it should use the given number of retries and default seed" in {
    val p = Gen.Parameters.default
    val retries = 5
    val c = GenConfig(p, retries)
    assertResult(retries)(c.retries)
    assertResult(GenConfig.default.seed)(c.seed)
  }

  s"$it should use the default number of retries and default seed" in {
    val p = Gen.Parameters.default
    val c = GenConfig(p)
    assertResult(GenConfig.default.retries)(c.retries)
    assertResult(GenConfig.default.seed)(c.seed)
  }

  s"$it should use the default number of retries and the given seed and maxSize" in {
    val s = Seed(5)
    val maxSize = 10
    val c = GenConfig(s, 10)
    assertResult(GenConfig.default.retries)(c.retries)
    assertResult(s)(c.seed)
    assertResult(maxSize)(c.params.size)
  }

  s"$it should use the default number of retries and maxSize and the given seed" in {
    val s = Seed(10)
    val c = GenConfig(s)
    assertResult(GenConfig.default.retries)(c.retries)
    assertResult(s)(c.seed)
    assertResult(GenConfig.default.params.size)(c.params.size)
  }

  s"$it should keep the same number of retries when updating the params" in {
    val initC = GenConfig.default
    val initP = initC.params
    val newP = Gen.Parameters.default
    assume(newP.size != initP.size)
    val newC = initC.withParams(newP)
    assertResult(initC.retries)(newC.retries)
  }

  s"$it should use the size config from given parameters" in {
    val rangeStart: PosZInt = 3
    val rangeWidth: PosZInt = 4
    val sizeMax = rangeStart + rangeWidth
    val paramSizeRange = rangeStart to sizeMax
    val gen = Gen.parameterized { p =>
      val c = GenConfig(p)
      assert(paramSizeRange contains c.params.size)
      Gen.listOf(Gen.numChar)
    }
    forAll(gen, minSize(rangeStart), sizeRange(rangeWidth)) { cs =>
      assert(cs.size <= sizeMax)
    }
  }
}
