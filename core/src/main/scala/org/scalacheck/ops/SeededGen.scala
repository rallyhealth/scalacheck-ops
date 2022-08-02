package org.scalacheck.ops

import com.softwaremill.tagging._
import org.scalacheck.Gen

import scala.language.implicitConversions

/** Used instead of a [[Gen]] to require the caller to provide a rallyId that is used to deterministically
  * alter the underlying seed of the resulting generator.
  *
  * If you use a SeededGen, the resulting generator will always generate the same results for the same seed.
  *
  * @note You only need to use this if you want to require the caller to pass a seed OR
  *       your generator depends on the value of the seed provided (passed along implicitly).
  *       Using a plain [[Gen]] will work as expected in all other contexts.
  *
  * {{{
  *   trait ID extends SeededGen.Tag
  *   val genNamespacedID = SeededGen[String, ID, String] { implicit id =>
  *     for {
  *       inner <- Gen.identifier // doesn't need to be a SeededGen
  *     } yield s"thing-$id-$inner"
  *   }
  *   assert(gen.instance("1") startsWith "thing-1-")
  * }}}
  *
  * @note You can treat a [[SeededGen]] as a [[Gen]] when inside the definition of a [[SeededGen]] if you
  *       make the given seed implicit.
  *
  * {{{
  *   val gen = SeededGen[String, ID, Map[String, String]] { implicit id =>
  *     for {
  *       namespacedID <- genNamespacedID // implicitly converted from SeededGen to Gen because of the implicit id
  *       rank <- Gen.choose(1, 10)
  *     } yield Map(
  *       "id" -> namespacedID,
  *       "rank" -> rank.toString
  *     )
  *   }
  * }}}
  *
  * Typically, it is most convenient to define a SeededGen alias so that you can benefit from
  * type inference and hide the often repeated seed and tag types. For this, you should define an
  * object that extends [[SeededGen#Companion]].
  *
  * Instead of defining `map` and `flatMap` methods, this pattern opts to implicitly convert your
  * SeededGen into a [[Gen]] where these methods are defined. This makes it easier to interlace
  * [[Gen]] with SeededGen in the same for-yield comprehension.
  *
  * In order to access the seed, you can call your SeededGen builder and pass a block of code that
  * takes an implicit seed and produces a [[Gen]]. In places where you don't want to define a full
  * SeededGen, but you need access to the seed, you can use [[andThenWithSeed]]. If you don't need
  * access to the seed, you can call [[andThen]] which will allow you to alter the underlying
  * generator while keeping everything inside of a SeededGen.
  *
  * @param genFn a function from seed (typically implicit) to a generator
  * @tparam S the seed type, must have an implicit [[SeedExtractor]] to mix in with the starting seed
  * @tparam T the type of [[org.scalacheck.ops.SeededGen.Tag]] used to avoid exposing
  *           implicit type arguments on primitive values
  * @tparam V the type of value produced by the generator
  */
final class SeededGen[S, T <: SeededGen.Tag, V](
  genFn: S @@ T => Gen[V]
)(implicit
  extractor: SeedExtractor[S]
) {

  /** Stateless function that generates an identical [[Gen]] of V instances for a given seed and [[GenConfig]]. */
  def gen(seed: S): Gen[V] = genFn(seed.taggedWith[T])

  /** Stateless function that generates an identical instance value V for a given seed and [[GenConfig]]. */
  def instance(seed: S)(implicit c: GenConfig): V = {
    val mixedSeed = c.seed.reseed(extractor.seed(seed))
    genFn(seed.taggedWith[T]).pureApply(c.params, mixedSeed, c.retries)
  }

  /** Produce a new seeded gen maps over the underlying generator with access to the seed.
    */
  @inline def andThenWithSeed[X](fn: S @@ T => Gen[V] => Gen[X]): SeededGen[S, T, X] = {
    new SeededGen[S, T, X](seed => fn(seed)(gen(seed)))
  }

  /** Produce a new seeded gen maps over the underlying generator without accessing the seed.
    */
  @inline def andThen[X](fn: Gen[V] => Gen[X]): SeededGen[S, T, X] = {
    andThenWithSeed(_ => fn)
  }
}

