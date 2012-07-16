package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.graph.textual.TripleTablePluginView
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.visual.settings._

class GraphPresenter(val viewParentElementId: String) extends Presenter
{
    // TODO
    val visualSetup = new VisualSetup(new VertexSettingsModel, new EdgeSettingsModel, new TextSettingsModel)

    val view = new PluginSwitchView(List(
        new TripleTablePluginView(null)/*,
            new CircleTechnique(visualSetup),
            new TreeTechnique(visualSetup),
            new MinimalizationTechnique(visualSetup),
            new GravityTechnique(visualSetup)*/
    ))

    def initialize() {
        view.render(document.getElementById(viewParentElementId))
    }
}
