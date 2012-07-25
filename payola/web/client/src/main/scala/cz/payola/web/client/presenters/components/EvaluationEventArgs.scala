package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.EventArgs
import cz.payola.common.rdf.Graph
import cz.payola.common.entities.Analysis

class EvaluationEventArgs(target: Analysis, val graph: Option[Graph])
    extends EventArgs[Analysis](target)
