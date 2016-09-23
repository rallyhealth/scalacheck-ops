package org.scalacheck

import org.scalacheck.ops.time.JavaTimeImplicits
import org.scalacheck.ops.time.joda.JodaTimeImplicits

/**
 * @note [[JodaTimeImplicits]] are included as well, since this library is pretty much the de-facto
 *       date / time library for Java / Scala. This may change in future versions if this is
 *       somehow no longer the case.
 */
package object ops extends ScalaCheckImplicits with JodaTimeImplicits with JavaTimeImplicits
