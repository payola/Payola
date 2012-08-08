package s2js.adapters.dom

trait DOMErrorHandler
{
    def handleError(error: DOMError): Boolean
}
