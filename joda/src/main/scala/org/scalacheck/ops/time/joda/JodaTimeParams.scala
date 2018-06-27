package org.scalacheck.ops.time.joda

import org.joda.time.{Chronology, DateTimeZone}

case class JodaTimeParams(chronology: Chronology, dateTimeZone: DateTimeZone)
object JodaTimeParams {
  implicit def fromImplicits(implicit chronology: Chronology, dateTimeZone: DateTimeZone): JodaTimeParams =
    new JodaTimeParams(chronology, dateTimeZone)
}
