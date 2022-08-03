package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class GenOpsSpec extends FlatSpec with Matchers with ScalaCheckImplicits {

  private def genDigits: Gen[Int] = Gen.choose(0, 100)

  behavior.of("Gen.collect")

  it should "use suchThat when retryUntilMatch is false" in {
    val collectEven = Gen.collect(Gen.chooseNum(1, 10), retryUntilMatch = false) {
      case x if x % 2 == 0 => x
    }
    val examples = collectEven.sampleIterator.take(100).toSeq
    examples should contain(None)
    assert(examples.forall(_.forall(_ % 2 == 0)))
  }

  it should "use retryUntil when retryUntilMatch is true" in {
    val collectEven = Gen.collect(Gen.chooseNum(1, 10), retryUntilMatch = true) {
      case x if x % 2 == 0 => x
    }
    val examples = collectEven.sampleIterator.take(100).toSeq
    examples should not contain None
    assert(examples.forall(_.exists(_ % 2 == 0)))
  }

  it should "avoid infinite recursion by default" in {
    val collectNegative = Gen.collect(Gen.chooseNum(1, 10)) {
      case x if x < 0 => x
    }
    an[EmptyGenSampleException[Int]] shouldBe thrownBy {
      collectNegative.randomOrThrow()
    }
  }

  behavior.of("Gen.of[Stream]")

  it should "generate samples less than the default size" in {
    forAll(Gen.of[Stream](Gen.choose(0, 1))) { digits =>
      assert(digits.size <= Gen.Parameters.default.size)
    }
  }

  it should "be able to generate a certain size" in {
    val n = 10
    forAll(Gen.of[Stream](n, genDigits)) { digits =>
      assertResult(n)(digits.size)
    }
  }

  behavior.of("Gen.set")

  it should "generate samples less than its sample size" in {
    forAll(Gen.setOf(Gen.choose(0, 1))) { digits =>
      assert(digits.size <= 2)
    }
  }

  behavior.of("Gen.setOfN")

  it should "be able to generate a certain size" in {
    val n = 10
    forAll(Gen.setOfN(n, genDigits)) { digits =>
      assertResult(n)(digits.size)
    }
  }

  behavior.of("Gen.vectorOf")

  it should "generate samples less than the default size" in {
    forAll(Gen.vectorOf(Gen.choose(0, 1))) { digits =>
      assert(digits.size <= Gen.Parameters.default.size)
    }
  }

  behavior.of("Gen.vectorOfN")

  it should "be able to generate a certain size" in {
    val n = 10
    forAll(Gen.vectorOfN(n, genDigits)) { digits =>
      assertResult(n)(digits.size)
    }
  }

  behavior.of("Gen[Iterator]")

  private val genOnesIter: Gen[Iterator[Int]] =
    Gen.const(Gen.oneOf(0, 1)).flatMap(_.filter(Set(1)).toUnboundedIterator)

  it should "(when filtered) always generate the same number of samples" in {
    forAll(genOnesIter) { iter =>
      val s = Stream.continually(Try(iter.next()))
      val successes = s.take(100).count(_.isSuccess)
      assert(successes == 100)
      val sumOfOnes = s.take(100).map(_.get).sum
      assert(sumOfOnes == 100)
    }
  }

  it should "(when filtered) be able to convert to a stream" in {
    forAll(genOnesIter) { iter =>
      val values = iter.toStream.take(200) // should not throw an exception
      val sumOfOnes = values.sum
      assert(sumOfOnes == 200)
    }
  }

  behavior.of("Gen.enumValue")

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
