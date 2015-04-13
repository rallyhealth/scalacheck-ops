package org.scalacheck.ops

import org.scalacheck.Gen

import scala.reflect.{ClassTag, classTag}

class EmptyGenSampleException[T : ClassTag](generator: Gen[T], attempts: Long)
  extends Exception(
    s"Cannot generate an instance of ${classTag[T].runtimeClass.getName} after $attempts attempts to " +
      s"get an unfiltered sample from a ScalaCheck Gen.  " +
      "Try adjusting the Gen.Parameters or loosening the restrictions to prevent exhausting the samples."
  )
