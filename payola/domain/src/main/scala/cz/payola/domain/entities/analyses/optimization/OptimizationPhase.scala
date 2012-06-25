package cz.payola.domain.entities.analyses.optimization

/**
  * A phase of analysis optimization.
  */
trait OptimizationPhase
{
    /**
      * Runs the optimization phase on the specified analysis.
      * @param analysis The analysis to run the optimization phase on.
      * @return The analysis optimized using the phase.
      */
    def run(analysis: OptimizedAnalysis): OptimizedAnalysis
}
