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

def commonProject(id: String, artifact: String, path: String): Project = {
  Project(id, file(path)).settings(
    name := artifact,

    mimaPreviousArtifacts := Set(organization.value %% artifact % "2.6.0"),

    scalacOptions := Seq(
      // "-Xfatal-warnings", // some methods in Scala 2.13 are deprecated, but I don't want to maintain to copies of source
      "-deprecation:false",
      "-feature",
      "-Xlint",
      "-Ywarn-dead-code",
      "-encoding", "UTF-8"
    ),

    // show full stack traces in test failures
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

    // disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
    Compile / doc / sources := Seq.empty,

    // disable publishing empty ScalaDocs
    Compile / packageDoc / publishArtifact := false,

    // Don't publish the test artifacts, nobody should depend on these
    Test / publishArtifact := false,
  )
}

def scSuffix(scalaCheckVersion: String): String = scalaCheckVersion match {
  case ScalaCheck_1_12 => "_1-12"
  case ScalaCheck_1_13 => "_1-13"
  case ScalaCheck_1_14 => "_1-14"
  case ScalaCheck_1_15 => "_1-15"
}

def scalaVersions(scalaCheckVersion: String): Seq[String] = scalaCheckVersion match {
  case ScalaCheck_1_12 => Seq(Scala_2_11)
  case ScalaCheck_1_13 => Seq(Scala_2_11, Scala_2_12)
  case ScalaCheck_1_14 => Seq(Scala_2_11, Scala_2_12, Scala_2_13)
  case ScalaCheck_1_15 => Seq(Scala_2_11, Scala_2_12, Scala_2_13)
}

def coreProject(srcPath: File, testPath: File, scalaCheckVersion: String): Project = {
  val suffix = scSuffix(scalaCheckVersion)
  val targetPath = s"core$suffix"
  commonProject(targetPath, s"scalacheck-ops$suffix", targetPath).settings(
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := scalaVersions(scalaCheckVersion),
    Compile / sourceDirectory := (srcPath / "src" / "main").getAbsoluteFile,
    Test / sourceDirectory := (testPath / "src" / "test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      izumiReflect,
      scalaCheck(scalaCheckVersion),
      tagging,
    ) ++ {
      // Test-only dependencies
      Seq(
        scalaTest(scalaCheckVersion),
      ) ++ {
        scalaCheckVersion match {
          case ScalaCheck_1_12 | ScalaCheck_1_13 => Seq()
          case ScalaCheck_1_14 | ScalaCheck_1_15 => Seq(
            scalaTestPlusScalaCheck(scalaCheckVersion),
          )
        }
      }
    }.map(_ % Test)
  )
}

lazy val `core_1-12` = coreProject(file("core_1-12"), file("core_1-12"), ScalaCheck_1_12)
lazy val `core_1-13` = coreProject(file("core"), file("core-1-13-test"), ScalaCheck_1_13)
lazy val `core_1-14` = coreProject(file("core"), file("core"), ScalaCheck_1_14)
lazy val `core_1-15` = coreProject(file("core"), file("core"), ScalaCheck_1_15)


def jodaProject(scalaCheckVersion: String): Project = {
  val projectPath = "joda"
  val suffix = scSuffix(scalaCheckVersion)
  commonProject(s"joda$suffix", s"scalacheck-ops-joda$suffix", s"$projectPath$suffix").settings(
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := scalaVersions(scalaCheckVersion),
    Compile / sourceDirectory := file(s"$projectPath/src/main").getAbsoluteFile,
    Test / sourceDirectory := file(s"$projectPath/src/test").getAbsoluteFile,
    // don't include dependencies that come from scalacheck-ops core project
    libraryDependencies ++= Seq(
      jodaTime,
    )
  ).dependsOn(
    (scalaCheckVersion match {
      case ScalaCheck_1_12 => `core_1-12`
      case ScalaCheck_1_13 => `core_1-13`
      case ScalaCheck_1_14 => `core_1-14`
      case ScalaCheck_1_15 => `core_1-15`
    }) % "compile;test->test"
  )
}

lazy val `joda_1-12` = jodaProject(ScalaCheck_1_12)
lazy val `joda_1-13` = jodaProject(ScalaCheck_1_13)
lazy val `joda_1-14` = jodaProject(ScalaCheck_1_14)
lazy val `joda_1-15` = jodaProject(ScalaCheck_1_15)
