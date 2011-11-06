import sbt._
import Keys._

object PayolaBuild extends Build 
{
    import Dependencies._
	import BuildSettings._
	
	lazy val helloWorldProject = Project(
		"HelloWorld", 
		file("HelloWorld"),
		settings = buildSettings ++ Seq(
            libraryDependencies := testDependencies,
            resolvers ++= Seq(DefaultMavenRepository)
        )
	)
	
	lazy val playBetaProject = Project(
		"PlayBeta", 
		file("PlayBeta"),
		settings = buildSettings
	)

    lazy val payolaProject = Project(
		"Payola",
		file("."),
		settings = buildSettings
	).aggregate(helloWorldProject, playBetaProject)

	object BuildSettings {
        val buildSettings = Defaults.defaultSettings ++ Seq (
            organization := "Payola",
            version := "0.1",
            scalaVersion := "2.9.1"
        )
    }

	object Dependencies {
		val testDependencies = Seq(
			"org.scalatest" %% "scalatest" % "1.6.1" % "test"
		)
	}
}  