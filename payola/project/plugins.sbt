logLevel := Level.Warn

resolvers ++= Seq(
    DefaultMavenRepository,
    "SBT IDEA Repository" at "http://mpeltonen.github.com/maven/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.url("Play", url("http://download.playframework.org/ivy-releases/"))(Resolver.ivyStylePatterns)
)

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

//addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")

// https://github.com/jrudolph/sbt-dependency-graph/
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.0")

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.0"
