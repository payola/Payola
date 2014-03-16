package cz.payola.web.client.views.graph

import cz.payola.web.client.views.graph.table._
import cz.payola.web.client.views.graph.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.graph.visual.techniques.gravity.GravityTechnique
import cz.payola.web.client.views.graph.visual.ColumnChartPluginView
import cz.payola.web.client.views.graph.sigma.GraphSigmaPluginView
import cz.payola.web.client.views.graph.datacube._
import cz.payola.web.client.views.map._
import cz.payola.web.client.views.d3.packLayout._
import cz.payola.web.client.models.PrefixApplier

object AvailablePluginViews
{
    /**
     * List of available visualization plugins.
     */
    def getPlugins(prefixApplier: Option[PrefixApplier]) = List[PluginView[_]]( //used by server.app.controllers.view.cachestore.list.scala.html
        new TripleTablePluginView(prefixApplier),
        new SelectResultPluginView(prefixApplier),
        new CircleTechnique(prefixApplier),
        new TreeTechnique(prefixApplier),
        new GravityTechnique(prefixApplier),
        new ColumnChartPluginView(prefixApplier),
        new GraphSigmaPluginView(prefixApplier),
        new TimeHeatmap(prefixApplier),
        new Generic(prefixApplier),
        new GoogleMapView(prefixApplier),
        new GoogleHeatMapView(prefixApplier),
        new ArcGisMapView(prefixApplier),
        new PackLayout(prefixApplier),
        new Sunburst(prefixApplier),
        new ZoomableSunburst(prefixApplier),
        new ZoomableTreemap(prefixApplier)
    )
}
