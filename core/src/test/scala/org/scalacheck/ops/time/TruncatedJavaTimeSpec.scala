package org.scalacheck.ops

package time

import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks._

import java.time.{Instant, LocalDateTime}
import java.time.temporal.{ChronoField, ChronoUnit}

class TruncatedJavaTimeSpec extends AnyFreeSpec {

  "Gen[Instant]" - {
    val it = "Gen[Instant]"

    s"$it.truncatedToMillis removes all nanoseconds and milliseconds" in {
      forAll(Gen.javaInstant.beforeNow.truncatedToMillis) { (instant: Instant) =>
        assertResult(0)(instant.getLong(ChronoField.NANO_OF_SECOND) % 1000)
      }
    }
  }

  "Gen[LocalDateTime]" - {
    val it = "Gen[LocalDateTime]"

    s"$it.truncatedToMillis removes all nanoseconds and milliseconds" in {
      forAll(Gen.javaLocalDateTime.beforeNow.truncatedToMillis) { (datetime: LocalDateTime) =>
        assertResult(0)(datetime.getLong(ChronoField.NANO_OF_SECOND) % 1000)
      }
    }

    s"$it.truncatedTo(ChronoUnit.SECONDS) removes all milliseconds" in {
      forAll(Gen.javaLocalDateTime.beforeNow.truncatedTo(ChronoUnit.SECONDS)) { (datetime: LocalDateTime) =>
        assertResult(0)(datetime.getLong(ChronoField.MILLI_OF_SECOND))
      }
    }

    s"$it.truncatedTo(ChronoUnit.MINUTES) removes all seconds" in {
      forAll(Gen.javaLocalDateTime.beforeNow.truncatedTo(ChronoUnit.MINUTES)) { (datetime: LocalDateTime) =>
        assertResult(0)(datetime.getLong(ChronoField.SECOND_OF_MINUTE))
      }
    }

    s"$it.truncatedTo(ChronoUnit.HOURS) removes all seconds" in {
      forAll(Gen.javaLocalDateTime.beforeNow.truncatedTo(ChronoUnit.HOURS)) { (datetime: LocalDateTime) =>
        assertResult(0)(datetime.getLong(ChronoField.MINUTE_OF_HOUR))
      }
    }
  }
}
