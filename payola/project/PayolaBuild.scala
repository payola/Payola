import collection.mutable.ListBuffer
import sbt._
import Keys._
import PlayProject._
import scala.io.Source
import scala.tools.nsc.io
import util.matching.Regex

object PayolaBuild extends Build {

    object PayolaSettings {
        val organization = "Payola"

        val version = "0.1"

        val scalaVersion = "2.9.1"
    }

    object S2JsSettings {
        val version = "0.2"

        val adaptersJar = file("lib/s2js-adapters_" + PayolaSettings.scalaVersion + "-" + version + ".jar")

        val compilerJar = file("lib/s2js-compiler_" + PayolaSettings.scalaVersion + "-" + version + ".jar")

        val compilerTestsTarget = file("s2js/compiler/target/tests")
    }

    object WebSettings {
        val javascriptsTarget = file("web/public/javascripts")

        val googleClosureDeps = javascriptsTarget / "deps.js"
    }


    val payolaSettings = Defaults.defaultSettings ++ Seq(
        organization := PayolaSettings.organization,
        version := PayolaSettings.version,
        scalaVersion := PayolaSettings.scalaVersion
    )

    val s2jsSettings = payolaSettings ++ Seq(
        version := S2JsSettings.version
    )

    val scalaTestDependency = "org.scalatest" %% "scalatest" % "1.6.1" % "test"

    val scalaCompilerDependency = "org.scala-lang" % "scala-compiler" % PayolaSettings.scalaVersion
    

    val cleanGen = TaskKey[Unit]("clean-gen", "Deletes generated javascript sources.")

    lazy val payolaProject = Project(
        "payola",
        file("."),
        settings = payolaSettings
    ).aggregate(
        s2jsProject,
        dataProject,
        modelProject,
        rdf2scalaProject,
        webProject
    )

    lazy val s2jsProject = Project(
        "s2js",
        file("./s2js"),
        settings = s2jsSettings
    ).aggregate(
        s2jsAdaptersProject,
        s2jsCompilerProject,
        s2jsRuntimeProject
    )

    lazy val s2jsAdaptersProject = Project(
        "adapters",
        file("s2js/adapters"),
        settings = s2jsSettings ++ Seq(
            packageBin <<= (packageBin in Compile).map {jarFile =>
                IO.copyFile(jarFile, S2JsSettings.adaptersJar)
                jarFile
            },
            compile <<= (compile in Compile).dependsOn(packageBin)
        )
    )

    lazy val s2jsCompilerProject = Project(
        "compiler",
        file("s2js/compiler"),
        settings = s2jsSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency, scalaCompilerDependency),
            resolvers ++= Seq(DefaultMavenRepository),

            scalacOptions ++= Seq("-unchecked", "-deprecation"),
            testOptions ++= Seq(
                Tests.Argument("-Dwd=" + S2JsSettings.compilerTestsTarget.absolutePath),
                Tests.Argument("-Dcp=" + new io.Directory(file("lib")).files.map(_.path).mkString(";"))
            ),

            // Delete the generated test files.
            cleanGen := {
                new io.Directory(S2JsSettings.compilerTestsTarget).list.foreach(_.deleteRecursively())
            },

            clean <<= clean.dependsOn(cleanGen),

            // The compiler needs to be packaged immediately after the compilation, because other projects that depend
            // on the compiler need the .jar package for their compilation. But it still doesen't work if you perform
            // the compile command anywhere outside of the compiler project.
            packageBin <<= (packageBin in Compile).map {jarFile =>
                IO.copyFile(jarFile, S2JsSettings.compilerJar)
                jarFile
            },

            compile <<= (compile in Compile).dependsOn(packageBin),
            (compile in Test) <<= (compile in Test).dependsOn(cleanGen)
        )
    ).dependsOn(
        s2jsAdaptersProject
    )

    lazy val s2jsRuntimeProject = Project(
        "runtime",
        file("s2js/runtime"),
        settings = s2jsSettings ++ Seq(
            scalacOptions += "-Xplugin:" + S2JsSettings.compilerJar.absolutePath,
            scalacOptions += "-P:s2js:outputDirectory:" + WebSettings.javascriptsTarget.absolutePath
        )
    ).dependsOn(
        s2jsAdaptersProject,
        s2jsCompilerProject
    )

    lazy val dataProject = Project(
        "data",
        file("data"),
        settings = payolaSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency),
            resolvers ++= Seq(DefaultMavenRepository)
        )
    )

    lazy val modelProject = Project(
        "model",
        file("model"),
        settings = payolaSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency),
            resolvers ++= Seq(DefaultMavenRepository)
        )
    ).dependsOn(
        dataProject
    )

    lazy val rdf2scalaProject = Project(
        "rdf2scala",
        file("./rdf2scala"),
        settings = payolaSettings ++ Seq(
            libraryDependencies := Seq(scalaTestDependency),
            resolvers ++= Seq(DefaultMavenRepository)
        )
    )

    lazy val webProject = PlayProject(
        "web", // Name of the project.
        PayolaSettings.version, // Version of the project.
        Nil, // Library dependencies.
        file("web") // Path to the project.
    ).settings(
        defaultScalaSettings: _*
    ).aggregate(
        clientProject
    ).dependsOn(
        modelProject
    )

    lazy val clientProject = Project(
        "client",
        file("web/client"),
        settings = payolaSettings ++ Seq(
            scalacOptions += "-Xplugin:" + S2JsSettings.compilerJar.absolutePath,
            scalacOptions += "-P:s2js:outputDirectory:" + WebSettings.javascriptsTarget.absolutePath,

            // Delete the generated directories and js files, but not the internal libraries.
            cleanGen := {
                new io.Directory(WebSettings.javascriptsTarget / "cz").deleteRecursively()
                WebSettings.googleClosureDeps.delete()
            },

            clean <<= clean.dependsOn(cleanGen),

            compile <<= (compile in Compile).dependsOn(clean).map {analysis =>
                val target = WebSettings.javascriptsTarget
                val targetPath = new io.File(target).path

                // Generate the the google closure dependency file. Doesn't have to be done for google closure library.
                val buffer = new ListBuffer[String]()
                new io.Directory(target).deepFiles.filter(_.extension == "js")foreach {file =>
                    val fileContent = Source.fromFile(file.path.toString).getLines.mkString
                    val pathRelativeToBase = ".." + file.path.stripPrefix(targetPath).replace("\\", "/")
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
                new io.File(WebSettings.googleClosureDeps).writeAll(buffer.mkString)

                analysis
            }
        )
    ).dependsOn(
        s2jsAdaptersProject
    )
}
