package cz.payola.data.entities.dao

import cz.payola.data.entities.schema.PayolaDB
import cz.payola.data.entities.User
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer

class UserDAO extends EntityDAO[User](PayolaDB.users)
{
    def findByUsername(userName: String, offset: Int = 0, count: Int = 0): Seq[User] = {
        require(offset >= 0, "Offset must be >= 0")
        require(count >=0 , "Count must be >= 0")

        // TODO: equal name or LIKE name?
        // TODO: defaults for count and offset?
        val query =
            from(table)(u =>
                where(u.name === userName)
                select(u)
                orderBy (u.name asc)
            ).page(offset, count)


        transaction {
            val users: ArrayBuffer[User] = new ArrayBuffer[User]()

            for (u <- query) {
                users += u
            }

            users.toSeq
        }
    }

    def getUserByCredentials(username: String, password: String): Option[User] = {
        val query = table.where(u => u.name === username and u.password === password)

        evaluateSingleResultQuery(query);

        /*
        transaction {
            if (query.size == 0) {
                None
            }
            else {
                Some(query.single)
            }
        }
        */
    }


}
