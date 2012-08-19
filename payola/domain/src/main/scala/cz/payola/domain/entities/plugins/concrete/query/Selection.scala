package cz.payola.domain.entities.plugins.concrete.query

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.sparql._

class Selection(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Selection", 1, List(
            new StringParameter(Selection.propertyURIParameter, "", false),
            new StringParameter(Selection.operatorParameter, "", false),
            new StringParameter(Selection.valueParameter, "", false)
        ), IDGenerator.newId)
    }

    def getPropertyURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Selection.propertyURIParameter)
    }

    def getOperator(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Selection.operatorParameter)
    }

    def getValue(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Selection.valueParameter)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getPropertyURI(instance), getOperator(instance), getValue(instance)) { (u, o, v) =>
            val objectVariable = variableGetter()
            val triples = List(TriplePattern(subject, Uri(u), objectVariable))
            val filters = List(Filter(objectVariable + " " + o + " " + v))
            ConstructQuery(triples, Some(GraphPattern(triples, filters = filters)))
        }
    }
}

object Selection
{
    val propertyURIParameter = "Property URI"

    val operatorParameter = "Operator"

    val valueParameter = "Value"
}

