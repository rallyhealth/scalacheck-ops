package org.scalacheck.ops.time

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDateTime}

import org.scalacheck.Gen

trait TruncatedJavaTimeImplicits {
  import scala.language.implicitConversions

  implicit def truncatedInstantGen(instantGen: Gen[Instant]): TruncatedInstantGen = {
    new TruncatedInstantGen(instantGen)
  }

  implicit def truncatedLocalDateTimeGen(localDateTimeGen: Gen[LocalDateTime]): TruncatedLocalDateTimeGen = {
    new TruncatedLocalDateTimeGen(localDateTimeGen)
  }
}

class TruncatedInstantGen(val instantGen: Gen[Instant]) extends AnyVal {
  def truncatedToMillis: Gen[Instant] = instantGen.map(_.truncatedTo(ChronoUnit.MILLIS))
}

class TruncatedLocalDateTimeGen(val localDateTimeGen: Gen[LocalDateTime]) extends AnyVal {
  def truncatedToMillis: Gen[LocalDateTime] = localDateTimeGen.map(_.truncatedTo(ChronoUnit.MILLIS))
  def truncatedTo(unit: ChronoUnit): Gen[LocalDateTime] = localDateTimeGen.map(_.truncatedTo(unit))
}
