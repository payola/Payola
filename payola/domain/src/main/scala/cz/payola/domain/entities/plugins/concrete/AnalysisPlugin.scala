package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.concrete.query.Construct
import cz.payola.common.entities.Analysis
import cz.payola.domain.IDGenerator

/**
 * A fake plugin with no implementation. It is used to mark a point in the analysis where another analysis should be
 * imported and bound.
 *
 * @author Jiri Helmich
 */
class AnalysisPlugin(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{

    def this(analysis: Analysis, parameters: immutable.Seq[Parameter[_]]) = {
        this("Analysis "+analysis.name, 0, parameters, IDGenerator.newId)
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        throw new Exception("This should be never called.")
    }
}
