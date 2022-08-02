package org.scalacheck.ops

import java.util.UUID
import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalatest.FreeSpec

class GenOrThrowSpec extends FreeSpec with ScalaCheckImplicits {

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

  generatesUniqueRandomValues("getOrThrow", gen => new GenOrThrow(gen).getOrThrow)
  throwsAnErrorWhenFiltered(
    "getOrThrow",
    _ => gen => new GenOrThrow(gen).getOrThrow,
    minRetryLimit = 100
  )

  generatesTheSameValueWhenCalledTwice(
    "getOrThrow (given the same Seed)",
    gen => new GenOrThrow(gen).getOrThrow(Seed(0))
  )

  generatesTheSameValueWhenCalledTwice("getOrThrowPure", gen => new GenOrThrow(gen).getOrThrowPure)
  throwsAnErrorWhenFiltered(
    "getOrThrowPure",
    implicit c => _.getOrThrowPure
  )

  generatesUniqueRandomValues("randomOrThrow", gen => new GenOrThrow(gen).randomOrThrow())
  throwsAnErrorWhenFiltered(
    "randomOrThrow()",
    implicit c => gen => new GenOrThrow(gen).randomOrThrow()
  )

  generatesUniqueRandomValues("tryGet.get", gen => new GenOrThrow(gen).tryGet.get)
  throwsAnErrorWhenFiltered(
    "tryGet.get",
    _ => gen => new GenOrThrow(gen).tryGet.get,
    minRetryLimit = 100
  )

  generatesTheSameValueWhenCalledTwice("tryGet (given the same Seed)", gen => new GenOrThrow(gen).tryGet(Seed(0)).get)

  generatesUniqueRandomValues("sampleIterator.next()", gen => new GenOrThrow(gen).sampleIterator.next())
  doesNotThrowAnErrorWhenFiltered(
    "sampleIterator.next()",
    _ => gen => new GenOrThrow(gen).sampleIterator.next(),
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("toUnboundedIterator.next()", gen => new GenOrThrow(gen).toUnboundedIterator.next())
  doesNotThrowAnErrorWhenFiltered(
    "toUnboundedIterator.next()",
    _ => gen => new GenOrThrow(gen).toUnboundedIterator.next(),
    minRetryLimit = 100
  )

  generatesTheSameValueWhenCalledTwice("toIterator.next()", gen => new GenOrThrow(gen).toIterator.next())
  generatesTheSameValueWhenCalledTwice("toIterator.take(3).toSeq", gen => new GenOrThrow(gen).toIterator.take(3).toSeq)
  throwsAnErrorWhenFiltered(
    "toIterator.next()",
    _ => gen => new GenOrThrow(gen).toIterator.next(),
    minRetryLimit = 100
  )

  generatesTheSameValueWhenCalledTwice("iterator.next()", gen => new GenOrThrow(gen).iterator.next())
  generatesTheSameValueWhenCalledTwice("iterator.take(3).toSeq", gen => new GenOrThrow(gen).iterator.take(3).toSeq)
  throwsAnErrorWhenFiltered(
    "iterator.next()",
    implicit c => gen => new GenOrThrow(gen).iterator.next()
  )

}
