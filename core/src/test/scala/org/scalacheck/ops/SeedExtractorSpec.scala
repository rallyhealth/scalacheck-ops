package org.scalacheck.ops

import java.util.UUID

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.FreeSpec
import org.scalatest.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks._

import scala.reflect.{ClassTag, classTag}

class SeedExtractorSpec extends FreeSpec {
  import SeedExtractorSpec._

  private val it = classOf[SeedExtractor[_]].getName

  private val collisionCheckParams = Seq(Workers(4))

  private def groupCollisions[A](values: Seq[A])(implicit extractor: SeedExtractor[A]): Map[Long, Set[A]] = {
    values.view
      .map(v => (extractor.seed(v), v))
      .groupBy(_._1)
      .collect(Function.unlift {
        case (s, vs) =>
          val vSet = vs.map(_._2).toSet
          if (vSet.size > 1) Some(s -> vSet) else None
      })
  }

  "verify this test catches collisions" in {
    implicit val collisionProneSeedExtractor: SeedExtractor[Int] = SeedExtractor.fromLong(_ % 2)
    forAll(Gen.listOfN(1000, arbitrary[Int]), collisionCheckParams: _*) { ints: Seq[Int] =>
      whenever(ints.nonEmpty) {
        val collisions = groupCollisions(ints)
        assert(collisions.nonEmpty)
      }
    }
  }

  private def itShouldExtractAConsistentAndUniqueSeed[A : Arbitrary : ClassTag : SeedExtractor](): Unit = {
    itShouldExtractAConsistentAndUniqueSeedUsing[A]("", implicitly)
  }

  private def itShouldExtractAConsistentAndUniqueSeedUsing[A : Arbitrary : ClassTag](suffix: String, extractor: SeedExtractor[A]): Unit = {
    val typeName = classTag[A].runtimeClass.getSimpleName

    s"$it[$typeName]$suffix should extract a consistent seed" in {
      forAll { a: A =>
        assert(extractor.seed(a) == extractor.seed(a))
      }
    }

    s"$it[$typeName]$suffix should avoid collisions" in {
      forAll(Gen.listOfN(1000, arbitrary[A]), collisionCheckParams: _*) { as: Seq[A] =>
        val collisions = groupCollisions(as)
        collisions should have size 0
      }
    }
  }

  itShouldExtractAConsistentAndUniqueSeed[Int]()
  itShouldExtractAConsistentAndUniqueSeed[Long]()
  itShouldExtractAConsistentAndUniqueSeed[String]()
  itShouldExtractAConsistentAndUniqueSeed[UUID]()

  itShouldExtractAConsistentAndUniqueSeedUsing[Long](".fromLong(identity)", SeedExtractor.fromLong(identity))
  itShouldExtractAConsistentAndUniqueSeedUsing[Examples](".from(_.string)", SeedExtractor.from(_.string))
  itShouldExtractAConsistentAndUniqueSeedUsing[Examples](".from(_.long)", SeedExtractor.from(_.long))
  itShouldExtractAConsistentAndUniqueSeedUsing[Examples](".fromToString", SeedExtractor.fromToString)
  itShouldExtractAConsistentAndUniqueSeedUsing[Examples](" (implicitly)", implicitly)
}

object SeedExtractorSpec {

  private final case class Examples(
    int: Int,
    long: Long,
    string: String
  )

  implicit private val arbExamples: Arbitrary[Examples] = {
    for {
      int <- arbitrary[Int]
      long <- arbitrary[Long]
      string <- arbitrary[String]
    } yield Examples(int, long, string)
  }

  private implicit def arbUUID: Arbitrary[UUID] = Gen.uuid
}
