package cz.payola.data.dao

import cz.payola.data._
import cz.payola.data.entities.User
import org.squeryl.PrimitiveTypeMode._

trait UserDAOComponent
{
    self: SquerylDataContextComponent =>

    lazy val userDAO = new UserDAO

    class UserDAO extends EntityDAO[User](schema.users) with DAO[User]
{
    /**
      * Searches for all [[cz.payola.data.entities.User]]s, whose username CONTAINS specified name.
      * Result may be paginated.
      *
      * @param name - username (or just its part) that must appear in users username
      * @param pagination - Optionally specified pagination of result
      * @return Return collection of [[cz.payola.data.entities.User]]s
      */
    def findByUsername(name: String, pagination: Option[PaginationInfo] = None): Seq[User] = {
        val query =
            from(table)(u =>
                where(u.name like "%" + name + "%")
                select (u)
                orderBy (u.name asc)
            )

        evaluateCollectionResultQuery(query, pagination)
    }

    /**
      * Finds [[cz.payola.data.entities.User]] with specified username.
      *
      * @param username - username of user to find
      * @return Returns Option([[cz.payola.data.entities.User]]) if found, None otherwise
      */
    def getUserByUsername(username: String): Option[User] = {
        val query = table.where(u => u.name === username)

        evaluateSingleResultQuery(query);
    }

    /**
      * Finds [[cz.payola.data.entities.User]] with specified username and password.
      *
      * @param username - username of user
      * @param password - encrypted password of user
      * @return Returns Option([[cz.payola.data.entities.User]]) if found, None otherwise
      */
    def getUserByCredentials(username: String, password: String): Option[User] = {
        val query = table.where(u => u.name === username and u.password === password)

        evaluateSingleResultQuery(query);
    }

    /**
      * Inserts or updates [[cz.payola.common.entities.User]].
      *
      * @param u - user to insert or update
      * @return Returns persisted [[cz.payola.data.entities.User]]
      */
    def persist(u: cz.payola.common.entities.User): User = {
        super.persist(User(u))
    }
}
}
