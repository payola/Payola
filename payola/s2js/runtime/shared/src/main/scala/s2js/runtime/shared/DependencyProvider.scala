package s2js.runtime.shared

import collection.mutable
import io.Source
import tools.nsc.io

@remote object DependencyProvider
{
    var dependencyFile = new java.io.File("./public/dependencies")

    private val fileDependenciesRegex = """'(.+)': \[(.*)\] \[(.*)\]""".r

    private val provideRegex = """s2js\.runtime\.client\.ClassLoader\.provide\(\s*['\"]([^'\"]+)['\"]\s*\);""".r

    def get(symbols: Seq[String], symbolsToIgnore: Seq[String]): DependencyPackage = {
        var dependencyPackage = new DependencyPackage("", "", Nil)

        val path = new io.File(dependencyFile).toAbsolute.toString
        if (dependencyFile != null && dependencyFile.exists) {
            val symbolFiles = new mutable.HashMap[String, String]
            val fileProvidedSymbols = new mutable.HashMap[String, mutable.ArrayBuffer[String]]
            val fileRequiredSymbols = new mutable.HashMap[String, mutable.ArrayBuffer[String]]

            // Retrieve the dependencies from the dependency file.
            Source.fromFile(dependencyFile).getLines.foreach {line =>
                fileDependenciesRegex.findFirstMatchIn(line).foreach {m =>
                    val path = m.group(1)

                    // Provided symbols.
                    fileProvidedSymbols += path -> new mutable.ArrayBuffer[String]
                    m.group(2).split(",").filter(_ != "").foreach {symbol =>
                        symbolFiles += symbol -> path
                        fileProvidedSymbols(path) += symbol
                    }

                    // Required symbols, but add only those that shouldn't be ignored
                    fileRequiredSymbols += path -> new mutable.ArrayBuffer[String]
                    m.group(3).split(",").filter(!symbolsToIgnore.contains(_)).filter(_ != "").foreach {symbol =>
                        fileRequiredSymbols(path) += symbol
                    }
                }
            }

            // Construct the file dependency graph from the symbol dependency graph.
            val fileDependencyGraph = fileRequiredSymbols.mapValues(_.map(symbol => symbolFiles(symbol)))

            // Compile the package.
            val processedSymbols = new mutable.ArrayBuffer[String]
            val processedFiles = new mutable.HashSet[String]
            val visitedFiles = new mutable.HashSet[String]
            val javaScriptBuffer = new mutable.ListBuffer[String]
            val cssBuffer = new mutable.ListBuffer[String]

            def processFile(path: String) {
                if (!processedFiles.contains(path)) {
                    if (visitedFiles.contains(path)) {
                        throw new DependencyException("A cycle in file dependencies detected. Check the file '%s'.".format(path))
                    }
                    visitedFiles += path

                    // Process the files, that the currently processed file requires first.
                    fileDependencyGraph(path).foreach {path =>
                        processFile(path)
                    }

                    // Add the file content to the buffer.
                    val buffer = if (path.endsWith("js")) javaScriptBuffer else cssBuffer
                    buffer ++= Source.fromFile(path).getLines
                    buffer += "\n\n"

                    processedSymbols ++= fileProvidedSymbols(path)
                    processedFiles += path
                    visitedFiles -= path
                }
            }

            // Process the files corresponding to the required symbols and create the dependency package.
            symbols.flatMap(symbolFiles.get(_)).foreach(processFile(_))
            val javaScript = javaScriptBuffer.mkString("\n")
            val css = provideRegex.replaceAllIn(cssBuffer.mkString("\n"), "")
            dependencyPackage = new DependencyPackage(javaScript, css, processedSymbols)
        }

        dependencyPackage
    }
}
