package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.Plugin
import cz.payola.domain.entities.analyses.parameters.{StringParameterValue, StringParameter}
import cz.payola.domain.rdf.Graph

class SelectSparqlPlugin
    extends Plugin("Construct Query", List(new StringParameter("PropertyNames", ""))) {


    protected def getStringParameterValueNamed(parameterValues: Seq[ParameterValueType], paramName: String) = {
        val param: ParameterValueType = parameterValues.find({ pv: ParameterValueType => pv.parameter.name == "URI" }).getOrElse(null)
        if (param == null) {
            ""
        }else{
            require(param.isInstanceOf[StringParameterValue])
            param.asInstanceOf[StringParameterValue].value
        }
    }


    protected def getPropertyNames(parameterValues: Seq[ParameterValueType]) = {
        getStringParameterValueNamed(parameterValues, "PropertyNames")
    }

    protected def queryString(parameterValues: Seq[ParameterValueType]): String = {
        // TODO Prefixes?
        "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>\n" +
            "SELECT " + getPropertyNames(parameterValues)
    }

    def evaluate(inputGraph: Graph, parameterValues: Seq[ParameterValueType], progressReporter: Double => Unit) = {
        val qs = this.queryString(parameterValues)
        inputGraph.executeSelectSPARQLQuery(qs)
    }

}
