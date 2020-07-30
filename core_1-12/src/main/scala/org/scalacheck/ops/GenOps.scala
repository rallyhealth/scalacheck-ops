package org.scalacheck.ops

import org.scalacheck.{Arbitrary, Gen}

import scala.collection.{BitSet, mutable}
import scala.reflect.{ClassTag, classTag}

object GenOps {

  @deprecated("This method will be removed in the next major version.", "2.3.0")
  def binary: Gen[Int] = Gen.oneOf(0, 1)

  def bits(size: Int): Gen[BitSet] = Gen.listOfN(size, binary).map(BitSet(_: _*))
  def bits: Gen[BitSet] = Gen.sized(bits)

  def collect[T, U](gen: Gen[T], retryUntilMatch: Boolean = false)(pf: PartialFunction[T, U]): Gen[U] = {
    {
      if (retryUntilMatch) gen.retryUntil(pf.isDefinedAt)
      else gen.suchThat(pf.isDefinedAt)
    } map pf
  }

  def enumValue[E <: Enumeration](enum: E): Gen[enum.Value] =
    Gen.oneOf[enum.Value](enum.values.toSeq)

  def boolean: Gen[Boolean] = Gen.oneOf(true, false)

  def stringOfN(n: Int)(implicit arbChar: Arbitrary[Char]): Gen[String] = {
    Gen.containerOfN[Array, Char](n, arbChar.arbitrary).map(new String(_))
  }
  def string(implicit arbChar: Arbitrary[Char]): Gen[String] = Gen.sized(stringOfN)

  def stringOfNWithin(range: Range)(implicit arbChar: Arbitrary[Char]): Gen[String] = {
    for {
      size <- Gen.choose(range.start, range.end)
      arr <- Gen.containerOfN[Array, Char](size, arbChar.arbitrary)
    } yield new String(arr)
  }

  def setOfN[T: ClassTag](size: Int, maxFailures: Int, tGen: Gen[T]): Gen[Set[T]] = {
    for {
      _ <- Gen.const(tGen)
    } yield {
      var failures = 0
      var total = 0
      val set = mutable.Set.empty[T]
      val iter = tGen.toIterator
      while(failures < maxFailures && total < size) {
        if (set.add(iter.next())) {
          total += 1
        }
        else {
          failures += 1
        }
      }
      if (failures >= maxFailures) {
        throw new IllegalArgumentException(
          s"Gen[Set[${classTag[T].runtimeClass}]] of size $size exceeded maximum failed attempts ($maxFailures) " +
          s"after generating a set of size $total"
        )
      }
      set.toSet[T]
    }
  }
  def setOfN[T: ClassTag](size: Int, tGen: Gen[T]): Gen[Set[T]] = setOfN(size, 50, tGen)

  def setOf[T](implicit arbT: Arbitrary[T]): Gen[Set[T]] = Gen.listOf(arbT.arbitrary).map(_.toSet[T])
}

/**
 * Mixin for some helpful implicits when dealing with ScalaCheck generator monads.
 *
 * Allows for better mixing of property checks with normal single take test cases
 * with randomized values.
 */
class GenOps[T](val gen: Gen[T]) extends AnyVal {

  /**
   * Flattens a generator of generators into a single generator.
   */
  def flatten[U](implicit ev: T <:< Gen[U]): Gen[U] = gen flatMap ev
}
