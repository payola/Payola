package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.parameters.{BooleanParameter, StringParameter}
import scala.collection.immutable
import cz.payola.domain.entities.analyses._
import cz.payola.domain.IDGenerator

class Join(
    name: String = "Join",
    inputCount: Int = 2,
    parameters: immutable.Seq[Parameter[_]] = List(
        new StringParameter("JoinPropertyURI", ""),
        new BooleanParameter("IsInner", true)),
    id: String = IDGenerator.newId)
    extends Plugin(name, inputCount, parameters, id)
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
