import Dependencies._

// Aggregate root project settings only
name := "scalacheck-ops-root"

ThisBuild / organization := "com.rallyhealth"
ThisBuild / organizationName := "Rally Health"

ThisBuild / scalaVersion := Scala_2_11
ThisBuild / licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

ThisBuild / bintrayOrganization := Some("rallyhealth")
ThisBuild / bintrayRepository := "maven"

resolvers in ThisBuild += Resolver.bintrayRepo("rallyhealth", "maven")

// don't publish the aggregate root project
publish := {}
publishLocal := {}

def commonProject(id: String, artifact: String, path: String): Project = {
  Project(id, file(path)).settings(
    name := artifact,

    scalacOptions := Seq(
      // "-Xfatal-warnings", // some methods in Scala 2.13 are deprecated, but I don't want to maintain to copies of source
      "-deprecation:false",
      "-feature",
      "-Xlint",
      "-Ywarn-dead-code",
      "-encoding", "UTF-8"
    ),

    // show full stack traces in test failures ()
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

    // disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
    Compile / doc / sources := Seq.empty,

    // disable publishing empty ScalaDocs
    Compile / packageDoc / publishArtifact := false

  ).enablePlugins(GitVersioningPlugin, SemVerPlugin)
}

def scSuffix(scalaCheckVersion: String): String = scalaCheckVersion match {
  case ScalaCheck_1_12 => "_1-12"
  case ScalaCheck_1_13 => "_1-13"
  case ScalaCheck_1_14 => "_1-14"
}

def scalaVersions(scalaCheckVersion: String): Seq[String] = scalaCheckVersion match {
  case ScalaCheck_1_12 => Seq(Scala_2_11)
  case ScalaCheck_1_13 => Seq(Scala_2_11, Scala_2_12)
  case ScalaCheck_1_14 => Seq(Scala_2_11, Scala_2_12, Scala_2_13)
}

def coreProject(scalaCheckVersion: String): Project = {
  val projectPath = "core"
  val suffix = scSuffix(scalaCheckVersion)
  commonProject(s"core$suffix", s"scalacheck-ops$suffix", s"$projectPath$suffix").settings(
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := scalaVersions(scalaCheckVersion),
    sourceDirectory := file(s"$projectPath/src").getAbsoluteFile,
    Compile / sourceDirectory := file(s"$projectPath/src/main").getAbsoluteFile,
    Test / sourceDirectory := file(s"$projectPath/src/test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      scalaCheck(scalaCheckVersion)
    ) ++ Seq(
      // Test-only dependencies
      scalaTest(scalaCheckVersion)
    ).map(_ % Test)
  )
}

lazy val `core_1-12` = coreProject(ScalaCheck_1_12)
lazy val `core_1-13` = coreProject(ScalaCheck_1_13)
lazy val `core_1-14` = coreProject(ScalaCheck_1_14)

def jodaProject(scalaCheckVersion: String): Project = {
  val projectPath = "joda"
  val suffix = scSuffix(scalaCheckVersion)
  commonProject(s"joda$suffix", s"scalacheck-ops-joda$suffix", s"$projectPath$suffix").settings(
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := scalaVersions(scalaCheckVersion),
    sourceDirectory := file(s"$projectPath/src").getAbsoluteFile,
    Compile / sourceDirectory := file(s"$projectPath/src/main").getAbsoluteFile,
    Test / sourceDirectory := file(s"$projectPath/src/test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      scalaCheck(scalaCheckVersion),
      jodaTime
    ) ++ Seq(
      // Test-only dependencies
      scalaTest(scalaCheckVersion)
    ).map(_ % Test)
  ).dependsOn(
    (scalaCheckVersion match {
      case ScalaCheck_1_12 => `core_1-12`
      case ScalaCheck_1_13 => `core_1-13`
      case ScalaCheck_1_14 => `core_1-14`
    }) % "compile;test->test"
  )
}

lazy val `joda_1-12` = jodaProject(ScalaCheck_1_12)
lazy val `joda_1-13` = jodaProject(ScalaCheck_1_13)
lazy val `joda_1-14` = jodaProject(ScalaCheck_1_14)

