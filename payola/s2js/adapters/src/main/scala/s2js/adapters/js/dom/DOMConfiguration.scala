package s2js.adapters.js.dom

trait DOMConfiguration
{
    val parameterNames: DOMStringList

    def setParameter(name: String, value: DOMUserData)

    def getParameter(name: String): DOMUserData

    def canSetParameter(name: String, value: DOMUserData): Boolean
}
