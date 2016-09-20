// Aggregate root project settings only
name := "scalacheck-ops-root"
// don't publish the aggregate root project
publish := {}
publishLocal := {}

organization in ThisBuild := "me.jeffmay"
organizationName in ThisBuild := "Jeff May"

semVerLimit in ThisBuild := "2.0.999"

scalaVersion in ThisBuild := "2.11.11"
crossScalaVersions in ThisBuild := Seq("2.11.8", "2.10.6")

licenses in ThisBuild += ("Apache-2.0", url("http://opensource.org/licenses/apache-2.0"))

resolvers in ThisBuild += "jeffmay" at "https://dl.bintray.com/jeffmay/maven"

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

    // show full stack traces in test failures ()
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

    // force scala version
    ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },

    // disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
    sources in(Compile, doc) := Seq.empty,

    // disable publishing empty ScalaDocs
    publishArtifact in (Compile, packageDoc) := false

  ).enablePlugins(GitVersioningPlugin, SemVerPlugin)
}

def coreProject(scalaCheckVersion: String): Project = {
  // TODO: Simplify this by publishing the 1.12 branch as _1-12 and naming the directory the same
  // All future versions should be > 1.13 and have no suffix
  val projectPath = s"core"
  val pathSuffix = scalaCheckVersion match {
    case Dependencies.scalaCheck12Version => "_1.12"
    case Dependencies.scalaCheck13Version => "_1.13"
  }
  val artifactSuffix = scalaCheckVersion match {
    case Dependencies.scalaCheck12Version => ""
    case Dependencies.scalaCheck13Version => "_1-13"
  }
  val idSuffix = scalaCheckVersion match {
    case Dependencies.scalaCheck12Version => "_1-12"
    case Dependencies.scalaCheck13Version => "_1-13"
  }
  commonProject(s"core$idSuffix", s"scalacheck-ops$artifactSuffix", s"$projectPath$pathSuffix").settings(
    sourceDirectory := file(s"$projectPath/src").getAbsoluteFile,
    (sourceDirectory in Compile) := file(s"$projectPath/src/main").getAbsoluteFile,
    (sourceDirectory in Test) := file(s"$projectPath/src/test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      Dependencies.scalaCheck(scalaCheckVersion)
    ) ++ Seq(
      // Test-only dependencies
      Dependencies.scalaTest(scalaCheckVersion)
    ).map(_ % Test)
  )
}

lazy val `core_1-12` = coreProject(Dependencies.scalaCheck12Version)
lazy val `core_1-13` = coreProject(Dependencies.scalaCheck13Version)

def jodaProject(scalaCheckVersion: String): Project = {
  // TODO: Simplify this by publishing the 1.12 branch as _1-12 and naming the directory the same
  // All future versions should be > 1.13 and have no suffix
  val projectPath = s"joda"
  val pathSuffix = scalaCheckVersion match {
    case Dependencies.scalaCheck12Version => "_1.12"
    case Dependencies.scalaCheck13Version => "_1.13"
  }
  val artifactSuffix = scalaCheckVersion match {
    case Dependencies.scalaCheck12Version => ""
    case Dependencies.scalaCheck13Version => "_1-13"
  }
  val idSuffix = scalaCheckVersion match {
    case Dependencies.scalaCheck12Version => "_1-12"
    case Dependencies.scalaCheck13Version => "_1-13"
  }
  commonProject(s"joda$idSuffix", s"scalacheck-ops-joda$artifactSuffix", s"$projectPath$pathSuffix").settings(
    sourceDirectory := file(s"$projectPath/src").getAbsoluteFile,
    (sourceDirectory in Compile) := file(s"$projectPath/src/main").getAbsoluteFile,
    (sourceDirectory in Test) := file(s"$projectPath/src/test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      Dependencies.scalaCheck(scalaCheckVersion),
      Dependencies.jodaConvert,
      Dependencies.jodaTime
    ) ++ Seq(
      // Test-only dependencies
      Dependencies.scalaTest(scalaCheckVersion)
    ).map(_ % Test)
  ).dependsOn(
    (if (scalaCheckVersion.startsWith("1.12")) `core_1-12` else `core_1-13`) % "compile;test->test"
  )
}

lazy val `joda_1-12` = jodaProject(Dependencies.scalaCheck12Version)
lazy val `joda_1-13` = jodaProject(Dependencies.scalaCheck13Version)

