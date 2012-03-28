import scala.collection.mutable
import mutable.ListBuffer
import scala.io.Source
import scala.tools.nsc.io
import sbt._
import Keys._
import PlayProject._

object PayolaBuild extends Build
{
    val compileAndPackage = TaskKey[File]("cp", "Compiles and packages the project in one step.")

    val cleanBeforeTests = TaskKey[Unit]("clean-before-tests", "Cleans the test target directories.")

    /** Common settings of all projects. */
    object Settings
    {
        val scalaVersion = "2.9.1"
    }

    /** Common settings of the S2Js projects. */
    object S2JsSettings
    {
        val version = "0.2"

        val compilerJarName = "s2js-compiler_%s-%s.jar".format(Settings.scalaVersion, version)

        val adaptersJarName = "s2js-adapters_%s-%s.jar".format(Settings.scalaVersion, version)

        val compilerTestsTarget = file("s2js/compiler/target/tests")
    }

    /** Common settings of the Payola projects. */
    object PayolaSettings
    {
        val version = "0.1"

        val organization = "Payola"

        val libDir = file("lib")
    }

    /** Settings of the web project. */
    object WebSettings
    {
        val serverBaseDir = file("web/server")

        val javaScriptsDir = serverBaseDir / "public/javascripts"

        val dependencyFile = javaScriptsDir / "dependencies"

        val compiledJavaScriptsDir = javaScriptsDir / "compiled"

        val bootstrapFileName = "bootstrap.js"
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
        compileAndPackage <<= (packageBin in Compile),
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
        s2JsProject, scala2JsonProject, commonProject, dataProject, modelProject, webProject
    )

    lazy val s2JsProject = Project(
        "s2js", file("s2js"), settings = s2JsSettings
    ).aggregate(
        s2JsAdaptersProject, s2JsCompilerProject, s2JsRuntimeProject
    )

    lazy val s2JsAdaptersProject = Project(
        "adapters", file("s2js/adapters"),
        settings = s2JsSettings ++ Seq(
            compileAndPackage <<= compileAndPackage.map {jarFile: File =>
                IO.copyFile(jarFile, PayolaSettings.libDir / S2JsSettings.adaptersJarName)
                jarFile
            }
        )
    )

    lazy val s2JsCompilerProject = Project(
        "compiler", file("s2js/compiler"),
        settings = s2JsSettings ++ Seq(
            libraryDependencies ++= Seq(
                "org.scala-lang" % "scala-compiler" % Settings.scalaVersion
            ),
            testOptions ++= Seq(
                Tests.Argument("-Dwd=" + S2JsSettings.compilerTestsTarget.absolutePath),
                Tests.Argument("-Dcp=" + new io.Directory(PayolaSettings.libDir).files.map(_.path).mkString(";"))
            ),
            cleanBeforeTests := {
                new io.Directory(S2JsSettings.compilerTestsTarget).deleteRecursively()
            },
            compileAndPackage <<= compileAndPackage.map {jarFile: File =>
                IO.copyFile(jarFile, PayolaSettings.libDir / S2JsSettings.compilerJarName)
                jarFile
            },
            (test in Test) <<= (test in Test).dependsOn(cleanBeforeTests)
        )
    ).dependsOn(
        s2JsAdaptersProject
    )

    /** A project that is compiled to JavaScript using Scala to JavaScript compiler (beside standard compilation). */
    object ScalaToJsProject
    {
        val compilerJar = file("lib/" + S2JsSettings.compilerJarName)

        val adaptersJar = file("lib/" + S2JsSettings.adaptersJarName)

        def apply(name: String, path: File, outputDir: File, projectSettings: Seq[Project.Setting[_]]) = {
            Project(
                name, path,
                settings = projectSettings ++ Seq(
                    scalacOptions ++= Seq(
                        "-Xplugin:" + compilerJar.absolutePath,
                        "-P:s2js:outputDirectory:" + (outputDir / name).absolutePath
                    ),
                    clean <<= clean.map {_ =>
                        new io.Directory(outputDir / name).deleteRecursively()
                    }
                )
            ).dependsOn(
                s2JsAdaptersProject, s2JsCompilerProject
            )
        }
    }

    lazy val s2JsRuntimeProject = ScalaToJsProject(
        "runtime", file("s2js/runtime"), WebSettings.javaScriptsDir, s2JsSettings
    )

    lazy val scala2JsonProject = Project(
        "scala2json", file("scala2json"), settings = payolaSettings
    )

    lazy val commonProject = ScalaToJsProject(
        "common", file("common"), WebSettings.javaScriptsDir, payolaSettings
    ).dependsOn(scala2JsonProject)

    lazy val dataProject = Project(
        "data", file("data"),
        settings = payolaSettings ++ Seq(
            libraryDependencies ++= Seq(
                "org.apache.jena" % "jena-core" % "2.7.0-incubating"
            )
        )
    ).dependsOn(
        scala2JsonProject, commonProject
    )

