package s2js

import java.io._

import org.scalatest.BeforeAndAfterAll

import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.util.BatchSourceFile
import scala.tools.nsc.reporters.{ConsoleReporter, Reporter}
import org.scalatest.fixture.{ConfigMapFixture, FixtureSpec}
import util.matching.Regex
import io.Source

abstract class CompilerFixtureSpec extends FixtureSpec with ConfigMapFixture with BeforeAndAfterAll
{
    var compiler: ScalaToJsCompiler = null
    var workingDirectory: File = new File("")
    var testId = 0

    override def beforeAll(configMap: Map[String, Any]) {
        workingDirectory = new File(configMap("wd").toString + "/" + this.getClass.getName)
        workingDirectory.mkdirs()

        // The packages should be ignored so we know exactly, where the js file will be placed.
        compiler = new ScalaToJsCompiler(configMap("cp").toString, workingDirectory.getAbsolutePath, true)
    }

    def expect(scalaSource: String): Expector = {
        // Compile the scala source.
        val scalaFile = new File(workingDirectory.getAbsolutePath + "/Test" + testId + ".scala")
        val writer = new FileWriter(scalaFile)
        writer.write(scalaSource)
        writer.close()
        compiler.compileFiles(List(scalaFile.getAbsolutePath))

        // Retrieve content of the javascript file.
        val compiled = Source.fromFile(workingDirectory.getAbsolutePath + "/Test" + testId + ".js").mkString
        testId += 1

        new Expector(compiled)
    }

    class Expector(val actual: String)
    {
        def toBe(expected: String) {
            assert(normalizeWhiteSpace(actual) == normalizeWhiteSpace(expected))
        }

        private def normalizeWhiteSpace(text: String) = {
            text.replaceAll("""^[ ]+""", "")
            text.replaceAll("""([ ]{2,}|[\n])""", "")
        }
    }
}