package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.sparql._

class  PropertySelection(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Property Selection", 1, List(
            new StringParameter(PropertySelection.propertyURIsParameter, "", true, false, false, true, Some(0)),
            new BooleanParameter(PropertySelection.selectPropertyInfoParameter, false, Some(1))
        ), IDGenerator.newId)
    }

    def getPropertyURIs(instance: PluginInstance): Option[Seq[String]] = {
        instance.getStringParameter(PropertySelection.propertyURIsParameter).map(_.split("\n").filter(_ != "").toList)
    }

    def getSelectPropertyInfo(instance: PluginInstance): Option[Boolean] = {
        instance.getBooleanParameter(PropertySelection.selectPropertyInfoParameter)
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

object PropertySelection
{
    val propertyURIsParameter = "Property URIs"

    val selectPropertyInfoParameter = "Select type and label of properties"
}
