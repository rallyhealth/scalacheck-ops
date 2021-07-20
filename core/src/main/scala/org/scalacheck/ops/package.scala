package org.scalacheck

import org.scalacheck.ops.time.TruncatedJavaTimeImplicits

/**
  * @note if you would like joda DateTime implicits to be included (as they were in past versions of scalacheck-ops),
  *       you will need to include the scalacheck-ops-joda library and import org.scalacheck.ops.time.joda._.
  */
package object ops
  extends ScalaCheckImplicits
    with ImplicitGenFromConfig
    with TruncatedJavaTimeImplicits
