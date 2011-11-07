import sbt._
import Keys._

object PayolaBuild extends Build 
{
    val buildSettings = Defaults.defaultSettings ++ Seq (
        organization := "Payola",
        version := "0.1",
        scalaVersion := "2.9.1"
    )

	lazy val helloWorldProject = Project(
		"HelloWorld", 
		file("HelloWorld"),
		settings = buildSettings ++ Seq(
            libraryDependencies := Seq(
                "org.scalatest" %% "scalatest" % "1.6.1" % "test"
            ),
            resolvers ++= Seq(
                DefaultMavenRepository
            )
        )
	)

    // The play project is defined here for IntelliJ IDEA SBT plugin, so proper structure of the project
    // can be generated using gen-idea. This project isn't aggregated with the payolaProject, because
    // compilation has to be done using play console.
	lazy val playBetaProject = Project(
		"PlayBeta", 
		file("PlayBeta"),
		settings = buildSettings ++ Seq(
            libraryDependencies := Seq(
                "play" %% "play" % "2.0-beta" intransitive()
            ),
            resolvers ++= Seq(
                Resolver.url("Play", url("http://download.playframework.org/ivy-releases/"))(Resolver.ivyStylePatterns)
            )
        )
	)

    lazy val payolaProject = Project(
		"Payola",
		file("."),
		settings = buildSettings
	).aggregate(helloWorldProject)
}  