package controllers

import cz.payola.web.shared.Payola
import cz.payola.domain.entities.User

class PayolaController extends Controller
{
    def getUser(userName: String): Option[User] = {
        Payola.model.userModel.getByName(userName)
    }
}
