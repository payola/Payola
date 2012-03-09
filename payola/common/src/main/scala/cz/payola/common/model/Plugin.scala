package cz.payola.common.model

trait Plugin extends NamedModelObject {

    def addParameter(p: Parameter[_])
    def containsParameter(p: Parameter[_]): Boolean
    def numberOfParameters: Int
    def parameterAtIndex(index: Int): Parameter[_]
    def parameters: List[Parameter[_]]
    def removeParameter(p: Parameter[_])

}
