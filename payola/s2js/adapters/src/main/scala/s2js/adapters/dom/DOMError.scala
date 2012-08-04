package s2js.adapters.dom

trait DOMError
{
    val severity: Int

    val message: String

    val `type`: String

    val relatedException: DOMObject

    val relatedData: DOMObject

    val location: DOMLocator
}

object DOMError
{
    val SEVERITY_WARNING = 1

    val SEVERITY_ERROR = 2

    val SEVERITY_FATAL_ERROR = 3
}
