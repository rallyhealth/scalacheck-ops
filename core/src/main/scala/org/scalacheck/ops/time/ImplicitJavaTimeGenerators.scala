package org.scalacheck.ops.time

import java.time._
import java.time.chrono._

import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

import scala.collection.JavaConverters._

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

  implicit val arbZoneId: Arbitrary[ZoneId] = {
    Arbitrary {
      Gen.oneOf(ZoneId.getAvailableZoneIds.asScala.toSeq.map(ZoneId.of))
    }
  }

  implicit val arbInstant: Arbitrary[Instant] = {
    Arbitrary {
      for {
        millis <- chooseNum(Instant.MIN.getEpochSecond, Instant.MAX.getEpochSecond)
        nanos <- chooseNum(Instant.MIN.getNano, Instant.MAX.getNano)
      } yield {
        Instant.ofEpochMilli(millis).plusNanos(nanos)
      }
    }
  }

  implicit val arbLocalDate: Arbitrary[LocalDate] = {
    Arbitrary {
      for {
        epochDay <- chooseNum(LocalDate.MIN.toEpochDay, LocalDate.MAX.toEpochDay)
      } yield LocalDate.ofEpochDay(epochDay)
    }
  }

  implicit val arbLocalTime: Arbitrary[LocalTime] = {
    Arbitrary {
      for {
        nanoOfDay <- chooseNum(LocalTime.MIN.toNanoOfDay, LocalTime.MAX.toNanoOfDay)
      } yield LocalTime.ofNanoOfDay(nanoOfDay)
    }
  }

  implicit lazy val arbLocalDateTime: Arbitrary[LocalDateTime] = {
    import ZoneOffset.UTC
    Arbitrary {
      for {
        seconds <- chooseNum(LocalDateTime.MIN.toEpochSecond(UTC), LocalDateTime.MAX.toEpochSecond(UTC))
        nanos <- chooseNum(LocalDateTime.MIN.getNano, LocalDateTime.MAX.getNano)
      } yield LocalDateTime.ofEpochSecond(seconds, nanos, UTC)
    }
  }

  implicit lazy val arbZonedDateTime: Arbitrary[ZonedDateTime] = {
    Arbitrary {
      for {
        zoneId <- arbZoneId.arbitrary
        instant <- arbInstant.arbitrary
      } yield ZonedDateTime.ofInstant(instant, zoneId)
    }
  }

}
