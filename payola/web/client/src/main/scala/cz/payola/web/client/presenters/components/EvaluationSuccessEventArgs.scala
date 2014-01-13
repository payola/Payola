package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.EventArgs
import cz.payola.common.rdf.Graph
import cz.payola.common.entities.Analysis

class EvaluationSuccessEventArgs(target: Analysis, val availableTransformators: List[String])
    extends EventArgs[Analysis](target)
