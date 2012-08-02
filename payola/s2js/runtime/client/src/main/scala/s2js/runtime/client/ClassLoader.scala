package s2js.runtime.client

import collection.mutable.ArrayBuffer
import s2js.compiler.javascript
import s2js.runtime.shared.DependencyProvider
import s2js.adapters.js.browser.console
import s2js.runtime.shared.rpc.RpcException

object ClassLoader
{
    val loadedClasses = new ArrayBuffer[String]()

    def load(classNames: Seq[String]) {
        try {
            val classesToLoad = classNames.filter(!loadedClasses.contains(_))
            if (classesToLoad.nonEmpty) {
                val dependencyPackage = DependencyProvider.get(classesToLoad, loadedClasses)

                // Process the dependency package.
                dependencyPackage.providedSymbols.foreach(provide(_))
                if (dependencyPackage.javaScript != "") {
                    s2js.adapters.js.eval(dependencyPackage.javaScript)
                }
                if (dependencyPackage.css != "") {
                    evaluateCss(dependencyPackage.css)
                }
            }
        } catch {
            case e: RpcException => {
                console.log(e.deepStackTrace)
                throw e
            }
        }
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

    @javascript("""
        var head = document.getElementsByTagName('head')[0];
        var style = document.createElement('style');
        var rules = document.createTextNode(css);

        style.type = 'text/css';
        if (style.styleSheet) {
            style.styleSheet.cssText = rules.nodeValue;
        } else {
            style.appendChild(rules);
        }
        head.appendChild(style);
    """)
    private def evaluateCss(css: String) {}

    @javascript("Utils.declareNamespace(namespace);")
    private def declareNamespace(namespace: String) {}
}
