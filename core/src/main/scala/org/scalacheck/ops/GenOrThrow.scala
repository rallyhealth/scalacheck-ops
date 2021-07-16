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
@deprecated("Use GenFromConfig instead. This class requires a ClassTag and is not as reusable as GenFromConfig. " +
  "It will be removed in the next major version.", "2.7.0")
class GenOrThrow[T: ClassTag](val gen: Gen[T]) {
  self =>

  /**
   * Same as [[GenFromConfig.iterator]] except throws an [[EmptyGenSampleException]]
   * instead of [[GenExceededRetryLimit]]
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
   * Same as [[GenFromConfig.getOrThrow]] except throws an [[EmptyGenSampleException]]
   */
  @deprecated("Use .getConfigured(_.withSeed(seed)) instead.", "2.7.0")
  def getOrThrow(seed: Seed): T = getOrThrow(GenConfig.default.withSeed(seed))

  /**
   * Get the next element from this generator using the default seed with the given number of retries.
   *
   * @throws EmptyGenSampleException when samples cannot be generated after the given number of attempts
   */
  @deprecated("Do not provide the number of retries. This method will be removed in the next major version.", "2.3.0")
  def getOrThrow(attempts: Int): T = randomOrThrow()(GenConfig.default.withRetries(attempts))

  /**
   * Same as [[GenFromConfig.getOrThrow]] except throws a [[EmptyGenSampleException]]
   * instead of [[GenExceededRetryLimit]]
   *
   * @throws EmptyGenSampleException when samples cannot be generated after a default number of attempts
   */
  @deprecated("If you need a random sample on each call, use .nextRandom() -- " +
    "otherwise, if you want a stateless function, use .head instead.", "2.3.0")
  def getOrThrow: T = getOrThrow(GenConfig.default.withSeed(Seed.random()))

  /**
   * @see [[GenFromConfig.head]]
   */
  @deprecated("Use .head instead. This method is contradictory " +
    "(throwing an exception makes the function partially undefined and thus impure) and verbose " +
    "(this has very similar semantics to the familiar .head method on Scala collections)", "2.7.0")
  def getOrThrowPure(implicit c: GenConfig): T = getOrThrow(c)

  /**
    * @see [[GenFromConfig.nextRandom]]
    */
  @deprecated("Use .nextRandom() instead.", "2.7.0")
  def randomOrThrow()(implicit c: GenConfig = GenConfig.default): T = {
    getOrThrow(c.withSeed(Seed.random()))
  }

  // Private because the preferred method would use an implicit argument list,
  // but I can't do this change until I am able to make a breaking change.
  private def tryGet(c: GenConfig): Try[T] = Try(getOrThrow(c))

  /**
   * Get the next element from this generator using the given seed and default size and number of retries.
   */
  @deprecated("Use Try(gen.getConfigured(_.withSeed(seed))) instead.", "2.7.0")
  def tryGet(seed: Seed): Try[T] = tryGet(GenConfig.default.withSeed(seed))

  /**
   * Try to get a non-empty sample for a given number of tries and return the resulting get attempts in a [[Try]].
   *
   * @param attempts the number of attempts to get a non-empty sample before returning a Failure.
   * @return Either a non-empty sample or a Failure with information about what sample was tried.
   */
  @deprecated("Do not provide the number of retries. This method will be removed in the next major version.", "2.3.0")
  def tryGet(attempts: Int): Try[T] = Try(randomOrThrow()(GenConfig.default.withRetries(attempts)))

  /**
   * Same as `Try(gen.randomOrThrow())`
   */
  @deprecated("This method will be removed in the next major version", "2.3.0")
  def tryGet: Try[T] = Try(randomOrThrow())

  /**
   * Same as [[GenFromConfig.sampleIterator]].
   */
  def sampleIterator: Iterator[Option[T]] = Iterator continually gen.sample

  /**
   * Same as [[GenFromConfig.toUnboundedIterator]].
   */
  @deprecated("This is generally a bad idea and is easy enough to inline. It will be removed in the next major version.", "2.7.0")
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
