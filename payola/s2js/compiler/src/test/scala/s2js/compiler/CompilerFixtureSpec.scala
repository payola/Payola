package s2js.compiler

import java.io.File
import org.scalatest.BeforeAndAfterAll
import org.scalatest.fixture.{ConfigMapFixture, FixtureSpec}
import scala.tools.nsc.io
import scala.io.Source

abstract class CompilerFixtureSpec extends FixtureSpec with ConfigMapFixture with BeforeAndAfterAll
{
    var compiler: ScalaToJsCompiler = null

    var workingDirectory = new File("")

    var testId = 0

    override def beforeAll(configMap: Map[String, Any]) {
        val fixtureDirectoyrName = configMap("wd").toString + "/" + this.getClass.getName
        workingDirectory = new File(fixtureDirectoyrName)
        workingDirectory.mkdirs()

        val targetDirectory = new File(fixtureDirectoyrName + "/target")
        targetDirectory.mkdirs()

        // The packages should be itd so we know exactly, where the js file will be placed.
        compiler = new ScalaToJsCompiler(
            configMap("cp").toString,
            targetDirectory.getAbsolutePath,
            workingDirectory.getAbsolutePath,
            false
        )
    }

    def scalaCode(scalaSource: String): Expector = {
        val fileName = workingDirectory.getAbsolutePath + "/Test" + testId
        testId += 1

        // Compile the scala source.
        val scalaFile = new File(fileName + ".scala")
        io.File(scalaFile).writeAll(scalaSource)
        compiler.compileFiles(List(scalaFile.getAbsolutePath))

        // Retrieve content of the javascript file.
        val compiled = Source.fromFile(fileName + ".js").mkString
        new Expector(compiled)
    }

    class Expector(val actual: String)
    {
        def shouldCompileTo(expected: String) {
            if (normalizeWhiteSpace(actual) != normalizeWhiteSpace(expected)) {
                println(">>>EXPECTED>>>" + normalizeWhiteSpace(expected) + "<<<")
                println(">>>ACTUAL  >>>" + normalizeWhiteSpace(actual) + "<<<")
                assert(false)
            }
        }

        def shouldExactlyCompileTo(expected: String) {
            if (actual != expected) {
                println(">>>EXPECTED>>>" + expected + "<<<")
                println(">>>ACTUAL  >>>" + actual + "<<<")
                assert(false)
            }
        }

        private def normalizeWhiteSpace(text: String) = {
            text.replaceAll("""([ ]+|[\n\r\t])""", " ").replaceAll("""[ ]+""", " ").replaceAll("""^[ ]""", "")
        }
    }

}
