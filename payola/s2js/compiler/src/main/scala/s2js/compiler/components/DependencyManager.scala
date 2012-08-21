package s2js.compiler.components

import collection.mutable
import tools.nsc.Global

/**A manager of dependencies in a PackageDef object. */
class DependencyManager(private val packageDefCompiler: PackageDefCompiler)
{
    /**Set of fully qualified names of symbols that have to be declared before the declaration. */
    private val declarationRequiredSymbols = new mutable.HashSet[String]

    /**Set of fully qualified names of symbols that are required during runtime. */
    private val runtimeRequiredSymbols = new mutable.HashSet[String]

    /**Set of fully qualified names of provided symbols. */
    private val providedSymbols = new mutable.HashSet[String]

    /**Structure of the PackageDef object. */
    private var packageDefStructure: PackageDefStructure = null

    /**
     * Compiles the dependencies into a sequence of require, declarationRequire and provide symbols.
     * @param buffer Buffer where the sequence is inserted.
     */
    def compileDependencies(buffer: mutable.ListBuffer[String]) {
        def symbolsToClassLoaderCalls(symbols: mutable.HashSet[String], methodName: String) = {
            symbols.toSeq.sortBy(s => s).map { s =>
                "s2js.runtime.client.core.get().classLoader.%s('%s');\n".format(methodName, s)
            }.mkString
        }

        buffer.insert(0, symbolsToClassLoaderCalls(providedSymbols, "provide"))
        buffer.insert(1, symbolsToClassLoaderCalls(declarationRequiredSymbols -- providedSymbols, "declarationRequire"))
        buffer.insert(2, symbolsToClassLoaderCalls(runtimeRequiredSymbols -- providedSymbols, "require"))
    }

    /**
     * Adds the specified symbol to the provided set.
     * @param symbol The symbol to add.
     */
    def addProvidedSymbol(symbol: Global#Symbol) {
        providedSymbols += packageDefCompiler.getSymbolFullJsName(symbol)
    }

    /**
     * Adds the specified symbol to the required set.
     * @param symbol The symbol to add.
     * @param declarationRequired Whether the symbol should be added to the declaration required set.
     */
    def addRequiredSymbol(symbol: Global#Symbol, declarationRequired: Boolean = false) {
        if (!packageDefCompiler.symbolIsInternal(symbol)) {
            addRequiredSymbolName(packageDefCompiler.getSymbolFullJsName(symbol), declarationRequired)
        }
    }

    /**
     * Adds the specified fully qualified class/object name to the required set.
     * @param symbolFullName Fully qualified name of the symbol to add.
     * @param declarationRequired Whether the symbol should be added to the declaration required set.
     */
    def addRequiredSymbolName(symbolFullName: String, declarationRequired: Boolean = false) {
        (if (declarationRequired) declarationRequiredSymbols else runtimeRequiredSymbols) += symbolFullName
    }

    /**
     * Returns structure of the PackageDef object.
     * @return Structure of the PackageDef object.
     */
    def getPackageDefStructure: PackageDefStructure = {
        packageDefStructure = new PackageDefStructure()
        retrieveStructure(packageDefCompiler.packageDef)
        packageDefStructure
    }

    /**
     * Retrieves structure of the specified AST.
     * @param ast The AST whose structure should be retrieved.
     */
    private def retrieveStructure(ast: Global#Tree) {
        ast match {
            case packageDef: Global#PackageDef => packageDef.children.foreach(retrieveStructure)
            case classDef: Global#ClassDef => retrieveClassDefStructure(classDef)
            case _ =>
        }
    }

    /**
     * Retrieves structure of the specified ClassDef.
     * @param classDef The ClassDef whose structure should be retrieved.
     */
    private def retrieveClassDefStructure(classDef: Global#ClassDef) {
        addProvidedSymbol(classDef.symbol)

        // Remote objects aren't compiled.
        if (packageDefCompiler.getSymbolAnnotations(classDef.symbol, "s2js.compiler.remote").nonEmpty) {
            packageDefStructure.remoteObjects += classDef
        } else {
            // Non-remote object should be added into the dependency graph.
            val name = getStructureKey(classDef.symbol)
            val dependencies = new mutable.HashSet[String]

            packageDefStructure.classDefMap += name -> classDef
            packageDefStructure.classDefDependencyGraph += name -> dependencies

            // If a class is declared inside another class or object, then it depends on the another class/object.
            if (!classDef.symbol.owner.isPackageClass) {
                dependencies += getStructureKey(classDef.symbol.owner)
            }

            // Resolve the dependencies. The class depends on parent classes that are currently compiled and requires
            // the other parent classes.
            classDef.impl.parents.foreach { parentClass =>
                if (!packageDefCompiler.symbolIsInternal(parentClass.symbol)) {
                    if (packageDefCompiler.symbolIsCompiled(parentClass.symbol)) {
                        dependencies += getStructureKey(parentClass.symbol)
                    } else {
                        addRequiredSymbol(parentClass.symbol, declarationRequired = true)
                    }
                }
            }

            // Retrieve structure of child items.
            classDef.impl.body.foreach(retrieveStructure)
        }
    }

    /**
     * Returns key to be used in the PackageDefStructure map and graph as an unique ClassDef identifier.
     * @param classDefSymbol Symbol whose key should be retrieved.
     * @return The key.
     */
    private def getStructureKey(classDefSymbol: Global#Symbol): String = {
        (if (classDefSymbol.isModuleClass) "object" else "class") + " " + classDefSymbol.fullName
    }
}
