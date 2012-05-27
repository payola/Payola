package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.User
import org.squeryl.PrimitiveTypeMode._

class UserDAO extends EntityDAO[User](PayolaDB.users)
{
    def findByUsername(userName: String, offset: Int = 0, count: Int = 0): Seq[User] = {
        // TODO: defaults for count and offset?
        val query =
            from(table)(u =>
                where(u.name like "%" + userName + "%")
                    select (u)
                    orderBy (u.name asc)
            )

        evaluateCollectionResultQuery(query, offset, count)
    }

    def getUserByUsername(username: String): Option[User] = {
        val query = table.where(u => u.name === username)

        evaluateSingleResultQuery(query);
    }

    def getUserByCredentials(username: String, password: String): Option[User] = {
        val query = table.where(u => u.name === username and u.password === password)

        evaluateSingleResultQuery(query);
    }

    def persist(u: cz.payola.common.entities.User): Option[User] = {
        super.persist(User(u))
    }
}
