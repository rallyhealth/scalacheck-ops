package org.scalacheck.ops

import java.util.UUID
import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalatest.freespec.AnyFreeSpec

class GenFromConfigSpec extends AnyFreeSpec {

  type MethodCall[A] = Gen[A] => Any

  def generatesUniqueRandomValues(
    methodName: String,
    methodCall: MethodCall[UUID]
  ): Unit = {
    s"gen.$methodName should return different values each time it is called" in {
      val gen = Gen.uuid
      val uuid1 = methodCall(gen)
      val uuid2 = methodCall(gen)
      assert(uuid1 != uuid2)
    }
  }

  def generatesTheSameValueWhenCalledTwice(
    methodName: String,
    methodCall: MethodCall[UUID]
  ): Unit = {
    s"gen.$methodName should return the same value when calling twice" in {
      val gen = Gen.uuid
      val uuid1 = methodCall(gen)
      val uuid2 = methodCall(gen)
      assert(uuid1 == uuid2)
    }
  }

  def throwsAnErrorWhenFiltered(
    methodName: String,
    methodCall: GenConfig => MethodCall[Char],
    minRetryLimit: Int = -1
  ): Unit = {
    val (aLimitedNumberOf, retries) = {
      if (minRetryLimit > -1)
        ("" + minRetryLimit, minRetryLimit)
      else
        ("the configured number of retries (3)", 3)
    }

    s"gen.$methodName should throw an exception after dropping $aLimitedNumberOf samples" in {
      var count = 0
      val gen = Gen.numChar.suchThat(_ => {
        count += 1
        count > retries + 1
      })
      val ex = intercept[GenExceededRetryLimit] {
        methodCall(GenConfig.default.withRetries(retries))(gen)
      }
      assertResult(retries)(ex.attempts)
    }
  }

  def doesNotThrowAnErrorWhenFiltered(
    methodName: String,
    methodCall: GenConfig => MethodCall[Char],
    minRetryLimit: Int
  ): Unit = {
    val (aLimitedNumberOf, retries) = {
      if (minRetryLimit > -1)
        ("" + minRetryLimit, minRetryLimit)
      else
        ("the configured number of retries (3)", 3)
    }

    s"gen.$methodName should NOT throw an exception after dropping $aLimitedNumberOf samples" in {
      var count = 0
      val gen = Gen.numChar.suchThat(_ => {
        count += 1
        count > retries + 1
      })
      val result = methodCall(GenConfig.default.withRetries(retries))(gen)
      assert(result != null)
    }
  }

  generatesTheSameValueWhenCalledTwice("head", _.head)
  throwsAnErrorWhenFiltered(
    "head",
    implicit c => _.head,
    minRetryLimit = 100
  )

  generatesTheSameValueWhenCalledTwice("valueFor(Seed(10))", _.valueFor(Seed(10)))
  throwsAnErrorWhenFiltered(
    "valueFor(Seed(10))",
    implicit c => _.valueFor(Seed(10)),
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("nextRandom()", _.nextRandom())
  throwsAnErrorWhenFiltered(
    "nextRandom()",
    implicit c => _.nextRandom()
  )

  // Coverage for deprecated method placeholders

  generatesUniqueRandomValues("getOrThrow", _.getOrThrow)
  throwsAnErrorWhenFiltered(
    "getOrThrow",
    _ => _.getOrThrow,
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("sampleIterator.next()", _.sampleIterator.next())
  doesNotThrowAnErrorWhenFiltered(
    "sampleIterator.next()",
    _ => _.sampleIterator.next(),
    minRetryLimit = 100
  )

  generatesTheSameValueWhenCalledTwice("iterator.next()", _.iterator.next())
  generatesTheSameValueWhenCalledTwice("iterator.take(3).toSeq", _.iterator.take(3).toSeq)
  throwsAnErrorWhenFiltered(
    "iterator.next()",
    implicit c => _.iterator.next()
  )
}
