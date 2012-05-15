package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.sparql._

class Selection extends Construct("Selection", List(
    new StringParameter("PropertyURI", ""),
    new StringParameter("Operator", ""),
    new StringParameter("Value", "")))
{
    def getPropertyURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("PropertyURI")
    }

    def getOperator(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("Operator")
    }

    def getValue(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("Value")
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        getPropertyURI(instance).flatMap {uri =>
            getOperator(instance).flatMap {operator =>
                getValue(instance).map {value =>
                    val objectVariable = variableGetter()
                    val triples = List(TriplePattern(subject, Uri(uri), objectVariable))
                    val filters = List(Filter(objectVariable + " " + operator + " " + value))
                    ConstructQuery(triples, Some(GraphPattern(triples, filters = filters)))
                }
            }
        }
    }
}
