package org.scalacheck.ops

import java.util.UUID

import org.scalacheck.Gen
import org.scalatest.FreeSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class GenOrThrowSpec extends FreeSpec with GeneratorDrivenPropertyChecks with ScalaCheckImplicits {

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

  def throwsAnErrorWhenFiltered(
    methodName: String,
    methodCall: MethodCall[Char],
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
      val ex = intercept[EmptyGenSampleException[UUID]] {
        methodCall(gen)
      }
      assertResult(retries)(ex.attempts)
    }
  }

  def doesNotThrowAnErrorWhenFiltered(
    methodName: String,
    methodCall: MethodCall[Char],
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
      val result = methodCall(gen)
      assert(result != null)
    }
  }

  generatesUniqueRandomValues("getOrThrow", _.getOrThrow)
  throwsAnErrorWhenFiltered(
    "getOrThrow",
    _.getOrThrow,
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("randomOrThrow()", _.randomOrThrow())
  throwsAnErrorWhenFiltered(
    "randomOrThrow()",
    _.randomOrThrow(),
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("tryGet.get", _.tryGet.get)
  throwsAnErrorWhenFiltered(
    "tryGet.get",
    _.tryGet.get,
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("sampleIterator.next()", _.sampleIterator.next())
  doesNotThrowAnErrorWhenFiltered(
    "sampleIterator.next()",
    _.sampleIterator.next(),
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("toUnboundedIterator.next()", _.toUnboundedIterator.next())
  doesNotThrowAnErrorWhenFiltered(
    "toUnboundedIterator.next()",
    _.toUnboundedIterator.next(),
    minRetryLimit = 100
  )

  generatesUniqueRandomValues("toIterator.next()", _.toIterator.next())
  generatesUniqueRandomValues("toIterator.take(3).toSeq", _.toIterator.take(3).toSeq)
  throwsAnErrorWhenFiltered(
    "toIterator.next()",
    _.toIterator.next(),
    minRetryLimit = 100
  )

}
