import sbt._
import Keys._
import PlayProject._

object PayolaBuild extends Build
{

    object PayolaSettings
    {
        val organization = "Payola"
        val version = "0.1"
        val scalaVersion = "2.9.1"
    }

    object ScalaToJsSettings
    {
        val version = "0.2"
    }

    val payolaSettings = Defaults.defaultSettings ++ Seq(
        organization := PayolaSettings.organization,
        version := PayolaSettings.version,
        scalaVersion := PayolaSettings.scalaVersion
    )

    val scalaToJsSettings = payolaSettings ++ Seq(
        version := ScalaToJsSettings.version
    )

    val scalaTestDependency = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
    val scalaCompilerDependency = "org.scala-lang" % "scala-compiler" % PayolaSettings.scalaVersion

    lazy val payolaProject = Project(
        "Payola",
        file("."),
        settings = payolaSettings
    ).aggregate(
        scalaToJsProject,
        helloWorldProject,
        playBetaProject
    )

    lazy val scalaToJsProject = Project(
        "ScalaToJs",
        file("./ScalaToJs"),
        settings = scalaToJsSettings
    ).aggregate(
        scalaToJsAdaptersProject,
        scalaToJsCompilerProject
    ).dependsOn(
        scalaToJsAdaptersProject,
        scalaToJsCompilerProject
    )

    val adaptersJarName = "adapters_" + PayolaSettings.scalaVersion + "-" + ScalaToJsSettings.version + ".jar";

    lazy val scalaToJsAdaptersProject = Project(
        "Adapters",
        file("./ScalaToJs/Adapters"),
        settings = scalaToJsSettings ++ Seq(
            packageBin <<= (packageBin in Compile) map {
                (jarFile: File) =>
                    IO.copyFile(jarFile, file("./lib/s2js-" + adaptersJarName))
                    jarFile
            },
            compile <<= (compile in Compile).dependsOn(packageBin)
        )
    )

    val compilerJarName = "compiler_" + PayolaSettings.scalaVersion + "-" + ScalaToJsSettings.version + ".jar";

    lazy val scalaToJsCompilerProject = Project(
        "Compiler",
        file("./ScalaToJs/Compiler"),
        settings = scalaToJsSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency, scalaCompilerDependency),
            resolvers ++= Seq(DefaultMavenRepository),

            // The compiler needs to be packaged immediately after the compilation, because other projects that depend
            // on the compiler need the .jar package for their compilation. But it still doesen't work if you perform
            // the compile command anywhere outside of the compiler project.
            packageBin <<= (packageBin in Compile).map {
                jarFile =>
                    IO.copyFile(jarFile, file("./lib/s2js-" + compilerJarName))
                    jarFile
            },
            compile <<= (compile in Compile).dependsOn(packageBin),

            testOptions ++= Seq(
                Tests.Argument("-Dwd=" + file("./ScalaToJs/Compiler/target/tests").absolutePath),
                Tests.Argument("-Dcp=" +
                    "./lib/scala-library-" + PayolaSettings.scalaVersion + ".jar;" +
                    "./lib/s2js-" + adaptersJarName)
            )
        )
    ).dependsOn(
        scalaToJsAdaptersProject
    )

    lazy val helloWorldProject = Project(
        "HelloWorld",
        file("./HelloWorld"),
        settings = payolaSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency),
            resolvers ++= Seq(DefaultMavenRepository)
        )
    )

    lazy val playBetaProject = PlayProject(
        "PlayBeta", // Name of the project.
        PayolaSettings.version, // Version of the project.
        Nil, // Library dependencies.
        file("./PlayBeta") // Path to the project.
    ).settings(
        defaultScalaSettings: _*
    ).aggregate(
        clientProject
    ).dependsOn(
        helloWorldProject,
        clientProject
    )

    lazy val clientProject = Project(
        "Client",
        file("./PlayBeta/Client"),
        settings = payolaSettings ++ Seq(
            // Whole path to the compiler plugin needs to be added, because scala compiler looks for the plugins only
            // in SCALA_HOME.
            scalacOptions += "-Xplugin:" + file("./lib/s2js-" + compilerJarName).absolutePath,
            scalacOptions += "-P:s2js:output:" + file("./PlayBeta/public/javascripts/client").absolutePath
        )
    ).dependsOn(
        scalaToJsProject
    )
}
