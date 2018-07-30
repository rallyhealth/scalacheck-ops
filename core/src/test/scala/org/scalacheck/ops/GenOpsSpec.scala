package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class GenOpsSpec extends FlatSpec
with GeneratorDrivenPropertyChecks
with Matchers
with ScalaCheckImplicits {

  private def genDigits: Gen[Int] = Gen.choose(0, 100)

  "Gen.set" should "generate samples less than its sample size" in {
    forAll(Gen.setOf(Gen.choose(0, 1))) { setOfDigits =>
      assert(setOfDigits.size <= 2)
    }
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
    an [EmptyGenSampleException[Int]] shouldBe thrownBy {
      collectNegative.getOrThrow
    }
  }

  behavior of "Gen.setOfN"

  it should "be able to generate a certain size" in {
    val n = 10
    forAll(Gen.setOfN(n, genDigits)) { digits =>
      assert(digits.size == n)
    }
  }

  behavior of "Gen.toIterator"

  it should "generate unique samples on each iteration" in {
    val gen = Gen.choose(Long.MinValue, Long.MaxValue)
    val longSet = gen.toIterator.take(100).toSet
    // the odds of this failing are incredibly slim
    assert(longSet.size > 1)
  }

  it should "fail with the right exception if an impassable filter is added to the generator" in {
    val impossible = Gen.const(true).filter(_ => false)
    an[EmptyGenSampleException[Boolean]] shouldBe thrownBy {
      impossible.toIterator.take(1).toList
    }
  }

  private def genOnesIter: Gen[Iterator[Int]] =
    Gen.const(Gen.oneOf(0, 1)).flatMap(_.filter(Set(1)).toIterator)

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
  val a = Value("a")
  val b = Value("b")
  val c = Value("c")
  val d = Value("d")
  val e = Value("e")
  val f = Value("f")
  val g = Value("g")
}