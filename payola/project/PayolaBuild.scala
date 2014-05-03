import scala.collection.mutable
import mutable.ListBuffer
import scala.io.Source
import scala.tools.nsc.io
import sbt._
import sbt.Keys._
import PlayProject._
import scala.util.matching.Regex

object PayolaBuild extends Build
{
    val compileAndPackage = TaskKey[File]("cp", "Compiles and packages the project in one step.")

    val cleanBeforeTests = TaskKey[Unit]("clean-before-tests", "Cleans the test target directories.")

    /** Common settings of all projects. */
    object Settings
    {
        val scalaVersion = "2.9.2"

        val libDir = file("lib")

        val targetDir = file("lib")
    }

    /** Common settings of the S2Js projects. */
    object S2JsSettings
    {
        val version = "0.2"

        val compilerJarName = "compiler_%s-%s.jar".format(Settings.scalaVersion, version)

        val compilerTestsTarget = file("s2js/compiler/target/tests")

        val compilerTestsClassPath = List(Settings.libDir, Settings.targetDir).flatMap { dir =>
            new io.Directory(dir).files.map(_.path)
        }.mkString(";")
    }

    /** Common settings of the Payola projects. */
    object PayolaSettings
    {
        val version = "1.0"

        val organization = "Payola"
    }

    /** Settings of the web project. */
    object WebSettings
    {
        val serverBaseDir = file("web/server")

        val dependencyDir = serverBaseDir / "public"

        val dependencyFile = dependencyDir / "dependencies"

        val javaScriptsDir = dependencyDir / "javascripts"
    }

    /** Common default settings of all projects. */
    val defaultSettings = Defaults.defaultSettings ++ Seq(
        scalaVersion := Settings.scalaVersion,
        scalacOptions ++= Seq(
            "-deprecation",
            "-unchecked",
            "-encoding", "utf8"
        ),
        libraryDependencies ++= Seq(
            "org.scalatest" %% "scalatest" % "1.6.1" % "test"
        ),
        resolvers ++= Seq(
            DefaultMavenRepository
        ),
        compileAndPackage <<= (packageBin in Compile).map {jarFile: File =>
            IO.copyFile(jarFile, Settings.targetDir / jarFile.name)
            jarFile
        },
        (test in Test) <<= (test in Test).dependsOn(compileAndPackage)
    )

    /** Common default settings of the S2Js projects. */
    val s2JsSettings = defaultSettings ++ Seq(
        version := S2JsSettings.version
    )

    /** Common settings of the Payola projects. */
    val payolaSettings = defaultSettings ++ Seq(
        version := PayolaSettings.version,
        organization := PayolaSettings.organization
    )

