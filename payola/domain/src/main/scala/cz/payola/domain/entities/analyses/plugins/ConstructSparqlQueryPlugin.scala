package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.Plugin
import cz.payola.domain.entities.analyses.parameters.{StringParameterValue, StringParameter}
import cz.payola.domain.rdf.Graph

class ConstructSparqlQueryPlugin
    extends Plugin("Construct Query",
                            List(new StringParameter("URI", ""), new StringParameter("Operator", ""), new StringParameter("Value", ""))) {


    protected def getStringParameterValueNamed(parameterValues: Seq[ParameterValueType], paramName: String) = {
        val param: ParameterValueType = parameterValues.find({ pv: ParameterValueType => pv.parameter.name == "URI" }).getOrElse(null)
        if (param == null) {
            ""
        }else{
            require(param.isInstanceOf[StringParameterValue])
            param.asInstanceOf[StringParameterValue].value
        }
    }


    protected def getURI(parameterValues: Seq[ParameterValueType]) = {
        getStringParameterValueNamed(parameterValues, "URI")
    }

    protected def getOperator(parameterValues: Seq[ParameterValueType]) = {
        getStringParameterValueNamed(parameterValues, "Operator")
    }

    protected def getValue(parameterValues: Seq[ParameterValueType]) = {
        getStringParameterValueNamed(parameterValues, "Value")
    }

    protected def queryString(parameterValues: Seq[ParameterValueType]): String = {
        // TODO Prefixes?
        "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>\n" +
        "CONSTRUCT { " + getURI(parameterValues) + " " + getOperator(parameterValues) + " " + getValue(parameterValues) + " }"
    }

    def evaluate(inputGraph: Graph, parameterValues: Seq[ParameterValueType], progressReporter: Double => Unit) = {
        val qs = this.queryString(parameterValues)
        inputGraph.executeConstructSPARQLQuery(qs)
    }

}
