package cz.payola.data.entities

import java.util.UUID

class User(id: String, name: String, pwd: String, email: String)
    extends cz.payola.domain.entities.User(name)
{
    override val _id: String = id
    _password = pwd
    _email = email
}
