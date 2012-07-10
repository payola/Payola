package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait UserRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val userRepository = new LazyTableRepository[User](schema.users, User) with UserRepository[User]
    {
        def getAllWithNameLike(name: String, pagination: Option[PaginationInfo] = None): Seq[User] = {
            selectWhere(_.name like "%" + name + "%")
        }

        def getByName(name: String): Option[User] = selectWhere(_.name like "%" + name + "%").headOption

        def getByCredentials(name: String, password: String): Option[User] = {
            selectWhere(u => u.name === name and u.password === password).headOption
        }
    }
}