object SeededGen {

  /** Extend this marker trait with either your seed class or another marker type tag.
    *
    * This is required to avoid defining an implicit conversion on any primitive types
    * (in the case where you decide to use a primitive type seed). See [[toGen]].
    *
    * @see [[Companion]] documentation for an example of how to use this.
    */
  trait Tag

  type GenFn[-S, T <: Tag, V] = S @@ T => Gen[V]

  /** Begin building a [[SeededGen]] by providing the type of seed.
    */
  def seededWith[S : SeedExtractor]: SeededBuilder[S] = new SeededBuilder

  final class SeededBuilder[S : SeedExtractor] private[SeededGen] {
    def taggedWith[T <: Tag]: FinalBuilder[S, T] = new FinalBuilder
  }

  /** Begin building a [[SeededGen]] by providing the [[Tag]] type.
    */
  def taggedWith[T <: Tag]: TaggedBuilder[T] = new TaggedBuilder

  final class TaggedBuilder[T <: Tag] private[SeededGen] {
    def seededWith[S : SeedExtractor]: FinalBuilder[S, T] = new FinalBuilder
  }

  final class FinalBuilder[S : SeedExtractor, T <: Tag] private[SeededGen] {

    /** Builds the [[SeededGen]] from the built up type arguments and implicits and the given function.
      */
    def build[V](genFn: GenFn[S, T, V]): SeededGen[S, T, V] = {
      new SeededGen[S, T, V](genFn)
    }
  }

  /** Extend this class in the companion object of a type or type tag that you would like
    * to use as a seed.
    *
    * {{{
    * final case class UserId(value: String)
    * object UserId extends SeededGen.Companion[UserId](SeedExtractor.from(_.value)) {
    *   sealed abstract class Tag extends SeededGen.Tag
    * }
    * }}}
    *
    * This will give you a uniform syntax for constructing seeded generators.
    *
    * {{{
    *     val example: UserId.Gen[String] = UserId.gen(implicit userId => Gen.string)
    * }}}
    */
  abstract class Companion[S](extractor: SeedExtractor[S]) {
    type Tag <: SeededGen.Tag
    type Gen[V] = SeededGen[S, Tag, V]
    implicit val seedExtractor: SeedExtractor[S] = extractor
    def gen[V](genFn: GenFn[S, Tag, V]): Gen[V] = new SeededGen(genFn)
  }

  /** Similar to [[Companion]], but used when you don't want to define the [[Companion#Tag]] on the companion
    * object of the model that you are using as a seed.
    *
    * Instead you can define a separate tag class and use this as the companion object for that tag type.
    *
    * {{{
    *   final case class UserId(value: String)
    *
    *   // in some other part of the code
    *   sealed abstract class UserIdSeeded extends SeededGen.Tag
    *   object UserIdSeeded extends SeededGen.TagCompanion[UserId, UserIdSeeded]
    * }}}
    *
    * This will give you a uniform syntax for constructing seeded generators.
    *
    * {{{
    *     val example: UserIdSeeded.Gen[String] = UserIdSeeded.gen(implicit userId => Gen.string)
    * }}}
    */
  abstract class TagCompanion[S, T <: Tag](implicit extractor: SeedExtractor[S]) extends Companion[S](extractor) {
    override type Tag = T
  }

  /** Implicitly converts a [[SeededGen]] into a regular [[Gen]] if there is the appropriate implicit seed in scope.
    *
    * This implicit conversion makes it possible to flatMap over [[SeededGen]]s the same way you do regular
    * [[Gen]]s, but it introduces the need to tag the seed type to avoid an implicit conversion on primitive types.
    */
  implicit def toGen[S, T <: SeededGen.Tag, V](seededGen: SeededGen[S, T, V])(implicit seed: S @@ T): Gen[V] = {
    seededGen.gen(seed)
  }
}
