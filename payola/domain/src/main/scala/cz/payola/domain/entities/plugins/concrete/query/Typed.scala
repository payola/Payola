package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.common.rdf.Edge

class Typed(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Typed", 1, List(new StringParameter(Typed.typeURIParameter, "", false, false, false, true, Some(0))), IDGenerator.newId)
    }

    def getTypeURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Typed.typeURIParameter)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getTypeURI(instance)) { uri =>
            val triples = List(TriplePattern(subject, Uri(Edge.rdfTypeEdge), Uri(uri)))
            ConstructQuery(GraphPattern(triples))
        }
    }
}

object Typed
{
    val typeURIParameter = "RDF Type URI"
}
