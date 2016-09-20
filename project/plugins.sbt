// See https://wiki.audaxhealth.com/display/ENG/Build+Structure#BuildStructure-Localconfiguration
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.url("Rally Plugin Releases", url("https://artifacts.werally.in/artifactory/ivy-plugins-release"))(Resolver.ivyStylePatterns)
 
addSbtPlugin("com.rallyhealth" %% "rally-versioning" % "latest.release") // must appear before rally-sbt-plugin which depends on version.
addSbtPlugin("com.rallyhealth" %% "rally-sbt-plugin" % "0.4.0")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.3.5")

