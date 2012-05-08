package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.{PluginInstance, Plugin}
import cz.payola.domain.rdf.Graph

class Union extends Plugin("Union", 2, Nil)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        inputs(0) + inputs(1)
    }
}
