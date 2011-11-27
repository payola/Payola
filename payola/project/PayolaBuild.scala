import sbt._
import Keys._
import PlayProject._
import tools.nsc.io.Directory

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
        val adaptersJar = file("./lib/s2js-adapters_" + PayolaSettings.scalaVersion + "-" + version + ".jar");
        val compilerJar = file("./lib/s2js-compiler_" + PayolaSettings.scalaVersion + "-" + version + ".jar");
        val compilerTestsTarget = file("./s2js/compiler/target/tests")
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

    lazy val s2JsAdaptersProject = Project(
        "adapters",
        file("./s2js/adapters"),
        settings = s2JsSettings ++ Seq(
            packageBin <<= (packageBin in Compile).map {
                jarFile =>
                    IO.copyFile(jarFile, S2JsSettings.adaptersJar)
                    jarFile
            },
            compile <<= (compile in Compile).dependsOn(packageBin)
        )
    )

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
                    IO.copyFile(jarFile, S2JsSettings.compilerJar)
                    jarFile
            },
            compile <<= (compile in Compile).dependsOn(packageBin),

            testOptions ++= Seq(
                Tests.Argument("-Dwd=" + S2JsSettings.compilerTestsTarget.absolutePath),
                Tests.Argument("-Dcp=" +
                    new Directory(file("./lib")).deepFiles.mkString(";").replace("\\", "/") + ";"
                )
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
            scalacOptions += "-Xplugin:" + S2JsSettings.compilerJar.getAbsolutePath,
            scalacOptions += "-P:s2js:output:" + file("./web/public/javascripts").getAbsolutePath
        )
    ).dependsOn(
        s2JsProject
    )
}
