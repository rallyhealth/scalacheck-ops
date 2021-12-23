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
  Developer(id = "jeffmay", name = "Jeff May", email = "jeff.n.may@gmail.com", url = url("https://github.com/jeffmay")),
)

// don't look for previous versions of the root project, they don't exist
mimaFailOnNoPrevious := false

// don't publish the aggregate root project
publish / skip := true

def commonSettings(artifact: String): Seq[Setting[_]] =
  Seq(
    name := artifact,

    mimaPreviousArtifacts := (
      if (scalaBinaryVersion.value == "3") Set.empty // remove once _3 is published
      else Set(organization.value %% artifact % "2.6.0")
      ),

    scalacOptions ++= Seq(
      // "-Xfatal-warnings", // some methods in Scala 2.13 are deprecated, but I don't want to maintain to copies of source
      "-deprecation:false",
      "-feature",
      "-encoding", "UTF-8"
    ) ++ (if (scalaBinaryVersion.value == "3") Seq.empty else Seq(
      "-Xlint",
      "-Ywarn-dead-code",
    )),

    // show full stack traces in test failures
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

    // disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
    Compile / doc / sources := Seq.empty,

    // disable publishing empty ScalaDocs
    Compile / packageDoc / publishArtifact := false,

    // Don't publish the test artifacts, nobody should depend on these
    Test / publishArtifact := false
  )

lazy val `core` = projectMatrix
  .settings(
    // default locations, overridden in custom rows where needed
    Compile / sourceDirectory := (file("core") / "src" / "main").getAbsoluteFile,
    Test / sourceDirectory := (file("core") / "src" / "test").getAbsoluteFile,

    scalacOptions ++= (scalaVersion.value match {
      case Scala_2_13 => Seq("-Ymacro-annotations")
      case _ => Seq()
    }),

    libraryDependencies ++= Seq(
      izumiReflect,
      tagging,
      newtype // Test-only
    ) ++ (scalaVersion.value match {
      case Scala_2_11 | Scala_2_12 => Seq(
        compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
      )
      case _ => Seq()
    })
  )
  .customRow(
    scalaVersions = Seq(Scala_2_11),
    axisValues = Seq(ScalaCheckAxis.v1_12, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_12.artifact) ++ Seq(
      coverageEnabled := false, // Scala 2.11
      Compile / sourceDirectory := (file("core_1-12") / "src" / "main").getAbsoluteFile,
      Test / sourceDirectory := (file("core_1-12") / "src" / "test").getAbsoluteFile,
      libraryDependencies ++= Seq(
        ScalaCheckAxis.v1_12.scalaCheck,
        ScalaCheckAxis.v1_12.scalaTest
      )
    )
  )
  .customRow(
    scalaVersions = Seq(Scala_2_11),
    axisValues = Seq(ScalaCheckAxis.v1_13, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_13.artifact) ++ Seq(
      coverageEnabled := false, // Scala 2.11
      Test / sourceDirectory := (file("core-1-13-test") / "src" / "test").getAbsoluteFile,
      libraryDependencies ++= Seq(
        ScalaCheckAxis.v1_13.scalaCheck,
        ScalaCheckAxis.v1_13.scalaTest
      )
    )
  )
  .customRow(
    scalaVersions = Seq(Scala_2_12),
    axisValues = Seq(ScalaCheckAxis.v1_13, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_13.artifact) ++ Seq(
      Test / sourceDirectory := (file("core-1-13-test") / "src" / "test").getAbsoluteFile,
      libraryDependencies ++= Seq(
        ScalaCheckAxis.v1_13.scalaCheck,
        ScalaCheckAxis.v1_13.scalaTest
      )
    )
  )
  .customRow(
    scalaVersions = Seq(Scala_2_11),
    axisValues = Seq(ScalaCheckAxis.v1_14, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_14.artifact) ++ Seq(
      coverageEnabled := false, // Scala 2.11
      libraryDependencies ++= Seq(
        ScalaCheckAxis.v1_14.scalaCheck,
        ScalaCheckAxis.v1_14.scalaTest,
        ScalaCheckAxis.v1_14.scalaTestPlusScalaCheck(scalaVersion.value)
      )
    )
  )
  .customRow(
    scalaVersions = Seq(Scala_2_12, Scala_2_13),
    axisValues = Seq(ScalaCheckAxis.v1_14, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_14.artifact) ++ Seq(
      libraryDependencies ++= Seq(
        ScalaCheckAxis.v1_14.scalaCheck,
        ScalaCheckAxis.v1_14.scalaTest,
        ScalaCheckAxis.v1_14.scalaTestPlusScalaCheck(scalaVersion.value)
      )
    )
  )
  .customRow(
    scalaVersions = Seq(Scala_2_12, Scala_2_13, Scala_3),
    axisValues = Seq(ScalaCheckAxis.v1_15, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_15.artifact) ++ Seq(
      libraryDependencies ++= Seq(
        ScalaCheckAxis.v1_15.scalaCheck,
        ScalaCheckAxis.v1_15.scalaTest,
        ScalaCheckAxis.v1_15.scalaTestPlusScalaCheck(scalaVersion.value)
      )
    )
  )

lazy val `joda` = projectMatrix
  .dependsOn(`core` % "compile;test->test")
  .settings(
    Compile / sourceDirectory := file(s"joda/src/main").getAbsoluteFile,
    Test / sourceDirectory := file(s"joda/src/test").getAbsoluteFile,

    // don't include dependencies that come from scalacheck-ops core project
    libraryDependencies += jodaTime
  )
  .customRow(
    scalaVersions = Seq(Scala_2_11),
    axisValues = Seq(ScalaCheckAxis.v1_12, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_12.subArtifact("joda")) :+
      (coverageEnabled := false) // Scala 2.11
  )
  .customRow(
    scalaVersions = Seq(Scala_2_11),
    axisValues = Seq(ScalaCheckAxis.v1_13, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_13.subArtifact("joda")) :+
      (coverageEnabled := false) // Scala 2.11
  )
  .customRow(
    scalaVersions = Seq(Scala_2_12),
    axisValues = Seq(ScalaCheckAxis.v1_13, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_13.subArtifact("joda"))
  )
  .customRow(
    scalaVersions = Seq(Scala_2_11),
    axisValues = Seq(ScalaCheckAxis.v1_14, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_14.subArtifact("joda")) :+
      (coverageEnabled := false) // Scala 2.11
  )
  .customRow(
    scalaVersions = Seq(Scala_2_12, Scala_2_13),
    axisValues = Seq(ScalaCheckAxis.v1_14, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_14.subArtifact("joda"))
  )
  .customRow(
    scalaVersions = Seq(Scala_2_12, Scala_2_13, Scala_3),
    axisValues = Seq(ScalaCheckAxis.v1_15, VirtualAxis.jvm),
    settings = commonSettings(ScalaCheckAxis.v1_15.subArtifact("joda"))
  )
