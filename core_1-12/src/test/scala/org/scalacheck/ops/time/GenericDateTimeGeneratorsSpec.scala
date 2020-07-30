package org.scalacheck.ops.time

import org.scalacheck.Arbitrary
import org.scalacheck.ops._
import org.scalatest.FlatSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalatest.Matchers._
import scala.reflect.ClassTag

private[time] abstract class GenericDateTimeGeneratorsSpec[Generators <: AbstractTimeGenerators](
  protected val gen: Generators
)(implicit classTag: ClassTag[Generators]) extends FlatSpec
  with GeneratorDrivenPropertyChecks {

  protected val genClassName: String = classTag.runtimeClass.getSimpleName.stripSuffix("$")
  protected def arbInstantType: Arbitrary[gen.InstantType]
  protected def clsTagInstantType: ClassTag[gen.InstantType]
  protected def orderingInstantType: Ordering[gen.InstantType]

  // set the local implicits
  private implicit def localArbDateTimeType: Arbitrary[gen.InstantType] = arbInstantType
  private implicit def localClsTagDateTimeType: ClassTag[gen.InstantType] = clsTagInstantType
  private implicit def localParams: gen.ParamsType = gen.defaultParams
  private implicit def localOrderingInstantType: Ordering[gen.InstantType] = orderingInstantType

  behavior of s"$genClassName.before"

  it should "not generate errors" in {
    forAll() { start: gen.InstantType =>
      val sampleIter = gen.before(start).sampleIterator
      val samples = sampleIter.take(10).toSeq
      assert(samples.forall(_.isDefined))
    }
  }

  it should s"always generate $genClassName instances less than the given instant" in {
    forAll() { start: gen.InstantType =>
      forAll(gen.before(start)) { before: gen.InstantType =>
        before should be <= start
      }
    }
  }

  behavior of s"$genClassName.after"

  it should "not generate errors" in {
    forAll() { start: gen.InstantType =>
      val sampleIter = gen.after(start).sampleIterator
      val samples = sampleIter.take(10).toSeq
      assert(samples.forall(_.isDefined))
    }
  }

  it should s"always generate $genClassName instances greater than the given instant" in {
    forAll() { start: gen.InstantType =>
      forAll(gen.after(start)) { after: gen.InstantType =>
        after should be >= start
      }
    }
  }

  behavior of s"$genClassName.around"

  it should "not generate errors" in {
    forAll() { start: gen.InstantType =>
      val sampleIter = gen.around(start).sampleIterator
      val samples = sampleIter.take(10).toSeq
      assert(samples.forall(_.isDefined))
    }
  }

  it should s"always generate $genClassName that are within the given range of the given time" in {
    forAll() { start: gen.InstantType =>
      forAll(gen.around(start, gen.defaultRange)) { around =>
        around should (
          be >= gen.subtractToFloor(start, gen.defaultRange) and
          be <= gen.addToCeil(start, gen.defaultRange)
        )
      }
    }
  }
}
