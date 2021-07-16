package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalacheck.Gen.RetrievalError
import org.scalacheck.rng.Seed

import scala.util.Random

final class GenFromConfig[T](gen: Gen[T], config: GenConfig, typeName: String) {

  private[this] def headFromConfig(c: GenConfig): T = {
    try gen.pureApply(c.params, c.seed, c.retries)
    catch {
      case _: RetrievalError =>
        // attempts exhausted, return failure
        throw GenExceededRetryLimit(typeName, c.retries)
    }
  }

  private[this] def iteratorFromConfig(seed: Seed): Iterator[T] = {
    var s = seed
    Iterator.continually {
      val res = headFromConfig(config.withSeed(s))
      s = s.next
      res
    }
  }

  /**
   * Get the first instance of this generator with the given configuration.
   *
   * @note this is a stateless function and calling this method with
   *       the same config will always produce the same result.
   */
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def head: T = headFromConfig(config)

  /**
   * Modify the captured [[GenConfig]] using the given function.
   */
  def configured(fn: GenConfig => GenConfig): GenFromConfig[T] = new GenFromConfig(gen, fn(config), typeName)

  /**
   * Effectively shorthand for `.configured(_.withSeed(seed)).head`.
   */
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def valueFor(seed: Seed): T = headFromConfig(config.withSeed(seed))

  /**
   * An iterator made by attempting to find each next sample after the given number of retries.
   *
   * @note This uses the new pureApply feature only available in ScalaCheck versions >=1.14.x
   *
   * @return An iterator that will generate from the given starting seed
   */
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def iterator: Iterator[T] = iteratorFromConfig(config.seed)

  /**
   * Get a random element from this generator using the default seed.
   *
   * Generators can run out of samples and return empty results. Typically,
   * this will be the result of bad Gen Parameters or having too many
   * suchThat() restrictions that make it difficult to find the next sample.
   */
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def nextRandom(rng: Random = Random): T = {
    headFromConfig(config.withSeed(config.seed.reseed(rng.nextLong())))
  }

  /**
   * Effectively the same as calling [[nextRandom()]] with [[Iterator.continually]], except that
   * the given [[Random]] number generator is only used to generate the first seed and all subsequent
   * iterations are produced from [[Seed.next]].
   *
   * @note If you want to provide a consistent [[Seed]], you should use [[configured]]
   *       to set [[GenConfig.withSeed]] and then call [[iterator]].
   *
   * @param rng the random number generator used to initialize the seed.
   */
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def nextRandomIterator(rng: Random = Random): Iterator[T] = iteratorFromConfig(Seed(rng.nextLong()))

  // Keeping the following methods around to keep source compatibility with 2.6.0 and avoid some of the pain
  // of pulling in versions >= 2.7.0

  /**
   * An iterator made by [[Iterator.continually]] calling [[Gen.sample]].
   *
   * @note If the generator has filters, then this method could return None a lot. If you want a
   *       safe way to filter the Nones, see [[iterator]]
   *
   * @note This will use a random seed for each sample. If you want to get the same iterator from
   *       the same seed, you would have to use [[iterator]] and handle the possible.
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
   * @see [[iterator]] for a safer, bounded alternative.
   *
   * @return An [[Iterator]] that returns only the defined samples.
   */
  @deprecated("This is generally a bad idea and is easy enough to inline. It will be removed in the next major version.", "2.7.0")
  def toUnboundedIterator: Iterator[T] = sampleIterator collect { case Some(x) => x }

  /**
   * Get a random element from this generator using the default seed.
   *
   * Generators can run out of samples and return empty results. Typically,
   * this will be the result of bad Gen Parameters or having too many
   * suchThat() restrictions that make it difficult to find the next sample.
   */
  @deprecated(
    "If you need a random sample on each call, use .nextRandom()\n" +
      "If you want a stateless function, use .head\n" +
      "If you want to pass a specific Seed, use .configured(_.withSeed(seed)).head",
    "2.3.0")
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def getOrThrow: T = headFromConfig(GenConfig.default.withSeed(Seed.random()))

  /**
   * @see [[head]]
   */
  @deprecated("Use .head instead. This method is contradictory " +
    "(throwing an exception makes the function partially undefined and thus impure) and verbose " +
    "(this has very similar semantics to the familiar .head method on Scala collections)", "2.7.0")
  @throws[GenExceededRetryLimit](
    "When the number of attempts to generate a valid sample is exhausted because " +
      "the filters on this generator are too restrictive.")
  def getOrThrowPure: T = head
}
