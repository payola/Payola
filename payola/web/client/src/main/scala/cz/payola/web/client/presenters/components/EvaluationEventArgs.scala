package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.EventArgs
import cz.payola.common.rdf.Graph

class EvaluationEventArgs(target: AnalysisControls, val graph: Option[Graph])
    extends EventArgs[AnalysisControls](target)
