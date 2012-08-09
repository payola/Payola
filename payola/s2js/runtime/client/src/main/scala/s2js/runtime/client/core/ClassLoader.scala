package s2js.runtime.client.core

import s2js.adapters.js

class ClassLoader
{
    val loadedClasses = new js.Array[String]

    /**
     * Returns whether the specified class is already loaded.
     */
    def isLoaded(className: String): Boolean = loadedClasses.indexOf(className) != -1

    /**
     * Checks whether the specified class has already been declared.
     */
    def declarationRequire(className: String) {
        if (!isLoaded(className)) {
            throw new RuntimeException("The class " + className + " which hasn't been declared yet is required.")
        }
    }

    /**
     * Nothing can be proven about the required class since it can be declared later.
     */
    def require(className: String) {}

    /**
     * Should be called before declaration of a class. Declares the class object path.
     */
    def provide(className: String) {
        if (!isLoaded(className)) {
            loadedClasses.push(className)
            declareObject(className)
        }
    }
}
