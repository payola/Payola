import sbt._
import Keys._
import PlayProject._

object PayolaBuild extends Build {
    
    object BuildSettings {
        val organization = "Payola"
        val version = "0.1"
        val scalaVersion = "2.9.1"
    }

    val buildSettings = Defaults.defaultSettings ++ Seq(
        organization := BuildSettings.organization,
        version := BuildSettings.version,
        scalaVersion := BuildSettings.scalaVersion
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

    lazy val playBetaProject = PlayProject(
        "PlayBeta", // Name of the project.
        BuildSettings.version, // Version of the project.
        Nil, // Library dependencies.
        file("PlayBeta") // Path to the project.
    ).settings(
        defaultScalaSettings: _*
    ).dependsOn(
        helloWorldProject
    )

    lazy val payolaProject = Project(
        "Payola",
        file("."),
        settings = buildSettings
    ).aggregate(
        helloWorldProject,
        playBetaProject
    )
}  