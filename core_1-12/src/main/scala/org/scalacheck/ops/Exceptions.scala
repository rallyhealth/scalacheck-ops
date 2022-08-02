package org.scalacheck.ops

import org.scalacheck.Gen

import scala.reflect.{classTag, ClassTag}

/** An exception thrown when a value cannot be pulled out from the Gen.sample method after a number of tries.
  */
class EmptyGenSampleException[T : ClassTag](
  val generator: Gen[T],
  val attempts: Long
) extends Exception(
    s"Cannot generate an instance of ${classTag[T].runtimeClass.getName} after $attempts attempts to " +
      "get an unfiltered sample from a ScalaCheck Gen.  " +
      "Try adjusting the Gen.Parameters or loosening the restrictions to prevent exhausting the samples."
  )
