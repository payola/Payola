logLevel := Level.Warn

resolvers ++= Seq(
    DefaultMavenRepository,
    "SBT IDEA Repository" at "http://mpeltonen.github.com/maven/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.url("Play", url("http://download.playframework.org/ivy-releases/"))(Resolver.ivyStylePatterns)
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")

addSbtPlugin("play" % "sbt-plugin" % "2.0")

libraryDependencies += "play" %% "play" % "2.0"
