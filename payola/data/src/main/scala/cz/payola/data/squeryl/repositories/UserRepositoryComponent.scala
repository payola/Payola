package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait UserRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val userRepository = new TableRepository[User](schema.users, User) with UserRepository[User]
    {
        def getAllWithNameLike(name: String, pagination: Option[PaginationInfo] = None): Seq[User] = {
            val query =
                from(table)(u =>
                    where(u.name like "%" + name + "%")
                    select (u)
                    orderBy (u.name asc)
                )

            evaluateCollectionResultQuery(query, pagination)
        }

        def getByName(name: String): Option[User] = {
            val query = table.where(u => u.name === name)

            evaluateSingleResultQuery(query)
        }

        def getByCredentials(name: String, password: String): Option[User] = {
            val query = table.where(u => u.name === name and u.password === password)

            evaluateSingleResultQuery(query)
        }
    }
}
