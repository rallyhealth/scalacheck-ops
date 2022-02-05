import Dependencies._
import sbt.Test

// Aggregate root project settings only
name := "scalacheck-ops-root"
scalaVersion := Scala_2_13

ThisBuild / organization := "com.rallyhealth"
ThisBuild / organizationName := "Rally Health"

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

    mimaPreviousArtifacts := Set(organization.value %% artifact.value % mimaPreviousVersion.value),

    scalacOptions ++= Seq(
      // "-Xfatal-warnings", // some methods in Scala 2.13 are deprecated, but I don't want to maintain to copies of source
      "-deprecation:false",
      "-feature",
      "-encoding", "UTF-8"
    ) ++ (if (scalaBinaryVersion.value == "3") Nil else Seq(
      "-Xlint",
      "-Ywarn-dead-code"
    )),

    // show full stack traces in test failures
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

    // Don't publish the test artifacts, nobody should depend on these
    Test / publishArtifact := false,

    // Disable coverage for Scala 2.11 -- sbt-scoverage no longer supports it
    coverageEnabled := (if (scalaBinaryVersion.value == "2.11") false else coverageEnabled.value)
  )
}

lazy val `core` = projectMatrix
  .settings(commonSettings(subProject = None))
  .settings(
    // default locations, overridden in custom rows where needed
    Compile / sourceDirectory := (file("core") / "src" / "main").getAbsoluteFile,
    Test / sourceDirectory := (file("core") / "src" / "test").getAbsoluteFile,

    scalacOptions ++= (scalaVersion.value match {
      case Scala_2_13 => Seq("-Ymacro-annotations")
      case _ => Nil
    }),

    libraryDependencies ++= Seq(
      izumiReflect,
      tagging,
      ScalaCheckAxis.current.value.scalaCheck,
      newtype, // Test-only
      ScalaCheckAxis.current.value.scalaTest // Test-only
    ) ++ (scalaVersion.value match {
      case Scala_2_11 | Scala_2_12 => Seq(
        compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
      )
      case _ => Nil
    })
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_12.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_12, VirtualAxis.jvm),
    settings = Seq(
      Compile / sourceDirectory := (file("core_1-12") / "src" / "main").getAbsoluteFile,
      Test / sourceDirectory := (file("core_1-12") / "src" / "test").getAbsoluteFile
    )
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

lazy val `joda` = projectMatrix
  .settings(commonSettings(subProject = Some("joda")))
  .dependsOn(`core` % "compile;test->test")
  .settings(
    Compile / sourceDirectory := file(s"joda/src/main").getAbsoluteFile,
    Test / sourceDirectory := file(s"joda/src/test").getAbsoluteFile,

    // don't include dependencies that come from scalacheck-ops core project
    libraryDependencies += jodaTime
  )
  .customRow(
    scalaVersions = ScalaCheckAxis.v1_12.scalaVersions,
    axisValues = Seq(ScalaCheckAxis.v1_12, VirtualAxis.jvm),
    settings = Nil
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
