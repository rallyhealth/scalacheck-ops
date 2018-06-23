import sbt._
import sbt.Keys._

object Dependencies {

  final val scalaCheck12Version = "1.12.6"
  final val scalaCheck13Version = "1.13.4"
  final val scalaCheck14Version = "1.14.0"

  private val scalaTest2Version = "2.2.6"
  private val scalaTest3Version = "3.0.5"

  private val jodaTimeVersion = "2.10"

  val jodaTime: ModuleID = "joda-time" % "joda-time" % jodaTimeVersion

  def scalaCheck(scalaCheckVersion: String): ModuleID = {
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  }

  def scalaTest(scalaCheckVersion: String): ModuleID = {
    val scalaTestVersion = scalaCheckVersion match {
      case `scalaCheck12Version` => scalaTest2Version
      case `scalaCheck13Version` => scalaTest3Version
      case `scalaCheck14Version` => scalaTest3Version
    }
    "org.scalatest" %% "scalatest" % scalaTestVersion
  }
}

