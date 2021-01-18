import sbt._

object Dependencies {

  final val Scala_2_11 = "2.11.12"
  final val Scala_2_12 = "2.12.13"
  final val Scala_2_13 = "2.13.0"

  final val ScalaCheck_1_12 = "1.12.6"
  final val ScalaCheck_1_13 = "1.13.5"
  final val ScalaCheck_1_14 = "1.14.0"

  private val ScalaTest_2 = "2.2.6"
  private val ScalaTest_3 = "3.0.8"

  private val JodaTimeVersion = "2.10"
  private val TaggingVersion = "2.2.1"

  val jodaTime: ModuleID = "joda-time" % "joda-time" % JodaTimeVersion
  val tagging: ModuleID = "com.softwaremill.common" %% "tagging" % TaggingVersion

  def scalaCheck(scalaCheckVersion: String): ModuleID = {
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  }

  def scalaTest(scalaCheckVersion: String): ModuleID = {
    val scalaTestVersion = scalaCheckVersion match {
      case ScalaCheck_1_12 => ScalaTest_2
      case ScalaCheck_1_13 | ScalaCheck_1_14 => ScalaTest_3
    }
    "org.scalatest" %% "scalatest" % scalaTestVersion
  }
}

