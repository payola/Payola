package controllers

import cz.payola.model.DataFacade
import cz.payola.data.PayolaDB
import cz.payola.domain.entities.User

class PayolaController extends Controller
{
    PayolaDB.connect()
    val df = new DataFacade

    def getUser(userName: String): Option[User] = {
        df.getUserByUsername(userName)
    }
}
