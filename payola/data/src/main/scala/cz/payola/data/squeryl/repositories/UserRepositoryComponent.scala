package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait UserRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val userRepository = new TableRepository[User](schema.users, User)
    {
        /**
          * Searches for all [[cz.payola.data.squeryl.entities.User]]s, whose username CONTAINS specified name.
          * Result may be paginated.
          *
          * @param name - username (or just its part) that must appear in users username
          * @param pagination - Optionally specified pagination of result
          * @return Return collection of [[cz.payola.data.squeryl.entities.User]]s
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
          * Finds [[cz.payola.data.squeryl.entities.User]] with specified username.
          *
          * @param username - username of user to find
          * @return Returns Option([[cz.payola.data.squeryl.entities.User]]) if found, None otherwise
          */
        def getUserByUsername(username: String): Option[User] = {
            val query = table.where(u => u.name === username)

            evaluateSingleResultQuery(query)
        }

        /**
          * Finds [[cz.payola.data.squeryl.entities.User]] with specified username and password.
          *
          * @param username - username of user
          * @param password - encrypted password of user
          * @return Returns Option([[cz.payola.data.squeryl.entities.User]]) if found, None otherwise
          */
        def getUserByCredentials(username: String, password: String): Option[User] = {
            val query = table.where(u => u.name === username and u.password === password)

            evaluateSingleResultQuery(query)
        }
    }
}
