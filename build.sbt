// Aggregate root project settings only
name := "scalacheck-ops-root"
ThisBuild / scalaVersion := "2.11.8"

val ScalaCheck_1_12 = "1.12.5"
val ScalaCheck_1_13 = "1.13.2"

val ScalaTest_2 = "2.2.6"
val ScalaTest_3 = "3.0.0"

ThisBuild / organization := "com.rallyhealth"
ThisBuild / organizationName := "Rally Health"

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

// reload sbt when the build files change
Global / onChangedBuildSource := ReloadOnSourceChanges

developers := List(
  Developer(id = "jeffmay", name = "Jeff May", email = "jeff.n.may@gmail.com", url = url("https://github.com/jeffmay")),
)

// don't look for previous versions any project, this is the oldest legacy version we support
ThisBuild / mimaFailOnNoPrevious := false

// don't publish the aggregate root project
publish / skip := true

def commonProject(id: String, artifact: String, path: String): Project = {
  Project(id, file(path)).settings(
    name := artifact,

    scalacOptions := {
      // the deprecation:false flag is only supported by scala >= 2.11.3, but needed for scala >= 2.11.0 to avoid warnings
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMinor)) if scalaMinor >= 11 =>
          // For scala versions >= 2.11.3
          Seq("-Xfatal-warnings", "-deprecation:false")
        case Some((2, scalaMinor)) if scalaMinor < 11 =>
          // For scala versions 2.10.x
          Seq("-Xfatal-warnings")
      }
    } ++ Seq(
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
    Compile / packageDoc / publishArtifact := false
  )
}

def coreProject(scalaCheckVersion: String, scalatestVersion: String): Project = {
  val (path, artifactName) = scalaCheckVersion match {
    case ScalaCheck_1_12 => ("core_1-12", "scalacheck-ops_1-12")
    case ScalaCheck_1_13 => ("core_1-13", "scalacheck-ops_1-13")
  }
  commonProject(artifactName, artifactName, path).settings(
    crossScalaVersions := Seq("2.11.8", "2.10.6"),
    sourceDirectory := file("core/src").getAbsoluteFile,
    libraryDependencies ++= Seq(
      "org.joda" % "joda-convert" % "1.8",
      "joda-time" % "joda-time" % "2.9.4",
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion % Test
    )
  )
}

lazy val `core_1-12` = coreProject(ScalaCheck_1_12, ScalaTest_2)
lazy val `core_1-13` = coreProject(ScalaCheck_1_13, ScalaTest_3)

