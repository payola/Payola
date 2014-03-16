package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.web.shared.Payola
import cz.payola.common.entities.EmbeddingDescription
import cz.payola.domain.entities.User

@remote object EmbeddingDescriptionManager
{

    @async def wft (successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {
        successCallback(true)
    }

    @async def setViewPlugin(id: String, visualPlugin: String)
        (successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {

        successCallback(Payola.model.embeddingDescriptionModel.setViewPlugin(id, visualPlugin).isDefined)
    }
}
