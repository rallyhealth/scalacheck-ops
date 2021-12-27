import sbt._
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport.virtualAxes

import scala.reflect.ClassTag

object Dependencies {

  final val Scala_2_11 = "2.11.12"
  final val Scala_2_12 = "2.12.12"
  final val Scala_2_13 = "2.13.6"
  final val Scala_3 = "3.1.0"

  final private val ScalaTest_2_2 = "2.2.6"

  // Newer versions of ScalaTest separate the scalatestplus %% scalacheck-1-X dependencies,
  // but do not support ScalaCheck 1.13.x
  // Once we no longer support ScalaCheck 1.13.x, we can upgrade to the latest version of
  // ScalaTest and always pull in the appropriate ScalaTestPlus artifact for ScalaCheck >= 1.14
  final private val ScalaTest_3_0 = "3.0.5"
  final private val ScalaTest_3_2 = "3.2.9"

  private def scalaTestPlusScalaCheckVersion(scalaVer: String) = CrossVersion.partialVersion(scalaVer) match {
    case Some((3, _)) => "3.2.10.0"
    case _ => "3.2.2.0"
  }

  final private val IzumiReflectVersion = "1.1.2"
  final private val JodaTimeVersion = "2.10.10"
  final private val NewtypeVersion = "0.4.4"
  final private val TaggingVersion = "2.3.2"

  val izumiReflect: ModuleID = "dev.zio" %% "izumi-reflect" % IzumiReflectVersion
  val newtype: ModuleID = for3Use2("io.estatico" %% "newtype" % NewtypeVersion) % Test
  val jodaTime: ModuleID = "joda-time" % "joda-time" % JodaTimeVersion
  val tagging: ModuleID = "com.softwaremill.common" %% "tagging" % TaggingVersion

  case class ScalaCheckAxis(
    id: String,
    scalaCheckVersion: String,
    scalaTestVersion: String,
    scalaVersions: Seq[String]
  ) extends VirtualAxis.WeakAxis {

    override def idSuffix: String = s"_$id"

    override def directorySuffix: String = s"-$id"

    def artifact(subProject: Option[String]): String = subProject match {
      case Some(sub) => s"scalacheck-ops-${sub}_$id"
      case None => s"scalacheck-ops_$id"
    }

    def scalaCheck: ModuleID =
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion

    def scalaTest: ModuleID =
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test

    def scalaTestPlusScalaCheck(scalaVer: String): ModuleID =
      "org.scalatestplus" %% s"scalacheck-$id" % scalaTestPlusScalaCheckVersion(scalaVer) % Test
  }

  object ScalaCheckAxis extends CurrentAxis[ScalaCheckAxis] {
    val v1_12 = ScalaCheckAxis("1-12", "1.12.6", ScalaTest_2_2, Seq(Scala_2_11))
    val v1_13 = ScalaCheckAxis("1-13", "1.13.5", ScalaTest_3_0, Seq(Scala_2_11, Scala_2_12))
    val v1_14 = ScalaCheckAxis("1-14", "1.14.3", ScalaTest_3_2, Seq(Scala_2_11, Scala_2_12, Scala_2_13))
    val v1_15 = ScalaCheckAxis("1-15", "1.15.4", ScalaTest_3_2, Seq(Scala_2_12, Scala_2_13, Scala_3))
  }

  abstract class CurrentAxis[T: ClassTag] {

    def current: Def.Initialize[T] = Def.setting {
      virtualAxes.value.collectFirst { case a: T => a }.get
    }
  }

  def for3Use2(m: ModuleID) = m.cross(CrossVersion.for3Use2_13)

}

