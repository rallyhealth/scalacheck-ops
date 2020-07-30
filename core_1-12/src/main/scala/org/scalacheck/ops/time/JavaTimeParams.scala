package org.scalacheck.ops.time

import java.time.ZoneOffset
import java.time.chrono.{Chronology, IsoChronology}

case class JavaTimeParams(chronology: Chronology, zoneOffset: ZoneOffset)

object JavaTimeParams {

  val isoUTC = JavaTimeParams(IsoChronology.INSTANCE, ZoneOffset.UTC)

  implicit def fromImplicits(implicit chronology: Chronology, zoneOffset: ZoneOffset): JavaTimeParams =
    new JavaTimeParams(chronology, zoneOffset)
}
