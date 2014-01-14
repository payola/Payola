package cz.payola.web.shared

import s2js.compiler._
import cz.payola.common.rdf._
import cz.payola.domain.entities.User
import scala.Some
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.common.PayolaException
import java.io.ByteArrayOutputStream


@remote
@secured object DataCube
{

    @async def getDSD(evaluationId: String, user: User = null)
        (successCallback: (String => Unit))(failCallback: (Throwable => Unit)) {

        val result = Payola.model.dataCubeModel.queryForCubeDSD(evaluationId)

        successCallback(result)
    }
}