    lazy val modelProject = Project(
        "model", file("model"), settings = payolaSettings
    ).dependsOn(
        scala2JsonProject, commonProject, dataProject
    )

    lazy val webProject = Project(
        "web", file("web"), settings = payolaSettings
    ).aggregate(
        webSharedProject, webClientProject, webServerProject
    )

    lazy val webSharedProject = ScalaToJsProject(
        "shared", file("web/shared"), WebSettings.javaScriptsDir / "shared", payolaSettings
    ).dependsOn(
        commonProject, dataProject
    )

    lazy val webClientProject = ScalaToJsProject(
        "client", file("web/client"), WebSettings.javaScriptsDir, payolaSettings
    ).dependsOn(
        commonProject, webSharedProject, s2JsRuntimeProject
    )

    lazy val webServerProject = PlayProject(
        "server", PayolaSettings.version, Nil, path = file("web/server"), mainLang = SCALA
    ).settings(
        compileAndPackage <<= (packageBin in Compile).dependsOn(clean).map {jarFile: File =>
            // Retrieve the dependencies.
            val files = new io.Directory(WebSettings.javaScriptsDir).deepFiles.filter(_.extension == "js")
            val symbolFiles = new mutable.HashMap[String, String]
            val fileProvidedSymbols = new mutable.HashMap[String, mutable.ArrayBuffer[String]]
            val fileRequiredSymbols = new mutable.HashMap[String, mutable.ArrayBuffer[String]]

            val provideRegex = """s2js\.ClassLoader\.provide\(\s*['\"]([^'\"]+)['\"]\s*\);""".r
            val requireRegex = """s2js\.ClassLoader\.require\(\s*['\"]([^'\"]+)['\"]\s*\);""".r
            files.foreach {file =>
                val path = file.toAbsolute.path.toString
                fileProvidedSymbols += path -> new mutable.ArrayBuffer[String]
                fileRequiredSymbols += path -> new mutable.ArrayBuffer[String]

                val fileContent = Source.fromFile(path).getLines.mkString
                provideRegex.findAllIn(fileContent).matchData.foreach {m =>
                    fileProvidedSymbols(path) += m.group(1)
                    symbolFiles += m.group(1) -> path
                }
                requireRegex.findAllIn(fileContent).matchData.foreach(fileRequiredSymbols(path) += _.group(1))
            }
            
            // Check whether all required symbols are provided.
            val errorFile = fileRequiredSymbols.find(_._2.exists(file => !symbolFiles.contains(file)))
            if (errorFile.isDefined) {
                throw new Exception("Dependency '%s' declared in the file '%s' wasn't found.".format(
                    errorFile.get._2.find(file => !symbolFiles.contains(file)).get.toString,
                    errorFile.get._1.toString
                ))
            }

            // Create the dependency file.
            val dependencyFile = WebSettings.dependencyFile
            val dependencyBuffer = ListBuffer.empty[String]
            fileProvidedSymbols.keys.foreach{file =>
                dependencyBuffer += "'%s': [".format(file)
                dependencyBuffer += fileProvidedSymbols(file).mkString(",")
                dependencyBuffer += "] ["
                dependencyBuffer += fileRequiredSymbols(file).mkString(",")
                dependencyBuffer += "]\n"
            }
            new io.File(dependencyFile).writeAll(dependencyBuffer.mkString)

            // Construct the file dependency graph from the symbol dependency graph.
            val fileDependencyGraph = fileRequiredSymbols.mapValues(_.map(o => symbolFiles(o)))
            
            // Compile the bootstrap file.
            val processedFiles = new mutable.HashSet[String]
            val visitedFiles = new mutable.HashSet[String]
            var buffer = new ListBuffer[String]
            def processFile(file: String) {
                if (!processedFiles.contains(file)) {
                    if (visitedFiles.contains(file)) {
                        throw new Exception("A cycle in JavaScript file dependencies detected. " +
                            "Check the file '%s'.".format(file))
                    }
                    visitedFiles += file

                    fileDependencyGraph(file).foreach(processFile(_))

                    val name = file.stripPrefix(WebSettings.javaScriptsDir.absolutePath.toString).replace("\\", "/")
                    buffer ++= Source.fromFile(file).getLines
                    buffer += "\n\n"

                    visitedFiles -= file
                    processedFiles += file
                }
            }

            val compiledBootstrapFile = new io.File(WebSettings.compiledJavaScriptsDir / WebSettings.bootstrapFileName)
            processFile((WebSettings.javaScriptsDir / WebSettings.bootstrapFileName).absolutePath.toString)
            compiledBootstrapFile.parent.jfile.mkdirs()
            compiledBootstrapFile.writeAll(buffer.mkString("\n"))

            jarFile
        },
        clean <<= clean.map {_ =>
            // Delete all compiled scripts.
            new io.Directory(WebSettings.compiledJavaScriptsDir).deleteRecursively()

            // Delete the dependency file.
            new io.File(WebSettings.dependencyFile).delete()
        }
    ).dependsOn(
        commonProject, webSharedProject, webClientProject, scala2JsonProject, dataProject
    )
}