    /**
      * The Payola solution. All projects have to be listed in the aggregate method.
      */
    lazy val payolaProject = Project(
        "payola", file("."), settings = payolaSettings
    ).aggregate(
        s2JsProject, scala2JsonProject, commonProject, domainProject, dataProject, modelProject, webProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val s2JsProject = Project(
        "s2js", file("s2js"), settings = s2JsSettings
    ).aggregate(
        s2JsAdaptersProject, s2JsCompilerProject, s2JsRuntimeProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val s2JsAdaptersProject = Project(
        "adapters", file("s2js/adapters"), settings = s2JsSettings
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val s2JsCompilerProject = Project(
        "compiler", file("s2js/compiler"),
        settings = s2JsSettings ++ Seq(
            libraryDependencies ++= Seq(
                "org.scala-lang" % "scala-compiler" % Settings.scalaVersion
            ),
            testOptions ++= Seq(
                Tests.Argument("-Dwd=" + S2JsSettings.compilerTestsTarget.absolutePath),
                Tests.Argument("-Dcp=" + S2JsSettings.compilerTestsClassPath)
            ),
            cleanBeforeTests := {
                new io.Directory(S2JsSettings.compilerTestsTarget).deleteRecursively()
            },
            (test in Test) <<= (test in Test).dependsOn(cleanBeforeTests)
        )
    ).dependsOn(
        s2JsAdaptersProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    /** A project that is compiled to JavaScript using Scala to JavaScript compiler (beside standard compilation). */
    object ScalaToJsProject
    {
        val compilerJar = Settings.targetDir / S2JsSettings.compilerJarName

        def apply(name: String, path: String, outputDir: File, settings: Seq[Project.Setting[_]]) = {
            raw(name, path, outputDir, settings).dependsOn(
                s2JsRuntimeClientProject
            )
        }

        def raw(name: String, path: String, outputDir: File, projectSettings: Seq[Project.Setting[_]]) = {
            Project(
                name, file(path),
                settings = projectSettings ++ Seq(
                    scalacOptions ++= Seq(
                        "-Xplugin:" + compilerJar.absolutePath,
                        "-P:s2js:outputDirectory:" + (outputDir / path).absolutePath
                    ),
                    clean <<= clean.map {_ =>
                        new io.Directory(outputDir / path).deleteRecursively()
                    }
                )
            ).dependsOn(
                s2JsAdaptersProject, s2JsCompilerProject
            )
        }
    }

    lazy val s2JsRuntimeProject = Project(
        "runtime", file("s2js/runtime"), settings = s2JsSettings
    ).aggregate(
        s2JsRuntimeSharedProject, s2JsRuntimeClientProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val s2JsRuntimeSharedProject = ScalaToJsProject.raw(
        "runtime-shared", "s2js/runtime/shared", WebSettings.javaScriptsDir, s2JsSettings
    )

    lazy val s2JsRuntimeClientProject = ScalaToJsProject.raw(
        "runtime-client", "s2js/runtime/client", WebSettings.javaScriptsDir, s2JsSettings
    ).dependsOn(
        s2JsRuntimeSharedProject
    )

    lazy val scala2JsonProject = Project(
        "scala2json", file("scala2json"), settings = payolaSettings
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val commonProject = ScalaToJsProject(
        "common", "common", WebSettings.javaScriptsDir, payolaSettings
    ).dependsOn(scala2JsonProject)

    lazy val domainProject = Project(
        "domain", file("domain"),
        settings = payolaSettings ++ Seq(
            libraryDependencies ++= Seq(
                "org.apache.jena" % "jena-core" % "2.11.1",
                "org.apache.jena" % "jena-arq" % "2.11.1",
                "org.apache.jena" % "jena" % "2.11.0",
                "org.apache.httpcomponents" % "httpclient" % "4.2.4",
                "commons-io" % "commons-io" % "2.4",
                "commons-lang" % "commons-lang" % "2.4"
            )
        )
    ).dependsOn(
        commonProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val dataProject = Project(
        "data", file("data"),
        settings = payolaSettings ++ Seq(
            libraryDependencies ++= Seq(
                "org.squeryl" % "squeryl_2.9.2" % "0.9.5",
                "com.h2database" % "h2" % "1.3.165",
                "mysql" % "mysql-connector-java" % "5.1.18",
                "postgresql" % "postgresql" % "9.1-901.jdbc4",
                "org.apache.derby" % "derby" % "10.8.2.2",
                "org.scalaj" % "scalaj-http_2.9.2" % "0.3.14"
            )
        )
    ).dependsOn(
        commonProject, domainProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val modelProject = Project(
        "model", file("model"),
        settings = payolaSettings ++ Seq(
            libraryDependencies ++= Seq(
                "org.apache.commons" % "commons-lang3" % "3.1",
                "com.fasterxml.jackson.core" % "jackson-core" % "2.3.0-rc1",
                "com.fasterxml.jackson.core" % "jackson-databind" % "2.3.0-rc1",
                "com.fasterxml.jackson.core" % "jackson-annotations" % "2.3.0-rc1"
            )
        )
    ).dependsOn(
        commonProject, domainProject, dataProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val webProject = Project(
        "web", file("web"), settings = payolaSettings
    ).aggregate(
        webSharedProject, webClientProject, webInitializerProject, webServerProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val webSharedProject = ScalaToJsProject(
        "shared", "web/shared", WebSettings.javaScriptsDir,
        settings = payolaSettings ++ Seq(
            libraryDependencies ++= Seq(
                "com.typesafe" % "config" % "0.5.0",
                "org.apache.commons" % "commons-email" % "1.2"
            )
        )
    ).dependsOn(
        commonProject, modelProject
    )

    lazy val webClientProject = ScalaToJsProject(
        "client", "web/client", WebSettings.javaScriptsDir, payolaSettings
    ).dependsOn(
        commonProject, webSharedProject
    )

    lazy val webInitializerProject = Project(
        "initializer", file("web/initializer"), settings = payolaSettings
    ).dependsOn(
        domainProject, dataProject, webSharedProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val webRunnerProject = Project(
        "runner", file("web/runner"), settings = payolaSettings
    ).dependsOn(
        webSharedProject
    ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

    lazy val webServerProject = PlayProject(
        "server", PayolaSettings.version, Nil, path = file("web/server"), mainLang = SCALA
    ).settings(
        compileAndPackage <<= (packageBin in Compile).dependsOn(clean).map { jarFile: File =>
            // Retrieve the dependencies.
            val dependencyExtensions = List("js", "css")
            val dependencyDirectory = new io.Directory(WebSettings.dependencyDir)
            val files = dependencyDirectory.deepFiles.filter(f => dependencyExtensions.contains(f.extension))
                .filterNot(f => f.path.contains("javascripts/lib"))

            val symbolFiles = new mutable.HashMap[String, String]
            val fileProvides = new mutable.HashMap[String, mutable.ArrayBuffer[String]]
            val fileDeclarationRequires = new mutable.HashMap[String, mutable.ArrayBuffer[String]]
            val fileRuntimeRequires = new mutable.HashMap[String, mutable.ArrayBuffer[String]]

            def classLoaderCallRegex(methodName: String): Regex = {
                """s2js\.runtime\.client\.core\.get\(\)\.classLoader\.%s\(\s*['\"]([^'\"]+)['\"]\s*\);""".format(
                    methodName
                ).r
            }
            val provideRegex = classLoaderCallRegex("provide")
            val declarationRequireRegex = classLoaderCallRegex("declarationRequire")
            val runtimeRequireRegex = classLoaderCallRegex("require")

            files.foreach { file =>
                val path = file.toAbsolute.path.toString.replace("\\", "/")
                val fileContent = Source.fromFile(path).getLines.mkString

                provideRegex.findAllIn(fileContent).matchData.foreach {m =>
                    fileProvides.getOrElseUpdate(path, mutable.ArrayBuffer.empty[String]) += m.group(1)
                    symbolFiles += m.group(1) -> path
                }
                declarationRequireRegex.findAllIn(fileContent).matchData.foreach {
                    fileDeclarationRequires.getOrElseUpdate(path, mutable.ArrayBuffer.empty[String]) += _.group(1)
                }
                runtimeRequireRegex.findAllIn(fileContent).matchData.foreach {
                    fileRuntimeRequires.getOrElseUpdate(path, mutable.ArrayBuffer.empty[String]) += _.group(1)
                }
            }

            // Check whether all required symbols are provided.
            val errorFile = (fileDeclarationRequires ++ fileRuntimeRequires).find(_._2.exists(!symbolFiles.contains(_)))
            errorFile.foreach { f =>
                throw new Exception("Dependency '%s' declared in the file '%s' wasn't found.".format(
                    f._2.find(file => !symbolFiles.contains(file)).get.toString,
                    f._1.toString
                ))
            }

            // Create the dependency file.
            val dependencyFile = WebSettings.dependencyFile
            val dependencyBuffer = ListBuffer.empty[String]
            fileProvides.keys.foreach{file =>
                dependencyBuffer += "'%s': [".format(file)
                dependencyBuffer += fileProvides.get(file).flatten.mkString(",")
                dependencyBuffer += "] ["
                dependencyBuffer += fileDeclarationRequires.get(file).flatten.mkString(",")
                dependencyBuffer += "] ["
                dependencyBuffer += fileRuntimeRequires.get(file).flatten.mkString(",")
                dependencyBuffer += "]\n"
            }
            new io.File(dependencyFile).writeAll(dependencyBuffer.mkString)

            jarFile
        },
        clean <<= clean.map {_ =>
            // Delete the dependency file.
            new io.File(WebSettings.dependencyFile).delete()
        }
    ).dependsOn(
        commonProject, modelProject, scala2JsonProject, webSharedProject, webClientProject
    )
}
