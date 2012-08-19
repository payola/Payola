package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.sparql._

class Projection(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Projection", 1, List(
            new StringParameter(Projection.propertyURIsParameter, "", true),
            new BooleanParameter(Projection.selectPropertyInfoParameter, false)
        ), IDGenerator.newId)
    }

    def getPropertyURIs(instance: PluginInstance): Option[Seq[String]] = {
        instance.getStringParameter(Projection.propertyURIsParameter).map(_.split("\n").filter(_ != "").toList)
    }

    def getSelectPropertyInfo(instance: PluginInstance): Option[Boolean] = {
        instance.getBooleanParameter(Projection.selectPropertyInfoParameter)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getPropertyURIs(instance), getSelectPropertyInfo(instance)) { (uris, selectInfo) =>
            val patterns = uris.map { uri =>
                val objectVariable = variableGetter()
                val objectPattern = TriplePattern(subject, Uri(uri), objectVariable)
                val objectProperties = GraphPattern.optionalProperties(objectVariable, variableGetter)
                GraphPattern(List(objectPattern), if (selectInfo) objectProperties.toList else Nil)
            }
            ConstructQuery(patterns.fold(GraphPattern.empty)(_ + _))
        }
    }
}

object Projection
{
    val propertyURIsParameter = "Property URIs"

    val selectPropertyInfoParameter = "Select property types and labels"
}
