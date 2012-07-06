package cz.payola.web.shared

import cz.payola.model.DataFacade
import cz.payola.common.entities.Plugin

@remote object AnalysisBuilderData
{
    val df = new DataFacade

    def getPlugins() : Seq[Plugin] = {
        df.getPlugins()
    }
}
