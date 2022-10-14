import Dependencies._
import com.typesafe.tools.mima.core.{ProblemFilters, ReversedMissingMethodProblem}
import sbt.Test

// Aggregate root project settings only
name := "scalacheck-ops-root"
scalaVersion := Scala_2_13

ThisBuild / organization := "com.rallyhealth"
ThisBuild / organizationName := "Rally Health"
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/rallyhealth/scalacheck-ops"), "scm:git:git@github.com:rallyhealth/scalacheck-ops.git")
)
ThisBuild / homepage := Some(url("https://github.com/rallyhealth/scalacheck-ops"))
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

// reload sbt when the build files change
Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / developers := List(
  Developer(id = "jeffmay", name = "Jeff May", email = "jeff.n.may@gmail.com", url = url("https://github.com/jeffmay"))
)

// don't look for previous versions of the root project, they don't exist
mimaFailOnNoPrevious := false

// don't publish the aggregate root project
publish / skip := true

def commonSettings(subProject: Option[String]): Seq[Setting[_]] = {
  val artifact = ScalaCheckAxis.current(_.artifact(subProject))
  val mimaPreviousVersion = scalaBinaryVersion(v => if (v == "3") "2.8.0" else "2.6.0")
  Seq(
    name := artifact.value,
    mimaPreviousArtifacts := {
      val scVersion = ScalaCheckAxis.current.value.scalaCheckVersion
      CrossVersion.partialVersion(scVersion) match {
        case Some((1, x)) if x > 15 => Set.empty
        case _ =>
          Set(organization.value %% artifact.value % mimaPreviousVersion.value)
      }
    },
    mimaBinaryIssueFilters ++= Seq(
      ProblemFilters.exclude[ReversedMissingMethodProblem](
        "org.scalacheck.ops.time.ImplicitJavaTimeGenerators.arbOffsetDateTime"
      )
    ),
    scalacOptions ++= Seq(
      // "-Xfatal-warnings", // some methods in Scala 2.13 are deprecated, but I don't want to maintain to copies of source
      "-deprecation:false",
      "-feature",
      "-encoding",
      "UTF-8"
    ) ++ {
      if (scalaBinaryVersion.value == "3") Nil
      else {
        val scVersion = scalaVersion.value
        Seq(
          "-Xlint",
          "-Ywarn-dead-code"
        ) ++ {
          scVersion match {
            case Scala_2_13 =>
              Seq(
                "-Ymacro-annotations",
              )
            case _ => Nil
          }
        }
      }
    },

    // Allow publishing ScalaDoc by disabling link warnings
    Compile / doc / scalacOptions += "-no-link-warnings",
    Test / doc / scalacOptions += "-no-link-warnings",

    // show full stack traces in test failures
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),
  )
}

lazy val core = projectMatrix
  .settings(commonSettings(subProject = None))
  .settings(
    // default locations, overridden in custom rows where needed
    Compile / sourceDirectory := (file("core") / "src" / "main").getAbsoluteFile,
    Test / sourceDirectory := (file("core") / "src" / "test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      izumiReflect,
      tagging,
      ScalaCheckAxis.current.value.scalaCheck,
      newtype, // Test-only
      ScalaCheckAxis.current.value.scalaTest // Test-only
    ) ++ (scalaVersion.value match {
      case CrossVersion.PartialVersion("2", "12", _*) =>
        Seq(
          compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full))
        )
      case _ => Nil
    })
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_13.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_13, VirtualAxis.jvm),
    settings = Seq(
      Test / sourceDirectory := (file("core-1-13-test") / "src" / "test").getAbsoluteFile
    )
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_14.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_14, VirtualAxis.jvm),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_15.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_15, VirtualAxis.jvm),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1, VirtualAxis.jvm),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )
  .jsPlatform(
    scalaVersions = ScalaCheckAxis.v1_15.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_15),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )
  .jsPlatform(
    scalaVersions = ScalaCheckAxis.v1.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )
  .nativePlatform(
    scalaVersions = ScalaCheckAxis.v1_15.scalaVersions.filterNot(_ == Scala_2_12),
    axisValues = Seq(ScalaCheckAxis.v1_15),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )
  .nativePlatform(
    scalaVersions = ScalaCheckAxis.v1.scalaVersions.filterNot(_ == Scala_2_12),
    axisValues = Seq(ScalaCheckAxis.v1),
    settings = Seq(
      libraryDependencies +=
        ScalaCheckAxis.current.value.scalaTestPlusScalaCheck(scalaVersion.value)
    )
  )

lazy val joda = projectMatrix
  .settings(commonSettings(subProject = Some("joda")))
  .dependsOn(core % "compile;test->test")
  .settings(
    Compile / sourceDirectory := file(s"joda/src/main").getAbsoluteFile,
    Test / sourceDirectory := file(s"joda/src/test").getAbsoluteFile,

    // don't include dependencies that come from scalacheck-ops core project
    libraryDependencies += jodaTime
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_13.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_13, VirtualAxis.jvm),
    settings = Nil
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_14.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_14, VirtualAxis.jvm),
    settings = Nil
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_15.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_15, VirtualAxis.jvm),
    settings = Nil
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1, VirtualAxis.jvm),
    settings = Nil
  )
