package org.scalacheck.ops

import org.scalatest.freespec.AnyFreeSpec

class NewtypeTypeNameSpec extends AnyFreeSpec {

  "Find a TypeName of a newtype" in {
    assertResult("org.scalacheck.ops.NewtypeExample.IdType.Type") {
      implicitly[TypeName[NewtypeExample.IdType]].typeName
    }
  }

  "Find a TypeName of a newsubtype" in {
    assertResult("org.scalacheck.ops.NewtypeExample.IdSubtype.Type") {
      implicitly[TypeName[NewtypeExample.IdSubtype]].typeName
    }
  }
}
