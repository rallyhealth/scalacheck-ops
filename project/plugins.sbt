resolvers += Classpaths.sbtPluginReleases

resolvers += Resolver.url(
  "Rally Plugin Releases",
  url("https://dl.bintray.com/rallyhealth/sbt-plugins")
)(Resolver.ivyStylePatterns)

resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases")
)(Resolver.ivyStylePatterns)

addSbtPlugin("com.rallyhealth.sbt" %% "sbt-git-versioning" % "1.2.0")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")
