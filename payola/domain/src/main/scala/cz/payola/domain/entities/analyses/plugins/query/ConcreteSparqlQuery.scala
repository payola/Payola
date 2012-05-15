package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import scala.collection.immutable
import cz.payola.domain.entities.analyses.{Plugin, PluginInstance}
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses.plugins.SparqlQuery

sealed class ConcreteSparqlQuery(
    name: String = "SPARQL Query",
    inputCount: Int = 1,
    parameters: immutable.Seq[Plugin#ParameterType] = List(new StringParameter("Query", "")),
    id: String = IDGenerator.newId)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def getQuery(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("Query")
    }
}
