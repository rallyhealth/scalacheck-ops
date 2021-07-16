package org.scalacheck.ops

import izumi.reflect.Tag

import scala.annotation.implicitNotFound
import scala.reflect.ClassTag

@implicitNotFound(
  "Could not grab the type name of ${T} at compile-time... except for printing this error message (go figure). " +
    "This means that neither a ClassTag nor izumi.reflect.Tag could not be summoned for this type.")
final class TypeName[T] private (val typeName: String)

object TypeName extends LowPriorityTypeName {

  def fromIzumiTag[T: Tag]: TypeName[T] = new TypeName[T](Tag[T].tag.longName)

  def fromClassTag[T: ClassTag]: TypeName[T] = new TypeName[T](implicitly[ClassTag[T]].runtimeClass.getName)
}

sealed trait LowPriorityTypeName extends LowerPriorityTypeName {

  implicit def typeNameFromClassTag[T : ClassTag]: TypeName[T] = TypeName.fromClassTag
}

sealed trait LowerPriorityTypeName {

  // TODO: Use https://blog.7mind.io/no-more-orphans.html trick to avoid transitive dependency on izumi?
  implicit def typeNameFromIzumiTag[T : Tag]: TypeName[T] = TypeName.fromIzumiTag
}
