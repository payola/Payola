package cz.payola.web.shared

import cz.payola.model.DataFacade

@remote object AnalysisBuilderData
{
    def getPlugins = {
        (new DataFacade).getPlugins()
    }
}
