package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.{PluginInstance, Plugin}
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.parameters.{BooleanParameter, StringParameter}

class LeftJoin extends Plugin("Left Join", 2, List(
    new StringParameter("JoinPropertyURI", ""),
    new BooleanParameter("IsInner", true)))
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        // TODO
        throw new Exception("LeftJoin.evaluated is not implemetned.")
    }
}
