package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalacheck.Gen.RetrievalError
import org.scalacheck.rng.Seed

import scala.reflect.ClassTag
import scala.util.Try

/**
 * Provides the ability to convert a [[Gen]] into an [[Iterator]]
 *
 * @param gen a generator of some kind
 * @tparam T the type of value produced by the generator
 */
class GenOrThrow[T: ClassTag](val gen: Gen[T]) {
  self =>

  /**
   * An iterator made by attempting to find each next sample after the given number of retries.
   *
   * @note This uses the new pureApply feature only available in ScalaCheck versions >=1.14.x
   *
   * @return An iterator that will generate from the given starting seed
   *
   * @throws EmptyGenSampleException when samples cannot be generated after the given number of attempts
   */
  def iterator(implicit c: GenConfig = GenConfig.default): Iterator[T] = {
    var s = c.seed
    Iterator.continually {
      val res = getOrThrow(c.withSeed(s))
      s = s.next
      res
    }
  }

  // Private because the preferred method would use an implicit argument list,
  // but I can't do this until I can make a breaking change.
  @inline private def getOrThrow(c: GenConfig): T = {
    try gen.pureApply(c.params, c.seed, c.retries)
    catch {
      case _: RetrievalError =>
        // attempts exhausted, return failure
        throw new EmptyGenSampleException[T](gen, c.retries)
    }
  }

  /**
   * Get the next element from this generator using the given seed and default size and number of retries.
   */
  def getOrThrow(seed: Seed): T = getOrThrow(GenConfig.default.withSeed(seed))

  /**
   * Get the next element from this generator using the default seed with the given number of retries.
   *
   * @throws EmptyGenSampleException when samples cannot be generated after the given number of attempts
   */
  @deprecated("Do not provide the number of retries. This method will be removed in the next major version.", "2.3.0")
  def getOrThrow(attempts: Int): T = randomOrThrow()(GenConfig.default.withRetries(attempts))

  /**
   * Get a random element from this generator using the default seed.
   *
   * Generators can run out of samples and return empty results. Typically,
   * this will be the result of bad Gen Parameters or having too many
   * suchThat() restrictions that make it difficult to find the next sample.
   *
   * @throws EmptyGenSampleException when samples cannot be generated after a default number of attempts
   */
  @deprecated("The meaning of this method will change in the next major version. " +
    "If you need a random sample on each call, you should use .randomOrThrow() instead",
    "2.3.0")
  def getOrThrow: T = getOrThrow(GenConfig.default.withSeed(Seed.random()))

  def randomOrThrow()(implicit c: GenConfig = GenConfig.default): T = {
    getOrThrow(c.withSeed(Seed.random()))
  }

  // Private because the preferred method would use an implicit argument list,
  // but I can't do this change until I am able to make a breaking change.
  private def tryGet(c: GenConfig): Try[T] = Try(getOrThrow(c))

  /**
   * Get the next element from this generator using the given seed and default size and number of retries.
   */
  def tryGet(seed: Seed): Try[T] = tryGet(GenConfig.default.withSeed(seed))

  /**
   * Try to get a non-empty sample for a given number of tries and return the resulting get attempts in a [[Try]].
   *
   * @param attempts the number of attempts to get a non-empty sample before returning a Failure.
   * @return Either a non-empty sample or a Failure with information about what sample was tried.
   */
  @deprecated("Do not provide the number of retries. This method will be removed in the next major version.", "2.3.0")
  def tryGet(attempts: Int): Try[T] = Try(gen.randomOrThrow()(GenConfig.default.withRetries(attempts)))

  /**
   * Same as `Try(gen.randomOrThrow())`
   */
  @deprecated("The meaning of this method will change in the next major version. " +
    "If you need a random sample on each call, you should use .randomOrThrow() instead",
    "2.3.0")
  def tryGet: Try[T] = Try(gen.randomOrThrow())

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
  @deprecated("Use .iterator instead. " +
    "If you need to specify retries, you can pass the argument GenConfig.defaults.withRetries(n) to .iterator",
    "2.3.0")
  def toIterator(attempts: Int): Iterator[T] = iterator(GenConfig.default.withRetries(attempts))

  @deprecated("Use .iterator instead", "2.3.0")
  def toIterator: Iterator[T] = iterator(GenConfig.default)

  /**
   * Converts this generator into a lazy [[Iterable]] so that it can be converted into a collection.
   *
   * @param attempts the number of attempts to try generating a sample before throwing an execption
   *
   * @throws EmptyGenSampleException when a sample cannot be generated after the given number of attempts
   * @return A lazy iterable that pulls from this generator or throws an exception
   */
  @deprecated("Use .iterator.to() instead. " +
    "If you need to specify retries, you can pass the argument GenConfig.defaults.withRetries(n) to .iterator",
    "2.3.0")
  def toIterable(attempts: Int): Iterable[T] = new Iterable[T] {
    override val iterator: Iterator[T] = self.iterator(GenConfig.default.withRetries(attempts))
  }

  @deprecated("Use .iterator.to() instead", "2.3.0")
  def toIterable: Iterable[T] = toIterable(GenConfig.default.retries)
}
