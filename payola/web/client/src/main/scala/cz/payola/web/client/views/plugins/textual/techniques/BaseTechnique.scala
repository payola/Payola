package cz.payola.web.client.views.plugins.textual.techniques

import cz.payola.web.client.views.plugins.textual.TextPlugin
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup

abstract class BaseTechnique(settings: VisualSetup) extends TextPlugin(settings)
{
    def redraw() {
        if(!graphModel.isEmpty && !parentElement.isEmpty) {
            performTechnique()
        }
    }

    def performTechnique()
}
