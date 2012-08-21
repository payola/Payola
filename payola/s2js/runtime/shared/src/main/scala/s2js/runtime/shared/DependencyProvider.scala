package s2js.runtime.shared

import collection.mutable
import io.Source
import s2js.compiler.remote
import scala.util.matching.Regex

@remote object DependencyProvider
{
    var dependencyFile = new java.io.File("./public/dependencies")

    private val fileDependenciesRegex = """'(.+)': \[(.*)\] \[(.*)\] \[(.*)\]""".r

    private val provideRegex =
        """s2js\.runtime\.client\.core\.get\(\)\.classLoader\.provide\(\s*['\"]([^'\"]+)['\"]\s*\);""".r

    def get(symbols: Seq[String], symbolsToIgnore: Seq[String]): DependencyPackage = {
        var dependencyPackage = new DependencyPackage("", "", Nil)

        if (dependencyFile != null && dependencyFile.exists) {
            val symbolFiles = new mutable.HashMap[String, String]
            val fileProvides = new mutable.HashMap[String, mutable.Buffer[String]]
            val fileDeclarationRequires = new mutable.HashMap[String, mutable.Buffer[String]]
            val fileRuntimeRequires = new mutable.HashMap[String, mutable.Buffer[String]]

            // Retrieve the dependencies from the dependency file.
            Source.fromFile(dependencyFile).getLines.foreach {line =>
                fileDependenciesRegex.findFirstMatchIn(line).foreach {m =>
                    val path = m.group(1)

                    // Provided symbols.
                    fileProvides += path -> mutable.Buffer.empty[String]
                    m.group(2).split(",").filter(_ != "").foreach {symbol =>
                        symbolFiles += symbol -> path
                        fileProvides(path) += symbol
                    }

                    // Required symbols, but add only those that shouldn't be ignored
                    fileDeclarationRequires += path -> mutable.Buffer.empty[String]
                    fileRuntimeRequires += path -> mutable.Buffer.empty[String]
                    
                    def matchToSymbols(matchValue: String): Seq[String] = {
                        matchValue.split(",").filter(!symbolsToIgnore.contains(_)).filter(_ != "")
                    }
                    matchToSymbols(m.group(3)).foreach(fileDeclarationRequires(path) += _)
                    matchToSymbols(m.group(4)).foreach(fileRuntimeRequires(path) += _)
                }
            }

            // Construct the file dependency graph from the symbol dependency graph.
            val declarationDependencyGraph = fileDeclarationRequires.mapValues(_.map(symbol => symbolFiles(symbol)))
            val runtimeDependencyGraph = fileRuntimeRequires.mapValues(_.map(symbol => symbolFiles(symbol)))

            // Compile the package.
            val processedSymbols = mutable.Buffer.empty[String]
            val processedFiles = mutable.Set.empty[String]
            val visitedFiles = mutable.Set.empty[String]
            val filesToProcess = mutable.Set.empty[String]
            val javaScriptBuffer = mutable.Buffer.empty[String]
            val cssBuffer = mutable.Buffer.empty[String]

            def processFile(path: String) {
                if (!processedFiles.contains(path)) {
                    if (visitedFiles.contains(path)) {
                        throw new DependencyException("Cycle in dependencies detected. Check file '%s'.".format(path))
                    }
                    visitedFiles += path

                    // Process the files, that the currently processed file requires declared first.
                    declarationDependencyGraph(path).foreach(processFile(_))

                    // Add the file content to the buffer.
                    val buffer = if (path.endsWith("js")) javaScriptBuffer else cssBuffer
                    buffer ++= Source.fromFile(path).getLines
                    buffer += "\n\n"

                    processedSymbols ++= fileProvides(path)
                    processedFiles += path
                    visitedFiles -= path

                    // Prepare the runtime dependencies for processing.
                    runtimeDependencyGraph(path).foreach(filesToProcess += _)
                }

                filesToProcess -= path
            }

            // Process the files corresponding to the required symbols and create the dependency package.
            symbols.flatMap(symbolFiles.get(_)).foreach(processFile(_))

            // Append runtime dependencies of the processed files.
            while (filesToProcess.nonEmpty) {
                processFile(filesToProcess.head)
            }

            val javaScript = javaScriptBuffer.mkString("\n")
            val css = provideRegex.replaceAllIn(cssBuffer.mkString("\n"), "")
            dependencyPackage = new DependencyPackage(javaScript, css, processedSymbols)
        }

        dependencyPackage
    }
}
