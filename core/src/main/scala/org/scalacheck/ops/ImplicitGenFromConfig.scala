package org.scalacheck.ops

import org.scalacheck.Gen

trait ImplicitGenFromConfig {

  import scala.language.implicitConversions

  implicit def genFromConfigOps[T](gen: Gen[T])(implicit gc: GenConfig, tn: TypeName[T]): GenFromConfig[T] =
    new GenFromConfig[T](gen, gc, tn.typeName)

  implicit def genFromConfigBuilder[T](gen: Gen[T]): GenFromConfigBuilder[T] = new GenFromConfigBuilder[T](gen)
}

final class GenFromConfigBuilder[T](private val gen: Gen[T]) extends AnyVal {

  def getOrFailWithName(typeName: String)(implicit gc: GenConfig): GenFromConfig[T] =
    new GenFromConfig[T](gen, gc, typeName)
}
