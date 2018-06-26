package org.scalacheck.ops.time

package object joda extends JodaTimeImplicits {

  @deprecated("Use JodaDateTimeGenerators instead.", "2.0.0")
  type DateTimeGenerators = JodaDateTimeGenerators

  @deprecated("Use JodaLocalDateTimeGenerators instead.", "2.0.0")
  type LocalDateTimeGenerators = JodaLocalDateTimeGenerators
}
