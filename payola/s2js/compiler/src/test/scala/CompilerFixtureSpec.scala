package s2js.compiler

import java.io._

import scala.io.Source

import org.scalatest.BeforeAndAfterAll
import org.scalatest.fixture.{ConfigMapFixture, FixtureSpec}

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

    def scalaCode(scalaSource: String): Expector = {
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
        def shouldCompileTo(expected: String) {
            if (normalizeWhiteSpace(actual) != normalizeWhiteSpace(expected)) {
                println(normalizeWhiteSpace(expected))
                println(normalizeWhiteSpace(actual))
                assert(false)
            }
        }

        private def normalizeWhiteSpace(text: String) = {
            text.replaceAll("""([ ]{2,}|[\n\r])""", "")
        }
    }

}
