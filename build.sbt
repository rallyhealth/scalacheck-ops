
name := "scalacheck-ops"

organization := "com.rallyhealth"

libraryDependencies := Seq(
  "org.scalacheck" %% "scalacheck" % "1.12.2",
  "org.scalatest" %% "scalatest" % "2.2.4"
)

// force scala version
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
sources in(Compile, doc) := Seq.empty

// disable publishing empty ScalaDocs
publishArtifact in (Compile, packageDoc) := false

licenses += ("Apache-2.0", url("http://opensource.org/licenses/apache-2.0"))

