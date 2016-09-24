package org.scalacheck.ops

import org.scalacheck.{Arbitrary, Gen}

import scala.collection.{BitSet, mutable}
import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

object GenOps {

  def binary: Gen[Int] = Gen.oneOf(0, 1)

  def bits(size: Int): Gen[BitSet] = Gen.listOfN(size, binary).map(BitSet(_: _*))
  def bits: Gen[BitSet] = Gen.sized(bits)

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
      gen <- Gen.const(tGen)
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
        throw new IllegalArgumentException(s"Provided generator of ${classTag[T].runtimeClass}")
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

/**
 * Provides the ability to convert a [[Gen]] into an [[Iterator]]
 *
 * @note Can't extend AnyVal because of this bug: https://issues.scala-lang.org/browse/SI-7034
 *
 * @param gen a generator of some kind
 * @tparam T the type of value produced by the generator
 */
class GenOrThrow[T: ClassTag](val gen: Gen[T]) /* extends AnyVal */ {
  self =>

  private[this] val defaultAttempts = 100

  /**
   * Generators can run out of samples and will return an empty result.
   *
   * Typically this will be the result of bad Gen Parameters or having too many
   * suchThat() restrictions that reduce the sample size to 0.
   */
  def getOrThrow(attempts: Int): T = tryGet(attempts).get
  def getOrThrow: T = tryGet(defaultAttempts).get

  /**
   * Try to get a non-empty sample for a given number of tries and return the resulting get attempts in a [[Try]].
   *
   * @param attempts the number of attempts to get a non-empty sample before returning a Failure.
   * @return Either a non-empty sample or a [[Failure]] with information about what sample was tried.
   */
  def tryGet(attempts: Int): Try[T] = {
    var attempt = 0
    while (attempt < attempts) {
      gen.sample match {
        case Some(t) => return Success(t)
        case None => attempt += 1
      }
    }
    // attempts exhausted, return failure
    Failure(new EmptyGenSampleException[T](gen, Gen.Parameters.default.size * attempts))
  }
  def tryGet: Try[T] = tryGet(defaultAttempts)

  /**
   * An iterator made by continuously sampling the generator.
   *
   * @note If the generator has filters, then this method could return None a lot. If you want a
   *       safe way to filter the Nones, see [[toIterator]]
   *
   * @return An iterator of Options which are None if the generator's filters ruled out the sample.
   */
  def sampleIterator: Iterator[Option[T]] = Iterator continually gen.sample

  /**
   * Converts this generator to an [[Iterator]] in the obvious (but potentially dangerous) way.
   *
   * @note This pulls an item from the generator one at a time, however, if the generator has too
   * many restrictive filters, this can result in an infinite loop.
   *
   * @see [[toIterator]] for a safer, bounded alternative.
   *
   * @return An [[Iterator]] that returns only the defined samples.
   */
  def toUnboundedIterator: Iterator[T] = sampleIterator collect { case Some(x) => x }

  /**
   * Converts this generator into an infinite [[Iterator]] for a more flexible pull-based API.
   *
   * @param attempts the number of attempts to try generating a sample before throwing an execption
   *
   * @throws EmptyGenSampleException when samples cannot be generated after the given number of attempts
   * @return An iterator that pulls from this generator or throws an exception
   */
  def toIterator(attempts: Int): Iterator[T] = {
    val toTryIterator = Iterator continually tryGet(attempts)
    toTryIterator.map(_.get)
  }
  def toIterator: Iterator[T] = toIterator(defaultAttempts)

  /**
   * Converts this generator into a lazy [[Iterable]] so that it can be converted into a collection.
   *
   * @param attempts the number of attempts to try generating a sample before throwing an execption
   *
   * @throws EmptyGenSampleException when a sample cannot be generated after the given number of attempts
   * @return A lazy iterable that pulls from this generator or throws an exception
   */
  def toIterable(attempts: Int): Iterable[T] = new Iterable[T] {
    override val iterator: Iterator[T] = self.toIterator(attempts)
  }
  def toIterable: Iterable[T] = toIterable(defaultAttempts)
}
