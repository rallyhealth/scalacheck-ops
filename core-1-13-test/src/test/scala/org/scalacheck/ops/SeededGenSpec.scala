package org.scalacheck.ops

import org.scalacheck.{Gen, Test}
import org.scalacheck.rng.Seed
import org.scalatest.FreeSpec

class SeededGenSpec extends FreeSpec {

  private val it = classOf[SeededGen[_, _, _]].getSimpleName

  private implicit val c: GenConfig = GenConfig(Seed(1))
  private val p = Test.Parameters.default

  def itShouldMakeTheSeedAvailableToTheGenerator[S, T <: SeededGen.Tag](
    suffix: String,
    buildSeed: Int => S,
    buildGen: SeededGen.GenFn[S, T, S] => SeededGen[S, T, S]
  ): Unit = {

    s"$it$suffix should make the given seed available to the generators" in {
      val seed = buildSeed(1)
      val seededGen: SeededGen[S, T, S] = buildGen { seed =>
        Gen.const(seed)
      }
      val result = seededGen.instance(seed)
      assertResult(seed)(result)
    }
  }

  def itShouldBehaveLikeASeededGen[S, T <: SeededGen.Tag](
    suffix: String,
    buildSeed: Int => S,
    buildGen: SeededGen.GenFn[S, T, String] => SeededGen[S, T, String]
  ): Unit = {

    s"$it$suffix should generate the same values given the same seed" in {
      val seed = buildSeed(2)
      val seededGen = buildGen { _ =>
        Gen.alphaNumStr
      }
      val gen1 = seededGen.gen(seed)
      val gen2 = seededGen.gen(seed)
      (gen1 == gen2).check(p) // compares 100 results to see if the generators are equivalent
    }

    s"$it$suffix should generate different values for different seeds" in {
      val seed1 = buildSeed(3)
      val seed2 = buildSeed(4)
      val seededGen = buildGen { _ =>
        Gen.alphaNumStr
      }
      val gen1 = seededGen.gen(seed1)
      val gen2 = seededGen.gen(seed2)
      (gen1 != gen2).check(p)
    }
  }

  itShouldMakeTheSeedAvailableToTheGenerator[String, TestSeeded](
    ".Companion.gen",
    _.toString,
    TestSeeded.gen
  )

  itShouldMakeTheSeedAvailableToTheGenerator[UserId, UserId.Tag](
    " using a custom seed type",
    idx => UserId(s"test-user-$idx"),
    SeededGen.seededWith[UserId].taggedWith[UserId.Tag].build
  )

  itShouldMakeTheSeedAvailableToTheGenerator[String, TestSeeded](
    ".seededWith[String].taggedWith[TestSeed]",
    _.toString,
    SeededGen.seededWith[String].taggedWith[TestSeeded].build
  )

  itShouldMakeTheSeedAvailableToTheGenerator[Long, TestSeeded](
    ".taggedWith[TestSeed].seededWith[Long]",
    _.toLong,
    SeededGen.taggedWith[TestSeeded].seededWith[Long].build
  )

  itShouldBehaveLikeASeededGen[String, TestSeeded](
    ".seededWith[String].taggedWith[TestSeed]",
    _.toString,
    SeededGen.seededWith[String].taggedWith[TestSeeded].build
  )

  itShouldBehaveLikeASeededGen[Long, TestSeeded](
    ".taggedWith[TestSeed].seededWith[Long]",
    _.toLong,
    SeededGen.taggedWith[TestSeeded].seededWith[Long].build
  )

  itShouldBehaveLikeASeededGen[String, TestSeeded](
    ".Companion.gen",
    _.toString,
    TestSeeded.gen
  )

  itShouldBehaveLikeASeededGen[UserId, UserId.Tag](
    " using a custom seed type",
    idx => UserId(s"test-user-$idx"),
    SeededGen.seededWith[UserId].taggedWith[UserId.Tag].build
  )

  s"$it should pass the seed between seeded generators" in {
    val seed = "test-seed1"
    val seededGen1 = TestSeeded.gen { _ =>
      Gen.identifier
    }
    val seededGen2 = TestSeeded.gen { implicit seed =>
      for {
        s <- seededGen1
        id2 <- Gen.identifier
      } yield s"$seed-$s-$id2"
    }
    val result = seededGen2.instance(seed)
    assert(result.contains(seed))
  }

  s"$it.map should map the underlying generator" in {
    val seed = "test-seed1"
    val seededGen1 = TestSeeded.gen { implicit seed =>
      for {
        id <- Gen.identifier
      } yield s"$seed-$id"
    }
    val prefix = "prefix"
    val seededGen2 = seededGen1.andThen(_.map(s => s"$prefix-$s"))
    val result = seededGen2.instance(seed)
    assert(result.startsWith(s"$prefix-$seed-"))
  }

  private case class UserId(value: String)

  private object UserId extends SeededGen.Companion[UserId](SeedExtractor.from(_.value)) {
    sealed abstract class Tag extends SeededGen.Tag
  }

  private sealed abstract class TestSeeded extends SeededGen.Tag
  private object TestSeeded extends SeededGen.TagCompanion[String, TestSeeded]
}
