resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("https://dl.bintray.com/content/sbt/sbt-plugin-releases")
)(Resolver.ivyStylePatterns)

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.8.1")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.6.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
