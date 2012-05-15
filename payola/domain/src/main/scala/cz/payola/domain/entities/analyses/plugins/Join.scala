package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.{PluginInstance, Plugin}
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.parameters.{BooleanParameter, StringParameter}
import query.Construct

class Join extends Plugin("Join", 2, List(
    new StringParameter("JoinPropertyURI", ""),
    new BooleanParameter("IsInner", true)))
{
    def getJoinPropertyUri(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("JoinPropertyURI")
    }

    def getIsInner(instance: PluginInstance): Option[Boolean] = {
        instance.getBooleanParameter("IsInner")
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        // TODO
        throw new Exception("Join.evaluate is not implemetned.")
    }
}
