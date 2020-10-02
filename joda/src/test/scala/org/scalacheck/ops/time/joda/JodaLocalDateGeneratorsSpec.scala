package org.scalacheck.ops.time.joda

import org.joda.time.{LocalDate, LocalDateTime}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen.chooseNum
import org.scalacheck.ops.time.GenericDateTimeGeneratorsSpec
import org.scalacheck.ops.time.joda.ChronologyOps.fromChronology

import scala.reflect.ClassTag

final class JodaLocalDateGeneratorsSpec extends GenericDateTimeGeneratorsSpec(JodaLocalDateGenerators) {

  override protected val arbInstantType: Arbitrary[LocalDate] = {
    val margin = JodaLocalDateGenerators.defaultRange.toPeriod.plusDays(1) // so +/- margin doesn't put us out of range
    val chronology = JodaLocalDateGenerators.defaultParams
    val minMillis = chronology.minLocalDateTime.withPeriodAdded(margin, 1).toDate.getTime
    val maxMillis = chronology.maxLocalDateTime.withPeriodAdded(margin, -1).toDate.getTime
    Arbitrary(chooseNum(minMillis, maxMillis).flatMap(new LocalDateTime(_, chronology).toLocalDate))
  }
  override protected val clsTagInstantType: ClassTag[LocalDate] = implicitly[ClassTag[LocalDate]]
  override protected val orderingInstantType: Ordering[LocalDate] = Ordering.fromLessThan(_.compareTo(_) < 0)
}
