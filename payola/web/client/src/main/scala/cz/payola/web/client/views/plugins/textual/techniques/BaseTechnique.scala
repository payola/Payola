package cz.payola.web.client.views.plugins.textual.techniques

import cz.payola.web.client.views.plugins.textual.TextPlugin

abstract class BaseTechnique extends TextPlugin
{
    def redraw() {
        if(!graphModel.isEmpty && !parentElement.isEmpty) {
            performTechnique()
        }
    }

    def performTechnique()
}
