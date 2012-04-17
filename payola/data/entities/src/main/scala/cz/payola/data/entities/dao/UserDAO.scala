package cz.payola.data.entities.dao

import cz.payola.data.entities._

class UserDAO extends EntityDAO[User](PayolaDB.users)
{
}
