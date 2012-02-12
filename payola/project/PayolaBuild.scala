import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.tools.nsc.io
import scala.util.matching.Regex
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
        val javascriptsTargetDir = file("web/server/public/javascripts")

        val googleClosureDepsFile = javascriptsTargetDir / "deps.js"
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
        s2JsProject, scala2JsonProject, dataProject, modelProject, webProject
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
                new io.Directory(S2JsSettings.compilerTestsTarget).list.foreach(_.deleteRecursively())
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
                        "-P:s2js:outputDirectory:" + outputDir.absolutePath
                    )
                )
            ).dependsOn(
                s2JsAdaptersProject, s2JsCompilerProject
            )
        }
    }

    lazy val s2JsRuntimeProject = ScalaToJsProject(
        "runtime", file("s2js/runtime"), WebSettings.javascriptsTargetDir, s2JsSettings
    )

    lazy val scala2JsonProject = Project(
        "scala2json", file("scala2json"), settings = payolaSettings
    )

    lazy val dataProject = Project(
        "data", file("data"),
        settings = payolaSettings ++ Seq(
            libraryDependencies ++= Seq(
                "joda-time" % "joda-time" % "2.0",
                "org.apache.jena" % "jena-core" % "2.7.0-incubating"
                //"com.hp.hpl.jena" % "jena" % "2.6.4"
            )
        )
    ).dependsOn(
        scala2JsonProject
    )

    lazy val modelProject = Project(
        "model", file("model"), settings = payolaSettings
    ).dependsOn(
        scala2JsonProject, dataProject
    )

    lazy val webProject = Project(
        "web", file("web"), settings = payolaSettings
    ).aggregate(
        webSharedProject, webClientProject
    )

    lazy val webSharedProject = ScalaToJsProject(
        "shared", file("web/shared"), WebSettings.javascriptsTargetDir, payolaSettings
    )

    lazy val webServerProject = PlayProject(
        "server", PayolaSettings.version, Nil, file("web/server"), SCALA
    ).settings(
        //payolaSettings: _*
    ).dependsOn(
        //webSharedProject
    )

    lazy val webClientProject = ScalaToJsProject(
        "client", file("web/client"), WebSettings.javascriptsTargetDir,
        payolaSettings ++ Seq(
            compileAndPackage <<= compileAndPackage.dependsOn(clean).map {jarFile: File =>
                val targetDirectory = new io.Directory(WebSettings.javascriptsTargetDir)

                // Generate the the google closure dependency file. Doesn't have to be done for google closure library.
                val buffer = new ListBuffer[String]()
                targetDirectory.deepFiles.filter(_.extension == "js").foreach {file =>
                    val fileContent = Source.fromFile(file.path.toString).getLines.mkString
                    val pathRelativeToBase = ".." + file.path.stripPrefix(targetDirectory.path).replace("\\", "/")
                    buffer += "goog.addDependency('%s', [".format(pathRelativeToBase)

                    // Finds all occurances of the regex in the text and produces string in format 'o1', 'o2', ...
                    // where oi is value of the first regex group in the i-th match.
                    def matchedGroupsToString(regex: Regex, text: String): String = {
                        regex.findAllIn(fileContent).matchData.map(m => "'%s'".format(m.group(1))).mkString(", ")
                    }

                    // Provides and requires.
                    buffer += matchedGroupsToString("""goog\.provide\(\s*['\"]([^'\"]+)['\"]\s*\);""".r, fileContent)
                    buffer += "], ["
                    buffer += matchedGroupsToString("""goog\.require\(\s*['\"]([^'\"]+)['\"]\s*\);""".r, fileContent)
                    buffer += "]);\n"
                }
                new io.File(WebSettings.googleClosureDepsFile).writeAll(buffer.mkString)

                jarFile
            }
        )
    ).dependsOn(
        webSharedProject
    )
}
