package controllers

import cz.payola.model.DataFacade
import cz.payola.data.PayolaDB
import cz.payola.data.entities.User
import play.api.mvc.{Codec, RequestHeader}

/**
  *
  * @author jirihelmich
  * @created 4/14/12 6:27 PM
  * @package controllers
  */

class PayolaController extends Controller
{
    assert(PayolaDB.connect())
    val df = new DataFacade

    def getUser(userName: String): Option[User] = {
        df.getUserByUsername(userName)
    }
}
