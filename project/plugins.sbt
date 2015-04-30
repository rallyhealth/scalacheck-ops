// See https://wiki.audaxhealth.com/display/ENG/Build+Structure#BuildStructure-Localconfiguration
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += "Rally Plugin Releases" at "https://artifacts.werally.in/artifactory/plugins-release"

addSbtPlugin("com.rallyhealth" %% "rally-versioning" % "0.2.0")
