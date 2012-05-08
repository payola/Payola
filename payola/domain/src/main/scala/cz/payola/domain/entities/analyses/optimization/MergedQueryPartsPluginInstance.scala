package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.plugins.query.{Selection, Projection, Typed}
import cz.payola.domain.entities.analyses.{AnalysisException, PluginInstance}
import collection.immutable

object MergedQueryPartsPluginInstance
{
    def empty: MergedQueryPartsPluginInstance = {
        new MergedQueryPartsPluginInstance(None, Nil, Nil)
    }
}

class MergedQueryPartsPluginInstance(
    val typed: Option[TypedPluginInstance[Typed]],
    val projections: immutable.Seq[TypedPluginInstance[Projection]],
    val selections: immutable.Seq[TypedPluginInstance[Selection]])
    extends PluginInstance(MergedQueryPartsPlugin, Nil)
{
    lazy val projectionPropertyURIs = projections.flatMap(t => t.plugin.getPropertyURIs(t.instance)).flatten

    lazy val selectionPropertyURIs = selections.flatMap(t => t.plugin.getPropertyURI(t.instance))

    lazy val propertyURIs = projectionPropertyURIs ++ selectionPropertyURIs

    lazy val propertyVariables = propertyURIs.zipWithIndex.map(uri => uri._1 -> ("?p" + uri._2)).toMap

    def +(instance: PluginInstance): MergedQueryPartsPluginInstance = {
        instance.plugin match {
            case t: Typed => {
                if (typed.isEmpty) {
                    new MergedQueryPartsPluginInstance(Some(TypedPluginInstance(t, instance)), projections, selections)
                } else {
                    throw new AnalysisException("Cannot add a Typed plugin instance to the merged instance. " +
                        "The merged instance already contains a Typed plugin instance.")
                }
            }
            case p: Projection => {
                new MergedQueryPartsPluginInstance(typed, TypedPluginInstance(p, instance) +: projections, selections)
            }
            case s: Selection => {
                new MergedQueryPartsPluginInstance(typed, projections, TypedPluginInstance(s, instance) +: selections)
            }
            case plugin => {
                throw new AnalysisException("Cannot add an instance of plugin %s to the merged instance.".format(
                    plugin.getClass))
            }
        }
    }
}
