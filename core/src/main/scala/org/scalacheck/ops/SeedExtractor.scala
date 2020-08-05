package org.scalacheck.ops

import java.util.UUID

/**
  * A typeclass representing how convert the given type of seed value into a long
  * so that it can be mixed in with the underlying random number generator [[org.scalacheck.rng.Seed]].
  */
trait SeedExtractor[-V] {

  /**
    * Extract a consistent and non-conflicting hash code from the given input.
    */
  def seed(input: V): Long
}

object SeedExtractor extends DefaultSeedExtractors {

  def apply[V](implicit extractor: SeedExtractor[V]): SeedExtractor[V] = extractor

  def from[A, B : SeedExtractor](fn: A => B): SeedExtractor[A] = new SeedExtractor[A] {
    override def seed(input: A): Long = SeedExtractor.extract(fn(input))
  }

  def fromLong[A](fn: A => Long): SeedExtractor[A] = new SeedExtractor[A] {
    override def seed(input: A): Long = fn(input)
  }

  def extract[V](input: V)(implicit seeder: SeedExtractor[V]): Long = seeder.seed(input)
}

trait DefaultSeedExtractors {

  /**
    * The [[toString]] method is a good default for almost all types because it is either
    * going to be unique per instance of an object with no overriding definition of it
    * (i.e. not a case class) OR it will be something like a tuple / case class which
    * overrides the definition in such a way that two equal objects will have the same
    * value from calling [[toString]].
    *
    * @note this is low priority to avoid collisions with other shared implicit definitions.
    */
  implicit def fromToString[S]: SeedExtractor[S] = new SeedExtractor[S] {
    override def seed(input: S): Long = SeedExtractor.string.seed(input.toString)
  }


  /**
    * Uses the most significant digits of the UUID as the seed.
    */
  implicit val uuid: SeedExtractor[UUID] = new SeedExtractor[UUID] {
    override def seed(input: UUID): Long = input.getMostSignificantBits
  }

  /**
    * Extract a deterministic and collision-resistant number from the given string.
    *
    * Creates a name-based UUID and extracts the most significant digits of the MD5 hash.
    */
  implicit val string: SeedExtractor[String] = new SeedExtractor[String] {
    override def seed(input: String): Long = {
      uuid.seed(UUID.nameUUIDFromBytes(input.getBytes()))
    }
  }

  // Lossless conversion of int to long to hash
  implicit lazy val int: SeedExtractor[Int] = new SeedExtractor[Int] {
    override def seed(input: Int): Long = input
  }

  // Lossless conversion of long to hash
  implicit lazy val long: SeedExtractor[Long] = new SeedExtractor[Long] {
    override def seed(input: Long): Long = input
  }
}
