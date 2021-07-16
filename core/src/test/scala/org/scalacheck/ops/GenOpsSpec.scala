package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks._

import scala.util.Try

class GenOpsSpec extends AnyFlatSpec {

  private def genDigits: Gen[Int] = Gen.choose(0, 100)

  behavior of "Gen.set"

  it should "generate samples less than its sample size" in {
    forAll(Gen.setOf(Gen.choose(0, 1))) { setOfDigits =>
      assert(setOfDigits.size <= 2)
    }
  }

  it should "generate the same samples when called twice with the same seed" in {
    val gen = Gen.setOf(Gen.choose(0, 1))
    val seed = Seed(1)
    implicit val gc: GenConfig = GenConfig.default.withSeed(seed)
    val a = gen.head
    val b = gen.head
    assert(a == b)
  }

  behavior of "Gen.collect"

  it should "use suchThat when retryUntilMatch is false" in {
    val collectEven = Gen.collect(Gen.chooseNum(1, 10), retryUntilMatch = false) {
      case x if x % 2 == 0 => x
    }
    val examples = collectEven.sampleIterator.take(100).toSeq
    examples should contain (None)
    assert(examples forall (_ forall (_ % 2 == 0)))
  }

  it should "use retryUntil when retryUntilMatch is true" in {
    val collectEven = Gen.collect(Gen.chooseNum(1, 10), retryUntilMatch = true) {
      case x if x % 2 == 0 => x
    }
    val examples = collectEven.sampleIterator.take(100).toSeq
    examples should not contain None
    assert(examples forall (_ exists (_ % 2 == 0)))
  }

  it should "avoid infinite recursion by default" in {
    val collectNegative = Gen.collect(Gen.chooseNum(1, 10)) {
      case x if x < 0 => x
    }
    an [GenExceededRetryLimit] shouldBe thrownBy {
      collectNegative.nextRandom()
    }
  }

  behavior of "Gen.setOfN"

  it should "be able to generate a certain size" in {
    val n = 10
    forAll(Gen.setOfN(n, genDigits)) { digits =>
      assertResult(n)(digits.size)
    }
  }

  it should "generate the same samples when called twice with the same seed" in {
    val n = 10
    val gen = Gen.setOfN(n, genDigits)
    val seed = Seed(n)
    implicit val gc: GenConfig = GenConfig.default.withSeed(seed)
    val a = gen.head
    val b = gen.head
    assert(a == b)
  }

  private val genOnesIter: Gen[Iterator[Int]] = {
    Gen.const(Gen.oneOf(0, 1)).flatMap(_.filter(Set(1)).iterator)
  }

  it should "(when filtered) always generate the same number of samples" in {
    forAll(genOnesIter) { iter =>
      val s = Stream continually { Try(iter.next()) }
      val successes = s take 100 count (_.isSuccess)
      assert(successes == 100)
      val sumOfOnes = (s take 100 map (_.get)).sum
      assert(sumOfOnes == 100)
    }
  }

  it should "(when filtered) be able to convert to a stream" in {
    forAll(genOnesIter) { iter =>
      val values = iter.toStream take 200  // should not throw an exception
      val sumOfOnes = values.sum
      assert(sumOfOnes == 200)
    }
  }

  behavior of "Gen.enumValue"

  it should "create a generator from an Enumeration" in {
    forAll(Gen.enumValue(TestValues)) { enum =>
      // Should be of the expected type
      assert(enum == TestValues.withName(enum.toString))
    }
  }
}

object TestValues extends Enumeration {
  final val a = Value("a")
  final val b = Value("b")
  final val c = Value("c")
  final val d = Value("d")
  final val e = Value("e")
  final val f = Value("f")
  final val g = Value("g")
}
