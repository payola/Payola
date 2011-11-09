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

    lazy val scalaToJsProject = Project(
        "ScalaToJs",
        file("PlayBeta/ScalaToJs"),
        settings = buildSettings ++ Seq(
            // Whole path to the compiler plugin needs to be added, because scala compiler looks for the plugins
            // only in SCALA_HOME.
            scalacOptions += "-Xplugin:" + file("PlayBeta/ScalaToJs/lib/s2js_2.9.0-0.1-SNAPSHOT.jar").getAbsolutePath,
            scalacOptions += "-P:s2js:output:PlayBeta/public/javascripts"
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
        helloWorldProject,
        scalaToJsProject
    )

    lazy val payolaProject = Project(
        "Payola",
        file("."),
        settings = buildSettings
    ).aggregate(
        helloWorldProject,
        scalaToJsProject,
        playBetaProject
    )
}  