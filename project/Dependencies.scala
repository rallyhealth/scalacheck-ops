import sbt._
import sbt.Keys._

object Dependencies {

  final val Scala_2_11 = "2.11.12"
  final val Scala_2_12 = "2.12.6"
  final val Scala_2_13 = "2.13.0-M4"

  final val ScalaCheck_1_12 = "1.12.6"
  final val ScalaCheck_1_13 = "1.13.5"
  final val ScalaCheck_1_14 = "1.14.0"

  private val ScalaTest_2 = "2.2.6"
  private val ScalaTest_3 = "3.0.5"
  private val ScalaTest_3_M4 = "3.0.6-SNAP2"

  private val JodaTimeVersion = "2.10"

  val jodaTime: ModuleID = "joda-time" % "joda-time" % JodaTimeVersion

  def scalaCheck(scalaCheckVersion: String): ModuleID = {
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  }

  def scalaTest(scalaCheckVersion: String): ModuleID = {
    val scalaTestVersion = scalaCheckVersion match {
      case ScalaCheck_1_12 => ScalaTest_2
      case ScalaCheck_1_13 => ScalaTest_3
      case ScalaCheck_1_14 => ScalaTest_3_M4
    }
    "org.scalatest" %% "scalatest" % scalaTestVersion
  }
}

