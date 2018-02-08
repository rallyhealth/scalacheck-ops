import sbt._
import sbt.Keys._

object Dependencies {

  final val scalaCheck12Version = "1.12.6"
  final val scalaCheck13Version = "1.13.4"

  private val scalaTest2Version = "2.2.6"
  private val scalaTest3Version = "3.0.4"

  private val jodaTimeVersion = "1.8"

  val jodaConvert: ModuleID = "org.joda" % "joda-convert" % "1.8"
  val jodaTime: ModuleID = "joda-time" % "joda-time" % "2.9.4"

  def scalaCheck(scalaCheckVersion: String): ModuleID = {
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  }

  def scalaTest(scalaCheckVersion: String): ModuleID = {
    val scalaTestVersion = scalaCheckVersion match {
      case `scalaCheck12Version` => scalaTest2Version
      case `scalaCheck13Version` => scalaTest3Version
    }
    "org.scalatest" %% "scalatest" % scalaTestVersion
  }
}

