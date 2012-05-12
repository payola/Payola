package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.plugins.query.Construct
import cz.payola.domain.sparql._

object ConstructJoinPlugin extends Construct("Joined SPARQL construct queries", Nil)
{
    override def createInstance(): PluginInstance = {
        throw new UnsupportedOperationException(
            "The ConstructJoinPlugin class has to be instantiated directly using the constructor.")
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        instance match {
            case constructJoinInstance: ConstructJoinPluginInstance => {
                val joinPlugin = constructJoinInstance.join.plugin
                val joinInstance = constructJoinInstance.join.instance
                val joinPropertyUri = joinPlugin.getJoinPropertyUri(joinInstance)
                val joinIsInner = joinPlugin.getIsInner(joinInstance)
                val joinObjectVariable = variableGetter()

                val subjectPlugin = constructJoinInstance.subjectConstruct.plugin
                val subjectInstance = constructJoinInstance.subjectConstruct.instance
                val subjectQuery = subjectPlugin.getConstructQuery(subjectInstance, subject, variableGetter)

                val objectPlugin = constructJoinInstance.objectConstruct.plugin
                val objectInstance = constructJoinInstance.objectConstruct.instance
                val objectQuery = objectPlugin.getConstructQuery(objectInstance, joinObjectVariable, variableGetter)

                if (List(joinPropertyUri, joinIsInner, subjectQuery, objectQuery).forall(_.isDefined)) {
                    val joinTriple = TriplePattern(subject, Uri(joinPropertyUri.get), joinObjectVariable)
                    val joinPattern = GraphPattern(joinTriple)
                    val template = joinTriple +: (subjectQuery.get.template ++ objectQuery.get.template)
                    val subjectPattern = subjectQuery.get.pattern
                    val objectPattern = objectQuery.get.pattern

                    val pattern = if (joinIsInner.get) {
                        joinPattern + subjectPattern + objectPattern
                    } else {
                        GraphPattern(Nil, List(joinPattern + objectPattern), Nil) + subjectPattern
                    }

                    Some(ConstructQuery(template, Some(pattern)))
                } else {
                    None
                }
            }
            case _ => None
        }
    }
}


