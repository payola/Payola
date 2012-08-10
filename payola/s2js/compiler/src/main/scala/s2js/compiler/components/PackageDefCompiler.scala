package s2js.compiler.components

import scala.tools.nsc.Global
import scala.collection._
import scala.tools.nsc.io.AbstractFile
import reflect.NoType
import s2js.compiler.ScalaToJsException

/** A compiler of PackageDef objects */
class PackageDefCompiler(val global: Global, private val sourceFile: AbstractFile, val packageDef: Global#PackageDef)
{
    /** The dependency manager. */
    val dependencies = new DependencyManager(this)

    /** Packages of the adapter classes. */
    val adapterPackagesNames = List(
        "s2js.adapters.browser",
        "s2js.adapters.dom",
        "s2js.adapters.events",
        "s2js.adapters.html.elements",
        "s2js.adapters.html",
        "s2js.adapters.js",
        "s2js.adapters"
    )

    /** An unique id generator. */
    private var uniqueId = 0

    /**
      * Compiles the PackageDef.
      * @return The compiled JavaScript source.
      */
    def compile(): String = {
        val buffer = new mutable.ListBuffer[String]

        // Retrieve the PackageDef structure.
        val structure = dependencies.getPackageDefStructure

        // Check the structure.
        checkPackageDefStructure(structure)

        // Compile the ClassDef object in the topological order.
        val graph = structure.classDefDependencyGraph
        while (graph.nonEmpty && graph.exists(_._2.isEmpty)) {
            // Sort the compilable ClassDef objects by their keys, so the result is "deterministic".
            val classDefKey = graph.toArray.filter(_._2.isEmpty).map(_._1).sortBy(x => x).head

            // Compile the ClassDef.
            val classDef = structure.classDefMap.get(classDefKey).get
            if (symbolIsCompiled(classDef.symbol)) {
                ClassDefCompiler(this, classDef).compile(buffer)
            }

            // Remove the compiled ClassDef from dependencies of all ClassDef objects, that aren't compiled yet. Also 
            // remove it from the working sets.
            graph.foreach(_._2 -= classDefKey)
            graph -= classDefKey
            structure.classDefMap -= classDefKey
        }

        // If there are some ClassDef objects left, then there is a cyclic dependency.
        if (graph.nonEmpty) {
            throw new ScalaToJsException("Cyclic dependency in the class/object dependency graph involving %s.".format(
                graph.head._1
            ))
        }

        // Compile the dependencies
        dependencies.compileDependencies(buffer)

        buffer.mkString
    }

    /**
      * Checks whether the package structure doesn't violate any preconditions.
      * @param structure The structure to check.
      */
    def checkPackageDefStructure(structure: PackageDefStructure) {
        // Check whether the async methods on remote objects are declared properly.
        structure.remoteObjects.foreach {classDef =>
            val methods = classDef.impl.body.collect { case defDef: Global#DefDef => defDef }
            methods.filter(v => symbolHasAnnotation(v.symbol, "s2js.compiler.async")).foreach(checkAsyncMethod(_))
        }
    }

    /**
      * Returns whether the specified symbol is an internal symbol that mustn't be used in the JavaScript.
      * @param symbol The symbol to check.
      * @return True if the symbol is internal, false otherwise.
      */
    def symbolIsInternal(symbol: Global#Symbol): Boolean = {
        val internalPackageNames = List("scala.reflect") ++ adapterPackagesNames
        val internalTypeNames = Set(
            "java.lang.Object",
            "scala.Any",
            "scala.AnyRef",
            "scala.Equals",
            "scala.Predef",
            "scala.ScalaObject",
            "scala.Serializable"
        )
        val internalTypePatterns = Set(
            """^scala.runtime.AbstractFunction[0-9]+$""".r
        )

        symbol.tpe.baseClasses.exists(_.fullName == "scala.AnyVal") ||
            internalPackageNames.exists(symbol.fullName.startsWith(_)) ||
            internalTypeNames.contains(symbol.fullName) ||
            internalTypePatterns.exists(_.findAllIn(symbol.fullName).nonEmpty)
    }

    /**
      * Returns whether the specified symbol is compiled within the current compilation unit.
      * @param symbol The symbol to check.
      * @return True if the symbol is compiled, false otherwise.
      */
    def symbolIsCompiled(symbol: Global#Symbol): Boolean = {
        symbol.sourceFile != null && symbol.sourceFile.path == sourceFile.path
    }

    /**
      * Returns a list of the specified annotation infos if the specified symbol is annotated with them.
      * @param symbol The symbol which should be annotated.
      * @param annotationTypeName Name of the annotation type.
      * @return The annotation infos.
      */
    def getSymbolAnnotations(symbol: Global#Symbol, annotationTypeName: String): List[Global#AnnotationInfo] = {
        symbol.annotations.filter(_.atp.toString == annotationTypeName)
    }

    /**
      * Returns whether the symbol has the specified annotation.
      * @param symbol The symbol to check.
      * @param annotationTypeName Name of the annotation type.
      * @return True if the symbol has the specified annotation.
      */
    def symbolHasAnnotation(symbol: Global#Symbol, annotationTypeName: String): Boolean = {
        getSymbolAnnotations(symbol, annotationTypeName).nonEmpty
    }

    /**
      * Finds possible package replacement in the symbol full name.
      * @param symbol The symbol in whose name to search for the replacement.
      * @return The replacement Some(oldPackage, newPackage) if such was found, None oterwise.
      */
    def symbolPackageReplacement(symbol: Global#Symbol): Option[(String, String)] = {
        // Ordered by transformation priority (if A is a prefix of B, then the B should be first).
        val packageReplacementMap = mutable.LinkedHashMap(
            "java.lang" -> "scala",
            "s2js.runtime.client.scala" -> "scala"
        ) ++ adapterPackagesNames.map(_ -> "")

        packageReplacementMap.find(r => symbol.fullName.startsWith(r._1))
    }

    /**
      * Returns JavaScript name of a symbol. If the symbol is local, then local JavaScript name is returned. Otherwise
      * fully qualified JavaScript name is returned.
      * @param symbol The symbol whose name should be returned.
      * @return The name.
      */
    def getSymbolJsName(symbol: Global#Symbol): String = {
        if (symbol.isLocal) getSymbolLocalJsName(symbol) else getSymbolFullJsName(symbol)
    }

    /**
      * Returns fully qualified JavaScript name of a symbol.
      * @param symbol The symbol whose name should be returned.
      * @return The name.
      */
    def getSymbolFullJsName(symbol: Global#Symbol): String = {
        var name = symbol.fullName.replace(".this", "").replace(".package", "")

        // Perform the namespace transformation (use the longest matching namespace).
        symbolPackageReplacement(symbol).foreach { r =>
            val (oldPackage, newPackage) = r
            name = name.stripPrefix(oldPackage)
            if (newPackage.isEmpty && name.startsWith(".")) {
                name = name.drop(1)
            }
            name = newPackage + name
        }

        name
    }

    /**
      * Returns JavaScript name of a symbol that should be used in a local scope.
      * @param symbol The symbol whose name should be returned.
      * @return The name.
      */
    def getSymbolLocalJsName(symbol: Global#Symbol): String = {
        val name = symbol.name.toString.trim
        if (symbol.owner.fullName.startsWith("s2js.adapters")) {
            name
        } else {
            getLocalJsName(name, !symbol.isMethod && symbol.isSynthetic)
        }
    }

    /**
      * Returns JavaScript name corresponding to the specified scala name.
      * @param name The scala name that should be converted.
      * @param forcePrefix Whether the name prefix is enforced. Default false.
      * @return The name.
      */
    def getLocalJsName(name: String, forcePrefix: Boolean = false): String = {
        val jsKeywords = List(
            "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "debugger",
            "default", "delete", "do", "double", "else", "enum", "export", "extends", "false", "final", "finally",
            "float", "for", "function", "goto", "if", "implements", "import", "in", "instanceof", "int", "interface",
            "long", "native", "new", "null", "package", "private", "private", "public", "return", "short", "static",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "typeof", "var",
            "void", "volatile", "while", "with"
        )
        val jsDefaultMembers = List(
            "constructor", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "apply", "arguments", "call",
            "prototype", "__class__", "__base__", "length", "charAt", "concat", "indexOf", "lastIndexOf", "join", "pop",
            "push", "reverse", "shift", "slice", "sort", "splice", "unshift", "valueOf"
        )

        // Synthetic symbols get a prefix to avoid name collision with other symbols. Also if the symbol name is a js
        // keyword then it gets the prefix.
        if (forcePrefix || jsKeywords.contains(name) || jsDefaultMembers.contains(name)) {
            "$" + name
        } else {
            name
        }
    }

    /**
      * Returns an unique local name with the specified name prefix.
      * @param namePrefix Prefix of the unique name.
      * @return The name.
      */
    def getUniqueLocalName(namePrefix: String) = {
        getLocalJsName(namePrefix + "$" + getUniqueId(), true)
    }

    /**
      * Returns true if the type is the NoType or the Unit.
      * @param tpe The type to check.
      * @return True if the type is empty, false otherwise.
      */
    def typeIsEmpty(tpe: Global#Type): Boolean = {
        tpe == NoType || tpe.typeSymbol.fullName == "scala.Unit"
    }

    /**
      * Returns whether the type is a Function with the specified number of parameters.
      * @param tpe the type to check.
      * @param parameterCount Count of parameters, the function should have.
      * @return True if the type is a function, false otherwise.
      */
    def typeIsFunction(tpe: Global#Type, parameterCount: Int): Boolean = {
        tpe.typeSymbol.fullName == "scala.Function" + parameterCount
    }

    /**
      * Checks whether the specified async method is valid.
      * @param defDef The method to check.
      */
    private def checkAsyncMethod(defDef: Global#DefDef) {
        val parameters = defDef.symbol.paramss.flatten

        val errorPrefix = "The asynchronous remote method %s ".format(defDef.symbol.fullName.toString)
        if (parameters.length < 2) {
            throw new ScalaToJsException(errorPrefix + "must have at least two parameters.")
        }
        if (!typeIsEmpty(defDef.tpt.tpe)) {
            throw new ScalaToJsException(errorPrefix + "mustn't return anything (the return type must be Unit).")
        }

        val callbacks = parameters.takeRight(2)
        val successCallback = callbacks.head
        val errorCallback = callbacks.last
        if (!List(0, 1).exists(c => typeIsFunction(successCallback.tpe, c)) || !typeIsFunction(errorCallback.tpe, 1)) {
            throw new ScalaToJsException(errorPrefix +
                " must have declared success callback function and error callback function parameters.")
        }

        var parameterTypeName = successCallback.tpe.typeArgs.head.typeSymbol.fullName
        if (parameterTypeName == "scala.Any" || parameterTypeName == "java.lang.Object" ) {
            throw new ScalaToJsException(errorPrefix +
                " must have a success callback whose first parameter is of a specified type (not Any or AnyRef).")
        }

        parameterTypeName = errorCallback.tpe.typeArgs.head.typeSymbol.fullName
        if (parameterTypeName != "java.lang.Throwable") {
            throw new ScalaToJsException(errorPrefix +
                " must have an error callback whose first parameter is of type Throwable.")
        }
    }

    /**
      * Returns an unique id (unique within the packageDef compilation)
      * @return The unique id.
      */
    private def getUniqueId(): Int = {
        uniqueId += 1
        uniqueId
    }
}
