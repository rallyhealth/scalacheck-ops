
val commonRootSettings = Seq(
  name := "scalacheck-ops",
  organization := "com.rallyhealth",

  // scala version for root project
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8", "2.10.6"),

  licenses += ("Apache-2.0", url("http://opensource.org/licenses/apache-2.0"))
)

lazy val root = (project in file("."))
  .settings(commonRootSettings)
  .settings(
    name := "scalacheck-ops-root",
    // don't publish the surrounding multi-project root
    publish := {},
    publishLocal := {}
  )
  .aggregate(`core_1-12`, `core_1-13`)

// the version of scalacheck, all cross-compiled settings are derived from this setting
lazy val scalacheckVersion = settingKey[String]("version of scalacheck (all cross-compiled settings are derived from this setting)")

// using scalatest version 3.0.0 should be no problem
lazy val scalatestVersion = settingKey[String]("version of scalatest")

/**
  * Choose one value or another based on the version of scalacheck.
  */
def basedOnVersion[T](version: String, old: T, latest: T): T = {
  if (version startsWith "1.12.") old else latest
}

val commonSettings = commonRootSettings ++ Seq(

  // scalatest 2.2.6 pulls in scalacheck and it only works with 1.12.5
  // if you want to pull in the latest version, you should be fine to pull in scalacheck 1.13.x
  scalatestVersion := basedOnVersion(scalacheckVersion.value, "2.2.6", "3.0.0"),

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

  libraryDependencies ++= Seq(
    // pull in the specified version of scalacheck
    "org.scalacheck" %% "scalacheck" % scalacheckVersion.value,
    // scalatest 2.2.6 pulls in scalacheck 1.12.5 so it only works with 1.12.5
    "org.scalatest" %% "scalatest" % scalatestVersion.value % Test
  ),

  // show full stack traces in test failures ()
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

  // force scala version
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },

  // disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
  sources in(Compile, doc) := Seq.empty,

  // disable publishing empty ScalaDocs
  publishArtifact in (Compile, packageDoc) := false
)

val coreSettings = commonSettings ++ Seq(
  sourceDirectory := file("core/src").getAbsoluteFile,
  libraryDependencies ++= Seq(
    "org.joda" % "joda-convert" % "1.8",
    "joda-time" % "joda-time" % "2.9.4"
  )
)

lazy val `core_1-12` = (project in file("core"))
  .settings(coreSettings: _*)
  .settings(
    name := "scalacheck-ops",
    scalacheckVersion := "1.12.5"
  )

lazy val `core_1-13` = (project in file("core_1.13"))
  .settings(coreSettings: _*)
  .settings(
    name := "scalacheck-ops_1.13",
    scalacheckVersion := "1.13.2"
  )

