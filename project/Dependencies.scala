import sbt._
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport.virtualAxes

import scala.reflect.ClassTag

object Dependencies {

  final val Scala_2_12 = "2.12.17"
  final val Scala_2_13 = "2.13.10"
  final val Scala_3 = "3.2.0"

  // Newer versions of ScalaTest separate the scalatestplus %% scalacheck-1-X dependencies,
  // but do not support ScalaCheck 1.13.x
  // Once we no longer support ScalaCheck 1.13.x, we can upgrade to the latest version of
  // ScalaTest and always pull in the appropriate ScalaTestPlus artifact for ScalaCheck >= 1.14
  private final val ScalaTest_3_0 = "3.0.5"
  private final val ScalaTest_3_2_9 = "3.2.9"
  private final val ScalaTest_3_2_14 = "3.2.14"

  // TODO: Upgrade to 2.x in next major version
  // Or use the strategy outlined in https://blog.7mind.io/no-more-orphans.html
  val izumiReflect: ModuleID = "dev.zio" %% "izumi-reflect" % "2.3.1"
  val newtype: ModuleID = for3Use2("io.estatico" %% "newtype" % "0.4.4") % Test
  val jodaTime: ModuleID = "joda-time" % "joda-time" % "2.11.2"
  val tagging: ModuleID = "com.softwaremill.common" %% "tagging" % "2.3.3"

  case class ScalaCheckAxis(
    id: String,
    scalaCheckVersion: String,
    scalaTestVersion: String,
    scalaVersions: Seq[String]
  ) extends VirtualAxis.WeakAxis {

    private val scPartialVersion = CrossVersion.partialVersion(scalaCheckVersion).getOrElse {
      throw new IllegalArgumentException(s"scalaCheckVersion '$scalaCheckVersion' is not a valid semantic version.")
    }

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

    def scalaTestPlusScalaCheck(scalaVer: String): ModuleID = {
      val version = (CrossVersion.partialVersion(scalaVer), scPartialVersion) match {
        case (_, (1, x)) if x > 15 => "3.2.14.0"
        case (Some((3, _)), _) => "3.2.10.0"
        case _ => "3.2.2.0"
      }
      val suffix = scPartialVersion.productIterator.mkString("-")
      "org.scalatestplus" %% s"scalacheck-$suffix" % version % Test
    }
  }

  object ScalaCheckAxis extends CurrentAxis[ScalaCheckAxis] {
    final val v1_13 = ScalaCheckAxis("1-13", "1.13.5", ScalaTest_3_0, Seq(Scala_2_12))
    final val v1_14 = ScalaCheckAxis("1-14", "1.14.3", ScalaTest_3_2_9, Seq(Scala_2_12, Scala_2_13))
    final val v1_15 = ScalaCheckAxis("1-15", "1.15.4", ScalaTest_3_2_14, Seq(Scala_2_12, Scala_2_13, Scala_3))

    // All scalacheck releases after 1.14 use semantic versioning, so it should be safe to pin to the latest
    // version of scalacheck v1.x at this point. If this changes, we can continue to add version specific aliases
    // as well.
    final val v1 = ScalaCheckAxis("1", "1.17.0", ScalaTest_3_2_14, Seq(Scala_2_12, Scala_2_13, Scala_3))
  }

  abstract class CurrentAxis[T : ClassTag] {

    def current: Def.Initialize[T] = Def.setting {
      virtualAxes.value.collectFirst { case a: T => a }.get
    }
  }

  def for3Use2(m: ModuleID): ModuleID = m.cross(CrossVersion.for3Use2_13)

}
