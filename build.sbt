
// Aggregate root project settings only
name := "scalacheck-ops-root"
// don't publish the aggregate root project
publish := {}
publishLocal := {}

organization in ThisBuild := "com.rallyhealth"
organizationName in ThisBuild := "Rally Health"

semVerLimit in ThisBuild := "2.0.999"
scalaVersion in ThisBuild := "2.11.11"
licenses in ThisBuild := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

bintrayOrganization in ThisBuild := Some("rallyhealth")
bintrayRepository := "ivy-scala-libs"

def commonProject(id: String, artifact: String, path: String): Project = {
  Project(id, file(path)).settings(
    name := artifact,

    scalacOptions := Seq(
      "-Xfatal-warnings",
      "-deprecation:false",
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

    // required to address a weird bug where sbt-bintray forces this to be "maven"
    bintrayRepository := "ivy-scala-libs",

    // disable publishing empty ScalaDocs
    publishArtifact in (Compile, packageDoc) := false

  ).enablePlugins(GitVersioningPlugin, SemVerPlugin)
}

def suffixFor(scalaCheckVersion: String): String = scalaCheckVersion match {
  case Dependencies.scalaCheck12Version => "_1-12"
  case Dependencies.scalaCheck13Version => "_1-13"
  case Dependencies.scalaCheck14Version => "_1-14"
}

def coreProject(scalaCheckVersion: String): Project = {
  val projectPath = "core"
  val suffix = suffixFor(scalaCheckVersion)
  commonProject(s"core$suffix", s"scalacheck-ops$suffix", s"$projectPath$suffix").settings(
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
lazy val `core_1-14` = coreProject(Dependencies.scalaCheck14Version)

def jodaProject(scalaCheckVersion: String): Project = {
  val projectPath = "joda"
  val suffix = suffixFor(scalaCheckVersion)
  commonProject(s"joda$suffix", s"scalacheck-ops-joda$suffix", s"$projectPath$suffix").settings(
    sourceDirectory := file(s"$projectPath/src").getAbsoluteFile,
    (sourceDirectory in Compile) := file(s"$projectPath/src/main").getAbsoluteFile,
    (sourceDirectory in Test) := file(s"$projectPath/src/test").getAbsoluteFile,
    libraryDependencies ++= Seq(
      Dependencies.scalaCheck(scalaCheckVersion),
      Dependencies.jodaTime
    ) ++ Seq(
      // Test-only dependencies
      Dependencies.scalaTest(scalaCheckVersion)
    ).map(_ % Test)
  ).dependsOn(
    (scalaCheckVersion match {
      case Dependencies.scalaCheck12Version => `core_1-12`
      case Dependencies.scalaCheck13Version => `core_1-13`
      case Dependencies.scalaCheck14Version => `core_1-14`
    }) % "compile;test->test"
  )
}

lazy val `joda_1-12` = jodaProject(Dependencies.scalaCheck12Version)
lazy val `joda_1-13` = jodaProject(Dependencies.scalaCheck13Version)
lazy val `joda_1-14` = jodaProject(Dependencies.scalaCheck14Version)

