package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import scala.collection.immutable
import cz.payola.domain.entities.analyses._
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses.plugins.SparqlQuery

sealed class ConcreteSparqlQuery(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def this() = this("SPARQL Query", 1, List(new StringParameter("Query", "")), IDGenerator.newId)

    def getQuery(instance: PluginInstance): String = {
        usingDefined(instance.getStringParameter("Query"))(query => query)
    }
}
