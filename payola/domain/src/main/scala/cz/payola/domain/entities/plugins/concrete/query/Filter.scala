package cz.payola.domain.entities.plugins.concrete.query

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.sparql
import cz.payola.domain.sparql._

class Filter(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Filter", 1, List(
            new StringParameter(Filter.propertyURIParameter, "", false, false, false, true, Some(0)),
            new StringParameter(Filter.operatorParameter, "", false, false, false, false, Some(1)),
            new StringParameter(Filter.valueParameter, "", false, false, false, false, Some(2))
        ), IDGenerator.newId)
    }

    def getPropertyURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Filter.propertyURIParameter)
    }

    def getOperator(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Filter.operatorParameter)
    }

    def getValue(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Filter.valueParameter)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getPropertyURI(instance), getOperator(instance), getValue(instance)) { (u, o, v) =>
            val objectVariable = variableGetter()
            val triples = List(TriplePattern(subject, Uri(u), objectVariable))
            val filters = List(sparql.Filter(objectVariable + " " + o + " " + v))
            ConstructQuery(triples, Some(GraphPattern(triples, filters = filters)))
        }
    }
}

object Filter
{
    val propertyURIParameter = "Property URI"

    val operatorParameter = "Operator"

    val valueParameter = "Value"
}

