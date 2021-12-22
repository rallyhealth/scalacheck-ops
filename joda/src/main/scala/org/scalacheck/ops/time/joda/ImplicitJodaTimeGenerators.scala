package org.scalacheck.ops.time.joda

import org.joda.time.chrono._
import org.joda.time.{Chronology, DateTime, DateTimeZone, LocalDateTime}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen._
import org.scalacheck.ops.time.joda.ChronologyOps._

import scala.collection.JavaConverters._

trait ImplicitJodaTimeGenerators {

  implicit val arbDateTimeZone: Arbitrary[DateTimeZone] = {
    val ids = DateTimeZone.getAvailableIDs.asScala.toSeq
    val zones = ids map DateTimeZone.forID
    Arbitrary(oneOf(zones))
  }

  implicit def arbChronology(implicit arbZone: Arbitrary[DateTimeZone]): Arbitrary[Chronology] = {
    val chronologyBuilders: Seq[DateTimeZone => Chronology] = Seq(
      BuddhistChronology.getInstance,
      CopticChronology.getInstance,
      EthiopicChronology.getInstance,
      GregorianChronology.getInstance,
      IslamicChronology.getInstance,
      ISOChronology.getInstance,
      JulianChronology.getInstance
    )
    Arbitrary {
      for {
        build <- oneOf(chronologyBuilders)
        zone <- arbZone.arbitrary
      } yield build(zone)
    }
  }

  implicit def arbDateTime(implicit params: JodaTimeParams = JodaDateTimeGenerators.defaultParams): Arbitrary[DateTime] = {
    val maxMillis = params.chronology.maxMillis
    Arbitrary(chooseNum(0L, maxMillis).flatMap(new DateTime(_, params.chronology)))
  }

  implicit def arbLocalDateTime(implicit params: JodaTimeParams = JodaLocalDateTimeGenerators.defaultParams): Arbitrary[LocalDateTime] = {
    val maxMillis = params.chronology.maxMillis
    Arbitrary(chooseNum(0L, maxMillis).flatMap(new LocalDateTime(_, params.chronology)))
  }

}
