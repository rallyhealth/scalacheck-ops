package org.scalacheck.ops.time

import java.time.chrono._
import java.time.{Instant, LocalDateTime, ZoneOffset, ZonedDateTime}

import org.scalacheck.Arbitrary
import org.scalacheck.Gen._

object ImplicitJavaTimeGenerators extends ImplicitJavaTimeGenerators
trait ImplicitJavaTimeGenerators {

  implicit val arbChronology: Arbitrary[Chronology] = {
    Arbitrary(oneOf(Seq(
      HijrahChronology.INSTANCE,
      IsoChronology.INSTANCE,
      JapaneseChronology.INSTANCE,
      MinguoChronology.INSTANCE,
      ThaiBuddhistChronology.INSTANCE
    )))
  }

  implicit val arbZoneOffset: Arbitrary[ZoneOffset] = {
    Arbitrary {
      for {
        totalSeconds <- chooseNum(ZoneOffset.MIN.getTotalSeconds, ZoneOffset.MAX.getTotalSeconds)
      } yield ZoneOffset.ofTotalSeconds(totalSeconds)
    }
  }

  implicit val arbInstant: Arbitrary[Instant] = {
    Arbitrary {
      for {
        millis <- chooseNum(0L, Instant.MAX.getEpochSecond)
        nanos <- chooseNum(0, Instant.MAX.getNano)
      } yield {
        Instant.ofEpochMilli(millis).plusNanos(nanos)
      }
    }
  }

  implicit val arbLocalDateTime: Arbitrary[LocalDateTime] = {
    Arbitrary {
      for {
        instant <- arbInstant.arbitrary
      } yield LocalDateTime.from(instant)
    }
  }

  implicit def arbZonedDateTime(implicit params: JavaTimeParams = JavaTimeGenerators.defaultParams): Arbitrary[ZonedDateTime] = {
    Arbitrary {
      for {
        instant <- arbInstant.arbitrary
      } yield ZonedDateTime.from(instant).withZoneSameInstant(params.zoneOffset)
    }
  }

}
