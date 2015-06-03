package org.scalacheck.ops.time

import org.scalacheck.Arbitrary
import org.scalacheck.ops._
import org.scalatest.FlatSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.reflect.ClassTag

abstract class GenericDateTimeGeneratorsSpec[Generators <: AbstractDateTimeGenerators](
  protected val gen: Generators,
  protected val genClassName: String
) extends FlatSpec
  with GeneratorDrivenPropertyChecks {

  protected def arbDateTimeType: Arbitrary[gen.DateTimeType]
  protected def clsTagDateTimeType: ClassTag[gen.DateTimeType]

  // set the local implicits
  private implicit def localArbDateTimeType = arbDateTimeType
  private implicit def localClsTagDateTimeType = clsTagDateTimeType
  private implicit def localParams = gen.defaultParams

  behavior of s"$genClassName.before"

  it should "not generate errors" in {
    forAll() { (start: gen.DateTimeType) =>
      val sampleIter = gen.before(start).sampleIterator
      val samples = sampleIter.take(10).toSeq
      assert(samples.forall(_.isDefined))
    }
  }

  it should "always generate DateTimes that are less than the given time" in {
    forAll() { (start: gen.DateTimeType) =>
      val startMillis = gen.millis(start)
      forAll() { (before: gen.DateTimeType) =>
        gen.millis(before) < startMillis
      }
    }
  }

  behavior of s"$genClassName.after"

  it should "not generate errors" in {
    forAll() { (start: gen.DateTimeType) =>
      val sampleIter = gen.after(start).sampleIterator
      val samples = sampleIter.take(10).toSeq
      assert(samples.forall(_.isDefined))
    }
  }

  it should "always generate DateTimes that are greater than the given time" in {
    forAll() { (start: gen.DateTimeType) =>
      val startMillis = gen.millis(start)
      forAll() { (after: gen.DateTimeType) =>
        gen.millis(after) > startMillis
      }
    }
  }

  behavior of s"$genClassName.around"

  it should "not generate errors" in {
    forAll() { (start: gen.DateTimeType) =>
      val sampleIter = gen.around(start).sampleIterator
      val samples = sampleIter.take(10).toSeq
      assert(samples.forall(_.isDefined))
    }
  }

  it should "always generate DateTimes that are within the given range of the given time" in {
    val defaultRangeMillis = gen.millis(gen.defaultRange)
    forAll() { (start: gen.DateTimeType) =>
      val startMillis = gen.millis(start)
      forAll(gen.around(start, gen.defaultRange)) { (around: gen.DateTimeType) =>
        val aroundMillis = gen.millis(around)
        aroundMillis > startMillis - defaultRangeMillis &&
          aroundMillis < startMillis + defaultRangeMillis
      }
    }
  }
}
