package org.scalacheck.ops

import org.scalatest.freespec.AnyFreeSpec

class NewtypeTypeNameSpec extends AnyFreeSpec {
  /*
   * Note differences in type name emited by Scala 3 vs. Scala 2
   */
  "Find a TypeName of a newtype" in {
    assertResult("org.scalacheck.ops.NewtypeExample$IdType") {
      implicitly[TypeName[NewtypeExample.IdType]].typeName
    }
  }

  "Find a TypeName of a newsubtype" in {
    assertResult("org.scalacheck.ops.NewtypeExample$IdSubtype") {
      implicitly[TypeName[NewtypeExample.IdSubtype]].typeName
    }
  }
}
