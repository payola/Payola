package controllers

import cz.payola.model.DataFacade
import cz.payola.data.entities.PayolaDB
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
    val df = new DataFacade
    PayolaDB.connect()
    PayolaDB.createSchema()

    def getUser(request: RequestHeader) : Option[User] = {
        val email = request.session.get("email")
        if (!email.isDefined)
        {
            None
        }else{
            val user = df.getUserByUsername(email.get)
            user
        }
    }
}
