// Your profile name of the sonatype account. The default is the same with the organization value
sonatypeProfileName := "com.rallyhealth"

// To sync with Maven central, you need to supply the following information:
publishMavenStyle := true

// publish to Maven Central
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := Some {
  if (isSnapshot.value)
    Resolver.url("Sonatype", url("https://s01.oss.sonatype.org/content/repositories/snapshots"))
  else
    Resolver.url("Sonatype", url("https://s01.oss.sonatype.org/content/repositories/releases"))
}

// add SNAPSHOT to non-release versions so they are not published to the main repo
ThisBuild / dynverSonatypeSnapshots := true
// Use '-' instead of '+' for simpler snapshot URLs
ThisBuild / dynverSeparator := "-"

import xerial.sbt.Sonatype.GitHubHosting
sonatypeProjectHosting := Some(GitHubHosting("rallyhealth", "scalacheck-ops", "jeff.n.may@gmail.com"))
