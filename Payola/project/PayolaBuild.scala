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

    object S2JsSettings
    {
        val version = "0.2"
    }

    val payolaSettings = Defaults.defaultSettings ++ Seq(
        organization := PayolaSettings.organization,
        version := PayolaSettings.version,
        scalaVersion := PayolaSettings.scalaVersion
    )

    val s2JsSettings = payolaSettings ++ Seq(
        version := S2JsSettings.version
    )

    val scalaTestDependency = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
    val scalaCompilerDependency = "org.scala-lang" % "scala-compiler" % PayolaSettings.scalaVersion

    lazy val payolaProject = Project(
        "payola",
        file("."),
        settings = payolaSettings
    ).aggregate(
        s2JsProject,
        modelProject,
        webProject
    )

    lazy val s2JsProject = Project(
        "s2js",
        file("./s2js"),
        settings = s2JsSettings
    ).aggregate(
        s2JsAdaptersProject,
        s2JsCompilerProject
    ).dependsOn(
        s2JsAdaptersProject,
        s2JsCompilerProject
    )

    val adaptersJarName = "adapters_" + PayolaSettings.scalaVersion + "-" + S2JsSettings.version + ".jar";

    lazy val s2JsAdaptersProject = Project(
        "adapters",
        file("./s2js/adapters"),
        settings = s2JsSettings ++ Seq(
            packageBin <<= (packageBin in Compile) map {
                (jarFile: File) =>
                    IO.copyFile(jarFile, file("./lib/s2js-" + adaptersJarName))
                    jarFile
            },
            compile <<= (compile in Compile).dependsOn(packageBin)
        )
    )

    val compilerJarName = "compiler_" + PayolaSettings.scalaVersion + "-" + S2JsSettings.version + ".jar";

    lazy val s2JsCompilerProject = Project(
        "compiler",
        file("./s2js/compiler"),
        settings = s2JsSettings ++ Seq(
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
                Tests.Argument("-Dwd=" + file("./s2js/compiler/target/tests").absolutePath),
                Tests.Argument("-Dcp=" +
                    "./lib/scala-library-" + PayolaSettings.scalaVersion + ".jar;" +
                    "./lib/s2js-" + adaptersJarName)
            )
        )
    ).dependsOn(
        s2JsAdaptersProject
    )

    lazy val modelProject = Project(
        "model",
        file("./model"),
        settings = payolaSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency),
            resolvers ++= Seq(DefaultMavenRepository)
        )
    )

    lazy val webProject = PlayProject(
        "web", // Name of the project.
        PayolaSettings.version, // Version of the project.
        Nil, // Library dependencies.
        file("./web") // Path to the project.
    ).settings(
        defaultScalaSettings: _*
    ).aggregate(
        clientProject
    ).dependsOn(
        modelProject
    )

    lazy val clientProject = Project(
        "client",
        file("./web/client"),
        settings = payolaSettings ++ Seq(
            // Whole path to the compiler plugin needs to be added, because scala compiler looks for the plugins only
            // in SCALA_HOME.
            scalacOptions += "-Xplugin:" + file("./lib/s2js-" + compilerJarName).absolutePath,
            scalacOptions += "-P:s2js:output:" + file("./web/public/javascripts").absolutePath
        )
    ).dependsOn(
        s2JsProject
    )
}
