package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

/**
 * Provides repository to access persisted users
 */
trait UserRepositoryComponent extends TableRepositoryComponent
{
    /**
     * A repository to access persisted users
     */
    self: SquerylDataContextComponent =>
    lazy val userRepository = new LazyTableRepository[User](schema.users, User)
        with UserRepository
        with NamedEntityTableRepository[User]
    {
        def getAllWithNameLike(name: String, pagination: Option[PaginationInfo] = None): Seq[User] = {
            selectWhere(_.name like "%" + name + "%", pagination)
        }

        def getByEmail(email: String): Option[User] = {
            selectOneWhere(_.email === email)
        }

        def getByCredentials(name: String, password: String): Option[User] = {
            selectOneWhere(u => u.name === name and u.password === password)
        }
    }
}
