package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Analysis

/**
  * Optimizer of analyses.
  * @param phases Phases of the optimization to perform.
  */
class AnalysisOptimizer(val phases: Seq[OptimizationPhase])
{
    /**
      * Optimizes the specified analysis.
      * @param analysis The analysis to optimize.
      * @return An evaluationally-equivalent optimized analysis.
      */
    def optimize(analysis: Analysis): OptimizedAnalysis = {
        phases.foldLeft[OptimizedAnalysis](new OptimizedAnalysis(analysis))((a, phase) => phase.run(a))
    }
}
