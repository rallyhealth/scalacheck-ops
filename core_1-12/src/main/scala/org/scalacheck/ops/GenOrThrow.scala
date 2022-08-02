package org.scalacheck.ops

import org.scalacheck.Gen

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

/** Provides the ability to convert a [[Gen]] into an [[Iterator]]
  *
  * @note Can't extend AnyVal because of this bug: https://issues.scala-lang.org/browse/SI-7034
  *
  * @param gen a generator of some kind
  * @tparam T the type of value produced by the generator
  */
class GenOrThrow[T : ClassTag](val gen: Gen[T]) {
  self =>

  private[this] val defaultAttempts = 100

  /** Get the next element from this generator using the default seed with the given number of retries.
    *
    * @throws EmptyGenSampleException when samples cannot be generated after the given number of attempts
    */
  @deprecated("Do not provide the number of retries. This method will be removed in the next major version.", "2.3.0")
  def getOrThrow(attempts: Int): T = tryGet(attempts).get

  /** Get a random element from this generator using the default seed.
    *
    * Generators can run out of samples and return empty results. Typically,
    * this will be the result of bad Gen Parameters or having too many
    * suchThat() restrictions that make it difficult to find the next sample.
    *
    * @throws EmptyGenSampleException when samples cannot be generated after a default number of attempts
    */
  @deprecated(
    "The meaning of this method will change in the next major version. " + "If you need a random sample on each call, you should use .randomOrThrow() instead",
    "2.3.0"
  )
  def getOrThrow: T = tryGet.get

  /** Get a random element from this generator using the default seed.
    * Generators can run out of samples and will return an empty result.
    *
    * Typically this will be the result of bad Gen Parameters or having too many
    * suchThat() restrictions that reduce the sample size to 0.
    *
    * @throws EmptyGenSampleException when samples cannot be generated after a default number of attempts
    */
  def randomOrThrow(): T = {
    tryGet(defaultAttempts).get
  }

  /** Try to get a non-empty sample for a given number of tries and return the resulting get attempts in a [[Try]].
    *
    * @param attempts the number of attempts to get a non-empty sample before returning a Failure.
    * @return Either a non-empty sample or a Failure with information about what sample was tried.
    */
  @deprecated("Do not provide the number of retries. This method will be removed in the next major version.", "2.3.0")
  def tryGet(attempts: Int): Try[T] = {
    var attempt = 0
    while (attempt < attempts)
      gen.sample match {
        case Some(t) => return Success(t)
        case None => attempt += 1
      }
    // attempts exhausted, return failure
    Failure(new EmptyGenSampleException[T](gen, attempts))
  }

  /** Same as `Try(gen.randomOrThrow())`
    */
  @deprecated(
    "The meaning of this method will change in the next major version. " + "If you need a random sample on each call, you should use .randomOrThrow() instead",
    "2.3.0"
  )
  def tryGet: Try[T] = tryGet(defaultAttempts)

  /** An iterator made by continuously sampling the generator.
    *
    * @note If the generator has filters, then this method could return None a lot. If you want a
    *       safe way to filter the Nones, see [[toIterator]]
    *
    * @return An iterator of Options which are None if the generator's filters ruled out the sample.
    */
  def sampleIterator: Iterator[Option[T]] = Iterator.continually(gen.sample)

  /** Converts this generator to an [[Iterator]] in the obvious (but potentially dangerous) way.
    *
    * @note This pulls an item from the generator one at a time, however, if the generator has too
    * many restrictive filters, this can result in an infinite loop.
    *
    * @see [[toIterator]] for a safer, bounded alternative.
    *
    * @return An [[Iterator]] that returns only the defined samples.
    */
  def toUnboundedIterator: Iterator[T] = sampleIterator.collect { case Some(x) => x }

  /** Converts this generator into an infinite [[Iterator]] for a more flexible pull-based API.
    *
    * @param attempts the number of attempts to try generating a sample before throwing an execption
    *
    * @throws EmptyGenSampleException when samples cannot be generated after the given number of attempts
    * @return An iterator that pulls from this generator or throws an exception
    */
  def toIterator(attempts: Int): Iterator[T] = {
    val toTryIterator = Iterator.continually(tryGet(attempts))
    toTryIterator.map(_.get)
  }
  def toIterator: Iterator[T] = toIterator(defaultAttempts)

  /** Converts this generator into a lazy [[Iterable]] so that it can be converted into a collection.
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
