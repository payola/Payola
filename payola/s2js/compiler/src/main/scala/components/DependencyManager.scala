package s2js.compiler.components

import collection.mutable
import tools.nsc.Global

/** A manager of dependencies in a PackageDef object */
class DependencyManager(private val packageDefCompiler: PackageDefCompiler)
{
    /** Set of fully qualified names of required classes/objects */
    private val requireHashSet = new mutable.HashSet[String]

    /** Set of fully qualified names of provided classes/objects */
    private val provideHashSet = new mutable.HashSet[String]

    /** Structure of the PackageDef object. */
    private var packageDefStructure: PackageDefStructure = null

    /**
      * Compiles the dependencies into a sequence of goog.provide and goog.require statements.
      * @param buffer Buffer where the sequence is inserted.
      */
    def compileDependencies(buffer: mutable.ListBuffer[String]) {
        val nonLocalRequires = requireHashSet.toArray.filter(!provideHashSet.contains(_))
        buffer.insert(0, provideHashSet.toArray.sortBy(x => x).map("goog.provide('%s');\n".format(_)).mkString)
        buffer.insert(1, nonLocalRequires.sortBy(x => x).map("goog.require('%s');\n".format(_)).mkString)
    }

    /**
      * Adds the specified symbol to the provided set.
      * @param symbol The symbol to add.
      */
    def addProvidedSymbol(symbol: Global#Symbol) {
        provideHashSet += packageDefCompiler.getSymbolFullJsName(symbol)
    }

    /**
      * Adds the specified symbol to the required set.
      * @param symbol The symbol to add.
      */
    def addRequiredSymbol(symbol: Global#Symbol) {
        if (!packageDefCompiler.symbolIsInternal(symbol)) {
            addRequiredSymbol(packageDefCompiler.getSymbolFullJsName(symbol))
        }
    }

    /**
      * Adds the specified fully qualified class/object name to the required set.
      * @param symbolFullName Fully qualified name of the symbol to add.
      */
    def addRequiredSymbol(symbolFullName: String) {
        requireHashSet += symbolFullName
    }

    /**
      * Returns structure of the PackageDef object.
      * @return Structure of the PackageDef object.
      */
    def getPackageDefStructure: PackageDefStructure = {
        packageDefStructure = new PackageDefStructure();
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
        val name = getStructureKey(classDef.symbol)
        val dependencies = new mutable.HashSet[String]

        packageDefStructure.classDefMap += name -> classDef
        packageDefStructure.classDefDependencyGraph += name -> dependencies

        // If a class is declared inside another class or object, then it depends on the another class/object.
        if (!classDef.symbol.owner.isPackageClass) {
            dependencies += getStructureKey(classDef.symbol.owner)
        }

        // Resolve the dependencies. The class depends on parent classes that are currently compiled and requires the
        // other parent classes.
        addProvidedSymbol(classDef.symbol)
        classDef.impl.parents.foreach {parentClass =>
            if (!packageDefCompiler.symbolIsInternal(parentClass.symbol)) {
                if (packageDefCompiler.symbolIsCompiled(parentClass.symbol)) {
                    dependencies += getStructureKey(parentClass.symbol)
                } else {
                    addRequiredSymbol(parentClass.symbol)
                }
            }
        }

        // Retrieve structure of child items.
        classDef.impl.body.foreach(retrieveStructure)
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
