package org.scalacheck.ops

import io.estatico.newtype.macros.{newsubtype, newtype}

object NewtypeExample {
  import scala.language.{higherKinds, implicitConversions}

  @newtype class IdType(id: String)
  @newsubtype class IdSubtype(id: String)
}
