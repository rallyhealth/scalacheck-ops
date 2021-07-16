package org.scalacheck.ops

import org.scalacheck.Gen

import scala.reflect.{ClassTag, classTag}

/**
 * An exception thrown when a value cannot be pulled out from the Gen.sample method after a number of tries.
 */
@deprecated("Use GenExceededRetryLimit instead.", "2.7.0")
class EmptyGenSampleException[T : ClassTag](val generator: Gen[T], attempts: Long)
  extends GenExceededRetryLimit(classTag[T].runtimeClass.getName, attempts)

/**
 * An exception thrown when a value cannot be pulled out from the Gen.sample method after a number of tries.
 *
 * TODO: Convert to final class in next major version.
 */
sealed class GenExceededRetryLimit protected (val typeName: String, val attempts: Long)
  extends Exception(
    s"Cannot generate an instance of $typeName after $attempts attempts to " +
      "get an unfiltered sample from a ScalaCheck Gen.  " +
      "Try adjusting the Gen.Parameters or loosening the restrictions to prevent exhausting the samples."
  )

object GenExceededRetryLimit {

  def apply(typeName: String, attempts: Long): GenExceededRetryLimit =
    new GenExceededRetryLimit(typeName, attempts)

  def fromClassTag[T : ClassTag](attempts: Long): GenExceededRetryLimit =
    new GenExceededRetryLimit(classTag[T].runtimeClass.getName, attempts)
}
