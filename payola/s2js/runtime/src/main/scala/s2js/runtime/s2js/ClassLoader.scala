package s2js.runtime.s2js

import collection.mutable.ArrayBuffer
import s2js.compiler.javascript

object ClassLoader
{
    val loadedClasses = new ArrayBuffer[String]()
    
    def load(classNames: Seq[String]) {
        val classesToLoad = classNames.filter(!loadedClasses.contains(_))
        val classDeclaration = ClassProvider.get(classesToLoad, loadedClasses)
        evaluateJs(classDeclaration)
    }

    def isLoaded(className: String): Boolean = {
        loadedClasses.contains(className)
    }

    def require(className: String) {
        if (!loadedClasses.contains(className)) {
            throw new RuntimeException("The class " + className + " which hasn't been loaded yet is required.")
        }
    }

    def provide(className: String) {
        if (!loadedClasses.contains(className)) {
            loadedClasses += className
        }
        declareNamespace(className)
    }

    @javascript("eval (classDeclaration);")
    private def evaluateJs(classDeclaration: String) {}

    @javascript("Utils.declareNamespace(namespace);")
    private def declareNamespace(namespace: String) {}
}
